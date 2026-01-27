package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;

/**
 * Length of a string.
 *
 * @author Romitou
 * @name Length
 * @pattern [the] length of %string%
 * @pattern %string%'s length
 * @since ALPHA
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
        return owner.length();
    }

}
