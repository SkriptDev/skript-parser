package io.github.syst3ms.skriptparser.types;

import java.util.Objects;

/**
 * A type used in a pattern.
 * Groups a {@link Type} and a number (single or plural) together (in contrast to {@link Type})
 */
public final class PatternType<T> {
    private final Type<T> type;
    private final boolean single;

    /**
     *
     */
    public PatternType(Type<T> type, boolean single) {
        this.type = type;
        this.single = single;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PatternType<?> o)) {
            return false;
        } else {
            return type.equals(o.type) && single == o.single;
        }
    }

    @Override
    public String toString() {
        var forms = type.getPluralForms();
        return forms[single ? 0 : 1];
    }

    public Type<T> getType() {
        return type;
    }

    public boolean isSingle() {
        return single;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, single);
    }

}
