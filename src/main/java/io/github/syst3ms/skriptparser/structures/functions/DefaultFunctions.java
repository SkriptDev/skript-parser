package io.github.syst3ms.skriptparser.structures.functions;

public class DefaultFunctions {

    static {
        Functions.newJavaFunction(new JavaFunction<>(
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
            .since("INSERT VERSION")
            .register();
    }

}
