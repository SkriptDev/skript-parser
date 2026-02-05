package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

/**
 * The character at a given position in a string. Note that indices in Skript start at 1.
 *
 * @name Character At
 * @pattern [the] char[acter][s] at [(ind(ex[es]|ices)|pos[ition][s])] %integers% (of|in) %string%
 * @since ALPHA
 * @author Olyno
 */
public class ExprStringCharAt implements Expression<String> {
	static {
		Parser.getMainRegistration().newExpression(ExprStringCharAt.class, String.class, false,
				"[the] char[acter][s] at [(ind(ex[es]|ices)|pos[ition][s])] %integers% (of|in) %string%")
			.name("Character At")
			.description("Returns the character(s) at a given position(s) in a string.")
			.since("1.0.0")
			.register();
	}

	private Expression<Integer> position;
	private Expression<String> value;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
		position = (Expression<Integer>) expressions[0];
		value = (Expression<String>) expressions[1];
		return true;
	}

	@Override
	public String[] getValues(TriggerContext ctx) {
		return value.getSingle(ctx)
				.map(val -> position.stream(ctx)
						.filter(pos -> Integer.signum(pos) > 0 && pos.compareTo(Integer.valueOf(val.length())) <= 0)
						.map(pos -> String.valueOf(val.charAt(pos.intValue() - 1)))
						.toArray(String[]::new))
				.orElse(new String[0]);
	}

	@Override
	public boolean isSingle() {
		return position.isSingle();
	}

	@Override
	public String toString(TriggerContext ctx, boolean debug) {
		return "character at index " + position.toString(ctx, debug) + " in " + value.toString(ctx, debug);
	}
}
