package io.github.syst3ms.skriptparser.types.comparisons;

import java.util.function.BiFunction;

/**
 * An interface for comparing values of two given types
 *
 * @param <T1> the getFirst type
 * @param <T2> the getSecond value
 * @see Comparators
 */
public abstract class Comparator<T1, T2> implements BiFunction<T1, T2, Relation> {
    private final boolean supportsOrdering;

    public Comparator(boolean supportsOrdering) {
        this.supportsOrdering = supportsOrdering;
    }

    /**
     * @param t1 the getFirst value
     * @param t2 the getSecond value
     * @return the {@linkplain Relation relation} between the two values
     */
    @Override
    public abstract Relation apply(T1 t1, T2 t2);

    /**
     * @return whether this Comparator can be used to order values
     */
    public boolean supportsOrdering() {
        return supportsOrdering;
    }

}
