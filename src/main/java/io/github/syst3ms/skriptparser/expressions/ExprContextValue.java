package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.MatchContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.pattern.PatternElement;
import io.github.syst3ms.skriptparser.pattern.PatternParser;
import io.github.syst3ms.skriptparser.registration.context.ContextValue;
import io.github.syst3ms.skriptparser.registration.context.ContextValue.State;
import io.github.syst3ms.skriptparser.registration.context.ContextValues;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Array;
import java.util.Optional;

public class ExprContextValue<T, C extends TriggerContext> implements Expression<T> {

    private static final String PATTERN = "[the] [(1:(past|previous)|2:(future|next))] [ctx:(context|event)-]<.+>";
    private static final PatternElement CONTEXT_VALUE_PATTERN = PatternParser.parsePattern(PATTERN, new SkriptLogger()).orElseThrow();

    private ContextValue<C, T> info;
    private String value;
    private boolean alone;

    static {
        //noinspection unchecked
        Parser.getMainRegistration().newExpression(ExprContextValue.class, Object.class, false,
                PATTERN)
            .name("Context/Event Value")
            .description("Returns a value from the current context/event.")
            .examples("set {_var} to context-player")
            .since("1.0.0")
            .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Contract("_, _, _ -> fail")
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        boolean alone = !parseContext.hasMark("ctx");
        String toParse = parseContext.getMatches().getFirst().group();
        String value = parseContext.getMatches().getFirst().group();
        State state = switch (parseContext.getNumericMark()) {
            case 0 -> State.PRESENT;
            case 1 -> State.PAST;
            case 2 -> State.FUTURE;
            default -> throw new IllegalArgumentException("Invalid state index: " + parseContext.getNumericMark());
        };


        ParserState parserState = parseContext.getParserState();
        SkriptLogger logger = parseContext.getLogger();

        var matchContext = new MatchContext(CONTEXT_VALUE_PATTERN, parserState, logger);

        for (Class<? extends TriggerContext> ctx : parserState.getCurrentContexts()) {
            for (var info : ContextValues.getContextValues(ctx)) {
                matchContext = new MatchContext(info.getPattern(), parserState, logger);

                // Checking all conditions, so no false results slip through.
                if (info.getPattern().match(value, 0, matchContext) != value.length()) {
                    continue;
                } else if (!info.getUsage().isCorrect(alone)) {
                    if (alone) {
                        logger.error(
                            "The context value matching '" + toParse + "' cannot be used alone",
                            ErrorType.SEMANTIC_ERROR,
                            "Use 'context-" + toParse + "' instead of using just '" + toParse + "' alone"
                        );
                    } else {
                        logger.error(
                            "The context value matching '" + toParse + "' must be used alone",
                            ErrorType.SEMANTIC_ERROR,
                            "Instead of 'context-" + toParse + "', use this context value as '" + toParse + "'"
                        );
                    }
                    return false;
                } else if (state != info.getState()) {
                    logger.error("The time state of this context value (" + state.toString().toLowerCase() + ") is incorrect", ErrorType.SEMANTIC_ERROR);
                    return false;
                } else if (parserState.isRestrictingExpressions() && parserState.forbidsSyntax(ExprContextValue.class)) {
                    logger.setContext(ErrorContext.RESTRICTED_SYNTAXES);
                    logger.error("The enclosing section does not allow the use of context expressions.", ErrorType.SEMANTIC_ERROR);
                    return false;
                }

                this.info = (ContextValue<C, T>) info;
                this.value = value;
                this.alone = alone;
                return true;
            }
        }
        if (!alone) {
            logger.error("No context value matching '" + toParse + "' was found", ErrorType.SEMANTIC_ERROR);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getValues(TriggerContext ctx) {
        if (!this.info.getContext().isAssignableFrom(ctx.getClass())) {
            return (T[]) Array.newInstance(getReturnType(), 0);
        }
        assert this.info.getContext().isInstance(ctx);
        if (this.info.isSingle()) {
            T apply = this.info.getSingleFunction().apply((C) ctx);
            T[] array = (T[]) Array.newInstance(getReturnType(), 1);
            array[0] = apply;
            return array;
        } else {
            return this.info.getListFunction().apply((C) ctx);
        }
    }

    @Override
    public Optional<Class<?>[]> acceptsChange(ChangeMode mode) {
        if (mode == ChangeMode.SET && this.info.canBeSet()) return Optional.of(new Class<?>[]{getReturnType()});
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void change(TriggerContext ctx, ChangeMode changeMode, Object[] changeWith) {
        if (changeMode != ChangeMode.SET) return;

        if (this.info.isSingle()) {
            this.info.getSingleSetterFunction().accept((C) ctx, (T) changeWith[0]);
        } else {
            this.info.getListSetterFunction().accept((C) ctx, (T[]) changeWith);
        }
    }

    @Override
    public boolean isSingle() {
        return this.info.isSingle();
    }

    @Override
    public Class<? extends T> getReturnType() {
        return this.info.getReturnType().getType().getTypeClass();
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return new String[]{"past ", "", "future "}[this.info.getState().ordinal()] +
            (this.alone ? "" : "context-") + this.value;
    }

}
