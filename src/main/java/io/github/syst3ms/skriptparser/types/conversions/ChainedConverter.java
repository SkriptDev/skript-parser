package io.github.syst3ms.skriptparser.types.conversions;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Used to chain converters to build a single converter. This is automatically created when a new converter is added.
 *
 * @param <F> same as Converter's <F> (from)
 * @param <M> the middle type, i.e. the type the getFirst converter converts to and the getSecond converter comverts from.
 * @param <T> same as Converter's <T> (to)
 * @author Peter Güttinger
 * @see Converters#registerConverter(Class, Class, Function)
 */
public final class ChainedConverter<F, M, T> implements Function<F, Optional<? extends T>> {
    private final Function<? super F, Optional<? extends M>> first;
    private final Function<? super M, Optional<? extends T>> second;

    public ChainedConverter(Function<? super F, Optional<? extends M>> first, Function<? super M, Optional<? extends T>> second) {
        this.first = first;
        this.second = second;
    }

    public static <F, M, T> ChainedConverter<F, M, T> newInstance(Function<? super F, Optional<? extends M>> first, Function<? super M, Optional<? extends T>> second) {
        return new ChainedConverter<>(first, second);
    }

    @Override
    public Optional<? extends T> apply(@Nullable F f) {
        return first.apply(f).flatMap(second::apply);
    }

}
