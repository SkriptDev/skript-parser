package io.github.syst3ms.skriptparser.structures.functions;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;

public class DefaultFunctions {

    static {
        SkriptRegistration reg = Parser.getMainRegistration();

        reg.newJavaFunction("mod", Number.class, true)
            .parameter("i", Number.class, true)
            .parameter("m", Number.class, true)
            .executeSingle(params -> {
                Number i = (Number) params[0][0];
                Number m = (Number) params[1][0];
                return i.doubleValue() % m.doubleValue();
            })
            .name("Mod")
            .description("Returns the remainder of the division of the first number by the second one.")
            .examples("set {_remainder} to mod(10, 3)",
                "if mod({_var}, 2) = 0:")
            .since("1.0.0")
            .register();
    }

}
