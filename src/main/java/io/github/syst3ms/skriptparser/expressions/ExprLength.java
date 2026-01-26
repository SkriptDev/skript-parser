package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;

import java.math.BigInteger;

/**
 * Length of a string.
 *
 * @name Length
 * @pattern [the] length of %string%
 * @pattern %string%'s length
 * @since ALPHA
 * @author Romitou
 */
public class ExprLength extends PropertyExpression<String, Number> {
    static {
        Parser.getMainRegistration().newPropertyExpression(ExprLength.class, Number.class, "length", "string")
            .name("Length")
            .description("The length of a string.")
            .since("INSERT VERSION")
            .register();
    }

    @Override
    public Number getProperty(String owner) {
        return BigInteger.valueOf(owner.length());
    }
}
