package io.github.syst3ms.skriptparser.types.conversions;

import org.intellij.lang.annotations.MagicConstant;

import java.util.Optional;
import java.util.function.Function;

public record ConverterInfo<F, T>(Class<F> from, Class<T> to, Function<? super F, Optional<? extends T>> converter,
                                  int flags) {
    public ConverterInfo(Class<F> from, Class<T> to, Function<? super F, Optional<? extends T>> converter) {
        this(from, to, converter, Converters.ALL_CHAINING);
    }

    public ConverterInfo(Class<F> from, Class<T> to, Function<? super F, Optional<? extends T>> converter, @MagicConstant(intValues = {Converters.ALL_CHAINING, Converters.NO_LEFT_CHAINING, Converters.NO_RIGHT_CHAINING, Converters.NO_CHAINING}) int flags) {
        this.from = from;
        this.to = to;
        this.converter = converter;
        this.flags = flags;
    }
}
