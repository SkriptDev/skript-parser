package io.github.syst3ms.skriptparser.lang.base;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.context.ContextValue;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.CollectionUtils;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * An {@link Expression} that corresponds to a contextual value. Each trigger
 * can carry multiple instances of data (called 'context values'). This expression
 * holds a reference to such a value.
 *
 * @param <C> the TriggerContext class
 * @param <T> the Expression's type
 * @author Mwexim
 */
public class ContextExpression<C extends TriggerContext, T> implements Expression<T> {
    private final ContextValue<C, T> info;
    private final String value;
    private final boolean alone;

    public ContextExpression(ContextValue<C, T> info, String value, boolean alone) {
        this.info = info;
        this.value = value;
        this.alone = alone;
    }

    @Override
    @Contract("_, _, _ -> fail")
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getValues(TriggerContext ctx) {
        assert info.getContext().isInstance(ctx);
        if (info.isSingle()) {
            T apply = this.info.getSingleFunction().apply((C) ctx);
            return CollectionUtils.arrayOf(apply);
        } else {
            return info.getListFunction().apply((C) ctx);
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
        return new String[]{"past ", "", "future "}[info.getState().ordinal()] + (alone ? "" : "context-") + value;
    }

}
