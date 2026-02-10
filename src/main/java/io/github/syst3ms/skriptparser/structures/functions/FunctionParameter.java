package io.github.syst3ms.skriptparser.structures.functions;

public record FunctionParameter<T>(String name, Class<? extends T> type, boolean single) {

    public FunctionParameter(String name, Class<? extends T> type) {
        this(name, type, true);
    }

    @Override
    public String toString() {
        return "FunctionParameter{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", single=" + single +
            '}';
    }

}
