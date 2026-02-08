package io.github.syst3ms.skriptparser.expressions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.lambda.ArgumentSection;
import io.github.syst3ms.skriptparser.lang.lambda.SectionValue;
import io.github.syst3ms.skriptparser.parsing.ParseContext;

public class ExprSingleParameter extends SectionValue<ArgumentSection, Object> {
    static {
        Parser.getMainRegistration().newExpression(
                ExprSingleParameter.class,
                Object.class,
                true,
                "[the] (input|parameter)")
            .name("Single Parameter")
            .description("Returns the input of a section expression.")
            .since("1.0.0")
            .register();
    }

    @Override
    public boolean preInitialize(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        return true;
    }

    @Override
    public Object[] getSectionValues(ArgumentSection section, TriggerContext ctx) {
        if (section.getArguments().length == 1) {
            return section.getArguments();
        } else {
            return new Object[0];
        }
    }

    @Override
    public Class<? extends ArgumentSection> getSectionClass() {
        return ArgumentSection.class;
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "parameter";
    }
}
