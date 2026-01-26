package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import io.github.syst3ms.skriptparser.types.changers.Arithmetic;
import io.github.syst3ms.skriptparser.types.comparisons.Comparator;
import io.github.syst3ms.skriptparser.types.comparisons.Comparators;
import io.github.syst3ms.skriptparser.types.comparisons.Relation;
import io.github.syst3ms.skriptparser.types.ranges.Ranges;
import io.github.syst3ms.skriptparser.util.DurationUtils;
import io.github.syst3ms.skriptparser.util.SkriptDate;
import io.github.syst3ms.skriptparser.util.Time;
import io.github.syst3ms.skriptparser.util.color.Color;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A class registering features such as types and comparators at startup.
 */
public class DefaultRegistration {

    public static void register() {
        SkriptRegistration registration = Parser.getMainRegistration();

        /*
         * Classes
         */
        registration.newType(Object.class, "object", "object@s")
            .name("Object")
            .description("Any possible object.")
            .since("INSERT VERSION")
            .register();

        registration.newType(Number.class, "number", "number@s")
            .name("Number")
            .description("A number (can be an integer/float/double/etc).")
            .since("INSERT VERSION")
            .literalParser(s -> {
                if (s == null) return null;
                try {
                    if (s.contains(".")) return Double.parseDouble(s);
                    else return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .toStringFunction(Object::toString)
            .arithmetic(new Arithmetic<Number, Number>() {
                @Override
                public Number difference(Number first, Number second) {
                    return first.doubleValue() - second.doubleValue();
                }

                @Override
                public Number add(Number value, Number difference) {
                    return value.doubleValue() + difference.doubleValue();
                }

                @Override
                public Number subtract(Number value, Number difference) {
                    return value.doubleValue() - difference.doubleValue();
                }

                @Override
                public Class<? extends Number> getRelativeType() {
                    return Number.class;
                }
            }).register();

        registration.newType(Integer.class, "integer", "integer@s")
            .literalParser(s -> {
                if (s == null) return null;
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .name("Integer")
            .description("A whole number.")
            .since("INSERT VERSION")
            .arithmetic(new Arithmetic<Integer, Integer>() {
                @Override
                public Integer difference(Integer first, Integer second) {
                    return first - second;
                }

                @Override
                public Integer add(Integer value, Integer difference) {
                    return value + difference;
                }

                @Override
                public Integer subtract(Integer value, Integer difference) {
                    return value - difference;
                }

                @Override
                public Class<? extends Integer> getRelativeType() {
                    return Integer.class;
                }
            })
            .register();

        registration.newType(Float.class, "float", "float@s")
            .name("Float")
            .description("A floating-point number.")
            .since("INSERT VERSION")
            .literalParser(s -> {
                if (s == null) return null;
                try {
                    return Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .arithmetic(new Arithmetic<Float, Float>() {
                @Override
                public Float difference(Float first, Float second) {
                    return first - second;
                }

                @Override
                public Float add(Float value, Float difference) {
                    return value + difference;
                }

                @Override
                public Float subtract(Float value, Float difference) {
                    return value - difference;
                }

                @Override
                public Class<? extends Float> getRelativeType() {
                    return Float.class;
                }
            })
            .register();

        registration.newType(Double.class, "double", "double@s")
            .name("Double")
            .description("A double floating-point number.")
            .since("INSERT VERSION")
            .literalParser(s -> {
                if (s == null) return null;
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .arithmetic(new Arithmetic<Double, Double>() {
                @Override
                public Double difference(Double first, Double second) {
                    return first - second;
                }

                @Override
                public Double add(Double value, Double difference) {
                    return value + difference;
                }

                @Override
                public Double subtract(Double value, Double difference) {
                    return value - difference;
                }

                @Override
                public Class<? extends Double> getRelativeType() {
                    return Double.class;
                }
            })
            .register();

        registration.newType(String.class, "string", "string@s")
            .name("String")
            .description("A string of characters.")
            .examples("set {_string} to \"Hello World!\"")
            .since("INSERT VERSION")
            .register();

        registration.newType(Boolean.class, "boolean", "boolean@s")
            .literalParser(s -> {
                if (s.equalsIgnoreCase("true")) {
                    return true;
                } else if (s.equalsIgnoreCase("false")) {
                    return false;
                } else {
                    return null;
                }
            })
            .name("Boolean")
            .description("A boolean value, represented as 'true' or 'false'.")
            .since("INSERT VERSION")
            .toStringFunction(String::valueOf)
            .register();

        registration.newType(Type.class, "type", "type@s")
            .name("Type")
            .description("Represents a type/class.")
            .since("INSERT VERSION")
            .literalParser(s -> TypeManager.getByExactName(s.toLowerCase()).orElse(null))
            .toStringFunction(Type::getBaseName)
            .register();

        registration.newType(Color.class, "color", "color@s")
            .name("Color")
            .description("Represents a color.")
            .since("INSERT VERSION")
            .literalParser(s -> Color.ofLiteral(s).orElse(null))
            .toStringFunction(Color::toString)
            .register();

        registration.newType(Duration.class, "duration", "duration@s")
            .name("Duration")
            .description("Represents a time-based amount of time, such as '34.5 seconds'.")
            .literalParser(s -> DurationUtils.parseDuration(s).orElse(null))
            .toStringFunction(DurationUtils::toStringDuration)
            .arithmetic(new Arithmetic<Duration, Duration>() {
                @Override
                public Duration difference(Duration first, Duration second) {
                    return first.minus(second).abs();
                }

                @Override
                public Duration add(Duration value, Duration difference) {
                    return value.plus(difference);
                }

                @Override
                public Duration subtract(Duration value, Duration difference) {
                    return value.minus(difference);
                }

                @Override
                public Class<? extends Duration> getRelativeType() {
                    return Duration.class;
                }
            })
            .register();

        registration.newType(SkriptDate.class, "date", "date@s")
            .toStringFunction(SkriptDate::toString)
            .arithmetic(new Arithmetic<SkriptDate, Duration>() {
                @Override
                public Duration difference(SkriptDate first, SkriptDate second) {
                    return first.difference(second);
                }

                @Override
                public SkriptDate add(SkriptDate value, Duration difference) {
                    return value.plus(difference);
                }

                @Override
                public SkriptDate subtract(SkriptDate value, Duration difference) {
                    return value.minus(difference);
                }

                @Override
                public Class<? extends Duration> getRelativeType() {
                    return Duration.class;
                }
            })
            .name("Date")
            .description("A date, represented as a string in the format 'yyyy-MM-dd HH:mm:ss'.")
            .since("INSERT VERSION")
            .register();

        registration.newType(Time.class, "time", "time@s")
            .literalParser(s -> Time.parse(s).orElse(null))
            .toStringFunction(Time::toString)
            .arithmetic(new Arithmetic<Time, Duration>() {
                @Override
                public Duration difference(Time first, Time second) {
                    return first.difference(second);
                }

                @Override
                public Time add(Time value, Duration difference) {
                    return value.plus(difference);
                }

                @Override
                public Time subtract(Time value, Duration difference) {
                    return value.minus(difference);
                }

                @Override
                public Class<? extends Duration> getRelativeType() {
                    return Duration.class;
                }
            })
            .name("Time")
            .description("A time, represented as a string in the format 'HH:mm:ss'.")
            .since("INSERT VERSION")
            .register();

        /*
         * Comparators
         */
        Comparators.registerComparator(
            Number.class,
            Number.class,
            new Comparator<>(true) {
                @Override
                public Relation apply(Number number, Number number2) {
                    return Relation.get(number.equals(number2));
                }
            }
        );

        Comparators.registerComparator(
            Duration.class,
            Duration.class,
            new Comparator<>(true) {
                @Override
                public Relation apply(Duration duration, Duration duration2) {
                    return Relation.get(duration.compareTo(duration2));
                }
            }
        );

        /*
         * Ranges
         */

        // Actually a character range
        Ranges.registerRange(
            String.class,
            String.class,
            (l, r) -> {
                if (l.length() != 1 || r.length() != 1)
                    return new String[0];
                char leftChar = l.charAt(0), rightChar = r.charAt(0);
                return IntStream.range(leftChar, rightChar + 1)
                    .mapToObj(i -> Character.toString((char) i))
                    .toArray(String[]::new);
            }
        );

        /*
         * Converters
         */

        registration.addConverter(SkriptDate.class, Time.class, da -> Optional.of(Time.of(da)));

        registration.register(true); // Ignoring logs here, we control the input
    }
}
