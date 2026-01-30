package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.properties.PropertyExpression;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.color.Color;

/**
 * Certain color values of a given color.
 *
 * @author Mwexim
 * @name Date Values
 * @type EXPRESSION
 * @pattern [the] (hex[adecimal]|red|green|blue|alpha) value of %color%
 * @pattern %color%'[s] (hex[adecimal]|red|green|blue|alpha)
 * @since ALPHA
 */
public class ExprColorValues extends PropertyExpression<Color, Object> {
    static {
        Parser.getMainRegistration().newPropertyExpression(ExprColorValues.class, Object.class,
                "(0:hex[adecimal]|1:red|2:green|3:blue|4:alpha) value", "colors")
            .name("Color Value")
            .description("The value of a certain color component.")
            .since("1.0.0")
            .register();
    }

    private int mark;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        this.mark = parseContext.getNumericMark();
        return super.init(expressions, matchedPattern, parseContext);
    }

    @Override
    public Object getProperty(Color owner) {
        return switch (this.mark) {
            case 0 -> owner.getHex();
            case 1 -> owner.getRed();
            case 2 -> owner.getGreen();
            case 3 -> owner.getBlue();
            case 4 -> owner.getAlpha();
            default -> throw new IllegalStateException();
        };
    }

    @Override
    public Class<?> getReturnType() {
        return this.mark == 0 ? String.class : Integer.class;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return toString(ctx, debug, new String[]{"hex", "red", "green", "blue", "alpha"}[this.mark] + " value");
    }

}
