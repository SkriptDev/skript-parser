package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generate a random number (double) or integer.
 *
 * @author WeeskyBDW
 * @name random number
 * @pattern [a] random integer [strictly] (from|between) %number% (to|and) %number%
 * @pattern [a] random number [strictly] (from|between) %integer% (to|and) %integer%
 * @since ALPHA
 */
public class ExprRandomNumber implements Expression<Number> {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    static {
        Parser.getMainRegistration().newExpression(
                ExprRandomNumber.class,
                Number.class,
                true,
                "[a] random integer (from|between) %integer% (to|and) %integer%",
                "[a] random number (from|between) %number% (to|and) %number%")
            .name("Random Number")
            .description("Generates a random number between two numbers.")
            .examples("set {_random} to random integer between 10 and 20",
                "set {_random} to random number between 10.5 and 20.5")
            .since("1.0.0")
            .register();
    }

    private int pattern;
    private Expression<Number> lowerNumber, maxNumber;
    private boolean isInteger;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext context) {
        this.pattern = matchedPattern;
        lowerNumber = (Expression<Number>) expressions[0];
        maxNumber = (Expression<Number>) expressions[1];
        isInteger = matchedPattern == 0;
        return true;
    }

    @Override
    public Number[] getValues(TriggerContext ctx) {
        Optional<? extends Number> low = this.lowerNumber.getSingle(ctx);
        Optional<? extends Number> high = this.maxNumber.getSingle(ctx);
        if (low.isEmpty() || high.isEmpty()) return null;

        if (this.pattern == 1) {
            return new Number[]{RANDOM.nextDouble(low.get().doubleValue(), high.get().doubleValue() + 1)};
        } else {
            return new Number[]{RANDOM.nextInt(low.get().intValue(), high.get().intValue() + 1)};
        }
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "a random " + (isInteger ? "integer " : "number ") + "between " + lowerNumber.toString(ctx, debug) + " and " + maxNumber.toString(ctx, debug);
    }

}
