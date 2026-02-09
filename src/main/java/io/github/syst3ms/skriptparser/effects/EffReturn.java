package io.github.syst3ms.skriptparser.effects;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.control.Continuable;
import io.github.syst3ms.skriptparser.lang.control.Finishing;
import io.github.syst3ms.skriptparser.lang.lambda.ReturnSection;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.parsing.SkriptParserException;
import io.github.syst3ms.skriptparser.structures.functions.Function;
import io.github.syst3ms.skriptparser.structures.functions.FunctionContext;
import io.github.syst3ms.skriptparser.types.TypeManager;
import io.github.syst3ms.skriptparser.types.conversions.Converters;
import io.github.syst3ms.skriptparser.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Returns one or more values to a corresponding section. Used with {@link ReturnSection}.
 *
 * @name Return
 * @type EFFECT
 * @pattern return %objects%
 * @since ALPHA
 * @author Syst3ms
 */
public class EffReturn extends Effect {
    static {
        Parser.getMainRegistration().newEffect(EffReturn.class, "return %objects%")
            .name("Return")
            .description("Returns one or more values to a corresponding section, such as in a function.")
            .since("1.0.0")
            .register();
    }
    private boolean isInFunction;

    private ReturnSection<?> section;
    private Expression<?> returned;
    private List<? extends Continuable> sections;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        if (parseContext.getParserState().isDelayed()) {
            parseContext.getLogger().error("Return statements cannot be used after a delay.", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        returned = expressions[0];
        this.sections = parseContext.getParserState().getCurrentSections().stream()
            .filter(sec -> sec instanceof Continuable)
            .map(sec -> (Continuable) sec)
            .collect(Collectors.toList());
        var logger = parseContext.getLogger();
        Optional<Class<? extends TriggerContext>> optionalContext = parseContext.getParserState().getCurrentContexts().stream().findFirst();
        if (optionalContext.isPresent()) {
            Class<? extends TriggerContext> currentContext = optionalContext.get();
            if (currentContext.equals(FunctionContext.class)) {
                isInFunction = true;
                return true;
            }
        }
        var sec = Expression.getLinkedSection(parseContext.getParserState(), ReturnSection.class);
        if (sec.isEmpty()) {
            logger.error("Couldn't find a section matching this return statement", ErrorType.SEMANTIC_ERROR);
            return false;
        }
        section = (ReturnSection<?>) sec.get();
        if (section.isSingle() && !returned.isSingle()) {
            logger.error("Only a single return value was expected, but multiple were given", ErrorType.SEMANTIC_ERROR);
            return false;
        } else if (!Converters.converterExists(returned.getReturnType(), section.getReturnType())) {
            var secType = TypeManager.getByClass(section.getReturnType())
                    .map(t -> StringUtils.withIndefiniteArticle(t.toString(), false))
                    .orElse(section.getReturnType().getName());
            var exprType = TypeManager.getByClass(returned.getReturnType())
                    .map(t -> StringUtils.withIndefiniteArticle(t.toString(), false))
                    .orElseThrow(AssertionError::new);
            logger.error(
                    "Expected " +
                            secType +
                            " return value, but found " +
                            exprType,
                    ErrorType.SEMANTIC_ERROR
            );
            return false;
        }
        if (!section.getReturnType().isAssignableFrom(returned.getReturnType())) {
            // The value is convertible but not in the trivial way
            returned = returned.convertExpression(section.getReturnType())
                               .orElseThrow(() -> new SkriptParserException("Return value should be convertible at this stage"));
        }
        return true;
    }

    @Override
    protected void execute(TriggerContext ctx) {
        /*if (isInFunction) {
            FunctionContext functionContext = (FunctionContext) ctx;
            Function<?> function = functionContext.getOwningFunction();
            function.setReturnValue(returned.getValues(ctx));
        } else throw new UnsupportedOperationException();*/
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<? extends Statement> walk(TriggerContext ctx) {
        this.sections.forEach(sec -> {
            if (sec instanceof Finishing fin) fin.finish();
        });
        if (isInFunction) {
            FunctionContext functionContext = (FunctionContext) ctx;
            Function<?> function = functionContext.getOwningFunction();
            function.setReturnValue(returned.getValues(ctx));
            return Optional.empty(); // stop the trigger
        }
        section.setReturned(returned.getValues(ctx));
        section.step(this);
        return Optional.of(section);
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "return " + returned.toString(ctx, debug);
    }
}
