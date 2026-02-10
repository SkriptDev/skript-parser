package io.github.syst3ms.skriptparser.util;

/**
 * A simple pair of two values.
 *
 * @param <T> type of the first value
 * @param <U> type of the second value
 */
public record Pair<T, U>(T first, U second) {

    /**
     * Retrieves the first element
     *
     * @return the first element
     */
    @Override
    public T first() {
        return first;
    }

    /**
     * Retrieves the second element
     *
     * @return the second element
     */
    @Override
    public U second() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        var pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
            second.equals(pair.second);
    }
}
