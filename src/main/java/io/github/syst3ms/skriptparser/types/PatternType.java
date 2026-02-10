package io.github.syst3ms.skriptparser.types;

/**
 * A type used in a pattern.
 * Groups a {@link Type} and a number (single or plural) together (in contrast to {@link Type})
 */
public record PatternType<T>(Type<T> type, boolean single) {

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
}
