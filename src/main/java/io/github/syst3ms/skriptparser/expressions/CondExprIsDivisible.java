package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

/**
 * Check if a given number is divisible by another number.
 * Note that when the number is a decimal number,
 * the check will automatically fail.
 *
 * @author Mwexim
 * @name Is Divisible
 * @type CONDITION
 * @pattern %numbers% (is|are)[ not|n't] divisible by %integer%
 * @since ALPHA
 */
public class CondExprIsDivisible extends PropertyConditional<Number> {
    static {
        Parser.getMainRegistration().addPropertyConditional(
            CondExprIsDivisible.class,
            "numbers",
            ConditionalType.BE,
            "divisible by %integer%"
        );
    }

    private Expression<Integer> divider;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        divider = (Expression<Integer>) expressions[1];
        return super.init(expressions, matchedPattern, parseContext);
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return getPerformer().check(ctx, (p) -> this.divider.check(ctx, (d) -> p.intValue() % d == 0));
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return toString(ctx, debug, "divisible by " + divider.toString(ctx, debug));
    }

}
