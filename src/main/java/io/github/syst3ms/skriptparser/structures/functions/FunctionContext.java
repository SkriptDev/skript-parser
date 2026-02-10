package io.github.syst3ms.skriptparser.structures.functions;

import io.github.syst3ms.skriptparser.lang.TriggerContext;

public record FunctionContext(Function<?> owningFunction) implements TriggerContext {

    @Override
    public String getName() {
        return "function";
    }

}
