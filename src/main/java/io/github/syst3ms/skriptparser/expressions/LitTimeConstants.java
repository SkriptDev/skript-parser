package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.Time;

/**
 * Midnight and noon time constants and the ability to use
 * expressions like {@code 12 o' clock} for literal usage.
 *
 * @author Mwexim
 * @name Time Constants
 * @pattern (midnight | noon | midday)
 * @pattern %*integer% o'[ ]clock
 * @since ALPHA
 */
public class LitTimeConstants implements Literal<Time> {
    static {
        Parser.getMainRegistration().newExpression(LitTimeConstants.class, Time.class, true,
                "(0:noon|0:midday|1:midnight)", "%*integer% o'[ ]clock")
            .name("Time Constants")
            .description("Midnight, noon, and midday time constants, as well as the ability to use expressions like '12 o' clock' for literal usage.")
            .since("1.0.0")
            .register();
    }

    private Literal<Integer> hours;
    private boolean onClock;
    private boolean midnight;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        onClock = matchedPattern == 1;
        midnight = parseContext.getNumericMark() == 1;
        if (onClock) {
            hours = (Literal<Integer>) expressions[0];
            if (hours.getSingle().isPresent()) {
                int h = hours.getSingle().get();
                if (h < 0 || h > 24) {
                    parseContext.getLogger().error("The given hour ('" + h + "') is not in between 0 and 24",
                        ErrorType.SEMANTIC_ERROR);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Time[] getValues() {
        if (onClock) {
            return hours.getSingle()
                .map(h -> Time.of(h.intValue(), 0, 0, 0))
                .map(t -> new Time[]{t})
                .orElse(new Time[0]);
        } else {
            return new Time[]{midnight ? Time.MIDNIGHT : Time.NOON};
        }
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        if (onClock) {
            return hours.toString(ctx, debug) + " o' clock";
        } else {
            return midnight ? "midnight" : "noon";
        }
    }

}
