package io.github.syst3ms.skriptparser.types.comparisons;

/**
 * A class containing information about a {@link Comparator}
 *
 * @param <T1> the getFirst type
 * @param <T2> the getSecond type
 */
public record ComparatorInfo<T1, T2>(Class<T1> firstClass, Class<T2> secondClass, Comparator<T1, T2> comparator) {

    public Class<?> getType(boolean first) {
        return first ? firstClass() : secondClass();
    }
}
