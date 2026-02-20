package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

/**
 * A shorthand expression for giving things a default value. If the first thing isn't set, the second thing will be returned.
 *
 * @author Olyno
 * @name Default Value
 * @pattern %objects% (otherwise|?) %objects%
 * @since ALPHA
 */
public class ExprDefaultValue implements Expression<Object> {
    static {
        Parser.getMainRegistration().newExpression(ExprDefaultValue.class, Object.class, false,
                "%objects% (otherwise|?) %objects%")
            .name("Default Value")
            .description("Returns the first expression if it's set, otherwise the second expression.")
            .examples("set {_var} to {_otherVarIfSet} otherwise \"world\"",
                "send {_varIfSet} ? \"ruh roh, no message\"")
            .since("1.0.0")
            .register();
    }

    private Expression<Object> firstValue, secondValue;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        firstValue = (Expression<Object>) expressions[0];
        secondValue = (Expression<Object>) expressions[1];
        return true;
    }

    @Override
    public Object[] getValues(TriggerContext ctx) {
        return firstValue.getValues(ctx).length != 0
            ? firstValue.getValues(ctx)
            : secondValue.getValues(ctx);
    }

    @Override
    public boolean isSingle() {
        return firstValue.isSingle() && secondValue.isSingle();
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return firstValue.toString(ctx, debug) + " otherwise " + secondValue.toString(ctx, debug);
    }
}
