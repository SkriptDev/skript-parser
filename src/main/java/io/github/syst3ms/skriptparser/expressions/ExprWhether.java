package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

/**
 * A wrapper that turns a condition into a boolean expression than can be used anywhere.
 *
 * @author Syst3ms
 * @name Whether
 * @pattern whether %=boolean%
 * @since ALPHA
 */
public class ExprWhether implements Expression<Boolean> {
    static {
        Parser.getMainRegistration().newExpression(ExprWhether.class, Boolean.class, true,
                "whether %~=boolean%")
            .name("Whether")
            .description("A wrapper that turns a condition into a boolean expression than can be used anywhere.")
            .since("1.0.0")
            .register();
    }

    private Expression<Boolean> condition;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        condition = (Expression<Boolean>) expressions[0];
        return true;
    }

    @Override
    public Boolean[] getValues(TriggerContext ctx) {
        return condition.getValues(ctx);
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "whether " + condition.toString(ctx, debug);
    }
}
