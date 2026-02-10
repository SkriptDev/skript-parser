package io.github.syst3ms.skriptparser.structures.functions;

import java.util.Objects;

public final class FunctionParameter<T> {
    private final String name;
    private final Class<? extends T> type;
    private final boolean single;

    public FunctionParameter(String name, Class<? extends T> type, boolean single) {
        this.name = name;
        this.type = type;
        this.single = single;
    }

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

    public String getName() {
        return name;
    }

    public Class<? extends T> getType() {
        return type;
    }

    public boolean isSingle() {
        return single;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FunctionParameter) obj;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.type, that.type) &&
            this.single == that.single;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, single);
    }


}
