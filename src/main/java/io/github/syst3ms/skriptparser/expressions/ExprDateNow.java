package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.SkriptDate;

import java.time.Duration;

/**
 * The current date, the one from yesterday or the one from tomorrow.
 *
 * @name Now
 * @type EXPRESSION
 * @pattern (yesterday|now|tomorrow)
 * @since ALPHA
 * @author Mwexim
 */
public class ExprDateNow implements Expression<SkriptDate> {
    static {
        Parser.getMainRegistration().newExpression(
                ExprDateNow.class,
                SkriptDate.class,
                true,
                "(0:yesterday|1:now|2:tomorrow)")
            .name("Current Date")
            .description("The current date, the one from yesterday or the one from tomorrow.")
            .since("INSERT VERSION")
            .register();
    }

	private int mark;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
		mark = parseContext.getNumericMark();
		return true;
	}

	@Override
	public SkriptDate[] getValues(TriggerContext ctx) {
		switch (mark) {
			case 0:
				return new SkriptDate[] {SkriptDate.now().minus(Duration.ofDays(1))};
			case 1:
				return new SkriptDate[] {SkriptDate.now()};
			case 2:
				return new SkriptDate[] {SkriptDate.now().plus(Duration.ofDays(1))};
			default:
				throw new IllegalStateException();
		}
	}

	@Override
	public String toString(TriggerContext ctx, boolean debug) {
		return new String[] {"yesterday", "now", "tomorrow"}[mark];
	}
}
