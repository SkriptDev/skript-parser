package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.color.Color;

/**
 * A color specified by its RGB value.
 * Each value, this means red, green and blue, can be a range from 0 to 255.
 * Any values outside of those ranges will result in no color to be created.
 * It is also possible to add the alpha value, which corresponds to the transparency.
 * If the argument size is not exactly 3 or 4, no color will be created.
 *
 * @author Mwexim
 * @name Color from RGB
 * @type EXPRESSION
 * @pattern [the] color (from|of) [the] rgb[a] [value] %integers%
 * @since ALPHA
 */
public class ExprColorFromRGB implements Expression<Color> {
    static {
        Parser.getMainRegistration().newExpression(ExprColorFromRGB.class, Color.class, true,
                "[the] color (from|of) [the] rgb[a] [value] %integers%")
            .name("Color from RGB")
            .description("Creates a color from its RGB values.")
            .since("1.0.0")
            .register();
    }

    private Expression<Integer> rgb;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        rgb = (Expression<Integer>) expressions[0];
        return true;
    }

    @Override
    public Color[] getValues(TriggerContext ctx) {
        var values = rgb.stream(ctx).map(Integer::intValue).toArray(Integer[]::new);
        if (values.length == 3) {
            return Color.of(values[0], values[1], values[2])
                .map(val -> new Color[]{val})
                .orElse(new Color[0]);
        } else if (values.length == 4) {
            return Color.of(values[0], values[1], values[2], values[3])
                .map(val -> new Color[]{val})
                .orElse(new Color[0]);
        } else {
            return new Color[0];
        }
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "color from rgb " + rgb.toString(ctx, debug);
    }
}
