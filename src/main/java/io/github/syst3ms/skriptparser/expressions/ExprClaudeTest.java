package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

public class ExprClaudeTest implements Expression<String> {

    static {
        Parser.getMainRegistration().newExpression(ExprClaudeTest.class, String.class,
            true, "claude test %number% [of] %number%")
            .noDoc()
            .register();
    }

    private Expression<Number> first, second;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        first = (Expression<Number>) expressions[0];
        second = (Expression<Number>) expressions[1];
        return true;
    }

    @Override
    public String[] getValues(TriggerContext ctx) {
        Number number = this.first.getSingle(ctx).orElse(null);
        Number other = this.second.getSingle(ctx).orElse(null);
        if (number != null && other != null) {
            return new String[]{"claude test " + number + " " + other};
        }
        return new String[]{};
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "";
    }
}
