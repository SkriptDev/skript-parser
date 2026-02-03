package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.base.ConditionalExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

import java.util.concurrent.ThreadLocalRandom;

public class CondExprChance extends ConditionalExpression {

    static {
        Parser.getMainRegistration().newExpression(CondExprChance.class, Boolean.class,
                true, "chance of %number%[1:\\%]")
            .name("Chance")
            .description("Checks if a certain chance has been met.",
                "You can either use a percent `10%` or a decimal `0.1`.")
            .examples("if chance of 10%:",
                "\tkill all players")
            .since("1.0.0")
            .register();
    }

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private Expression<Number> chance;
    private boolean percent;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.chance = (Expression<Number>) expressions[0];
        this.percent = parseContext.getNumericMark() == 1;
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return this.chance.check(ctx, n ->
            this.percent ? RANDOM.nextDouble(1) <= n.doubleValue() / 100 : RANDOM.nextDouble() <= n.doubleValue());
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "chance of " + this.chance.toString(ctx, debug) + (this.percent ? "%" : "");
    }

}
