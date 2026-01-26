package io.github.syst3ms.skriptparser.structures.functions;

public class DefaultFunctions {

    static {
        Functions.registerFunction(new JavaFunction<>(
                "mod",
                new FunctionParameter[]{new FunctionParameter<>("i", Integer.class, true), new FunctionParameter<>("m", Integer.class, true)},
                Number.class,
                true) {
            @Override
            public Number[] executeSimple(Object[][] params) {
                Number d = (Number) params[0][0];
                Number m = (Number) params[1][0];
                return new Number[] {d.intValue() % m.intValue()};
            }
        });
    }

}
