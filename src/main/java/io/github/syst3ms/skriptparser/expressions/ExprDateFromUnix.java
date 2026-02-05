package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.SkriptDate;

/**
 * The date from a (unix) timestamp.
 * The default timestamp returns the amount of <b>milliseconds</b> since the Unix Epoch.
 * The unix timestamp returns the amount of <b>seconds</b> since that same date.
 * The Unix Epoch is defined as January 1st 1970.
 *
 * @author Mwexim
 * @name Date from Unix
 * @type EXPRESSION
 * @pattern [the] date (from|of) [the] [unix] timestamp %integer%
 * @since ALPHA
 */
public class ExprDateFromUnix implements Expression<SkriptDate> {
    static {
        Parser.getMainRegistration().newExpression(ExprDateFromUnix.class, SkriptDate.class, true,
                "[the] date (from|of) [the] [1:unix] timestamp %integer%")
            .name("Date from Unix")
            .description("Returns the date corresponding to a given timestamp.")
            .since("1.0.0")
            .register();
    }

    private Expression<Integer> timestamp;
    private boolean unix;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        unix = parseContext.getNumericMark() == 1;
        timestamp = (Expression<Integer>) expressions[0];
        return true;
    }

    @Override
    public SkriptDate[] getValues(TriggerContext ctx) {
        return timestamp.getSingle(ctx)
            .map(t -> new SkriptDate[]{SkriptDate.of(unix ? t.longValue() * 1000 : t.longValue())})
            .orElse(new SkriptDate[0]);
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "date from " + (unix ? "unix " : "") + "timestamp " + timestamp.toString(ctx, debug);
    }
}
