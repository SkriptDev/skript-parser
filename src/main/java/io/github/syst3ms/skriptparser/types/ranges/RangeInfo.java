package io.github.syst3ms.skriptparser.types.ranges;

import java.util.function.BiFunction;

/**
 * Information about a range function
 *
 * @param <B> the type of the two endpoints
 * @param <T> the type of the range that is returned
 */
public record RangeInfo<B, T>(Class<B> bound, Class<T> to, BiFunction<? super B, ? super B, T[]> function) {
}
