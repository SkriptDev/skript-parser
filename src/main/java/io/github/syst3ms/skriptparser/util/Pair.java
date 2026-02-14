package io.github.syst3ms.skriptparser.util;

/**
 * A simple pair of two values.
 *
 * @param <T> type of the first value
 * @param <U> type of the second value
 */
public record Pair<T, U>(T getFirst, U getSecond) {

    /**
     * Retrieves the first element
     *
     * @return the first element
     */
    @Override
    public T getFirst() {
        return getFirst;
    }

    /**
     * Retrieves the second element
     *
     * @return the second element
     */
    @Override
    public U getSecond() {
        return getSecond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        var pair = (Pair<?, ?>) o;
        return getFirst.equals(pair.getFirst) &&
            getSecond.equals(pair.getSecond);
    }
}
