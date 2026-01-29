package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.properties.ConditionalType;
import io.github.syst3ms.skriptparser.lang.properties.PropertyConditional;

/**
 * Check if a given number is a prime number.
 * This means that for a number {@code n},
 * there does not exist a number {@code a} such that
 * {@code a < n} and {@code n % a = 0}, except for {@code a = 1}.
 *
 * @author Mwexim
 * @name Is Prime
 * @type CONDITION
 * @pattern %numbers% (is|are)[ not|n't] [a] prime [number[s]]
 * @since ALPHA
 */
public class CondExprIsPrime extends PropertyConditional<Number> {
    static {
        Parser.getMainRegistration().newPropertyConditional(CondExprIsPrime.class, "numbers", ConditionalType.BE, "[a] prime [number[s]]")
            .addData(PROPERTY_IDENTIFIER, "prime")
            .name("Is Prime")
            .description("Checks if a number is a prime number.")
            .examples("if 99 is prime:")
            .register();
    }

    @Override
    public boolean check(Number performer) {
        int n = performer.intValue();
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false; // Found a factor other than 1 and n
            }
        }
        return true;
    }

}
