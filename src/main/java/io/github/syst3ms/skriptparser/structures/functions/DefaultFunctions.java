package io.github.syst3ms.skriptparser.structures.functions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;

public class DefaultFunctions {

    static {
        SkriptRegistration reg = Parser.getMainRegistration();
        Functions.newJavaFunction(reg,new JavaFunction<>(
                "mod",
                new FunctionParameter[]{new FunctionParameter<>("i", Number.class, true), new FunctionParameter<>("m", Number.class, true)},
                Number.class,
                true) {
                @Override
                public Number[] executeSimple(Object[][] params) {
                    Number d = (Number) params[0][0];
                    Number m = (Number) params[1][0];
                    return new Number[]{d.doubleValue() % m.doubleValue()};
                }
            })
            .name("Mod")
            .description("Returns the remainder of the division of the first number by the second one.")
            .examples("set {_remainder} to mod(10, 3)",
                "if mod({_var}, 2) = 0:")
            .since("1.0.0")
            .register();
    }

}
