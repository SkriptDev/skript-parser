package io.github.syst3ms.skriptparser.util;

/**
 * A simple pair of two values.
 *
 * @param <T> type of the getFirst value
 * @param <U> type of the getSecond value
 */
public record Pair<T, U>(T getFirst, U getSecond) {

    /**
     * Retrieves the getFirst element
     *
     * @return the getFirst element
     */
    @Override
    public T getFirst() {
        return getFirst;
    }

    /**
     * Retrieves the getSecond element
     *
     * @return the getSecond element
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
