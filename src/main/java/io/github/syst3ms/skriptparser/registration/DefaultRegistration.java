package io.github.syst3ms.skriptparser.registration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.structures.functions.FunctionParameter;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import io.github.syst3ms.skriptparser.types.changers.Arithmetic;
import io.github.syst3ms.skriptparser.types.changers.TypeSerializer;
import io.github.syst3ms.skriptparser.types.comparisons.Comparator;
import io.github.syst3ms.skriptparser.types.comparisons.Comparators;
import io.github.syst3ms.skriptparser.types.comparisons.Relation;
import io.github.syst3ms.skriptparser.types.ranges.Ranges;
import io.github.syst3ms.skriptparser.util.DurationUtils;
import io.github.syst3ms.skriptparser.util.SkriptDate;
import io.github.syst3ms.skriptparser.util.Time;
import io.github.syst3ms.skriptparser.util.color.Color;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
            .since("1.0.0")
            .register();

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
            .since("1.0.0")
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
            .serializer(new TypeSerializer<>() {
                @Override
                public JsonElement serialize(Gson gson, Integer value) {
                    return gson.toJsonTree(value, Integer.class);
                }
                @Override
                public Integer deserialize(Gson gson, JsonElement element) {
                    return gson.fromJson(element, Integer.class);
                }
            })
            .register();

        registration.newType(Float.class, "float", "float@s")
            .name("Float")
            .description("A floating-point number.")
            .since("1.0.0")
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
            .serializer(new TypeSerializer<>() {
                @Override
                public JsonElement serialize(Gson gson, Float value) {
                    return gson.toJsonTree(value, Float.class);
                }
                @Override
                public Float deserialize(Gson gson, JsonElement element) {
                    return gson.fromJson(element, Float.class);
                }
            })
            .toStringFunction(f -> String.format("%.2f", f))
            .register();

        registration.newType(Double.class, "double", "double@s")
            .name("Double")
            .description("A double floating-point number.")
            .since("1.0.0")
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
            .serializer(new TypeSerializer<>() {
                @Override
                public JsonElement serialize(Gson gson, Double value) {
                    return gson.toJsonTree(value, Double.class);
                }
                @Override
                public Double deserialize(Gson gson, JsonElement element) {
                    return gson.fromJson(element, Double.class);
                }
            })
            .toStringFunction(d -> String.format("%.2f", d))
            .register();

        registration.newType(Number.class, "number", "number@s")
            .name("Number")
            .description("A number (can be an integer/float/double/etc).")
            .since("1.0.0")
            .literalParser(s -> {
                if (s == null) return null;
                try {
                    return Double.parseDouble(s);
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
            })
            .serializer(new TypeSerializer<>() {
                @Override
                public JsonElement serialize(Gson gson, Number value) {
                    return gson.toJsonTree(value.doubleValue());
                }

                @Override
                public Number deserialize(Gson gson, JsonElement element) {
                    return gson.fromJson(element, Double.class);
                }
            })
            .toStringFunction(n -> {
                if (n instanceof Integer || n instanceof Long) {
                    return n.toString();
                } else {
                    return String.format("%.2f", n.doubleValue());
                }
            })
            .register();

        registration.newType(String.class, "string", "string@s")
            .name("String")
            .description("A string of characters.")
            .examples("set {_string} to \"Hello World!\"")
            .since("1.0.0")
            .serializer(new TypeSerializer<>() {
                @Override
                public JsonElement serialize(Gson gson, String value) {
                    return gson.toJsonTree(value, String.class);
                }

                @Override
                public String deserialize(Gson gson, JsonElement element) {
                    return gson.fromJson(element, String.class);
                }
            })
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
            .since("1.0.0")
            .toStringFunction(String::valueOf)
            .serializer(new TypeSerializer<>() {
                @Override
                public JsonElement serialize(Gson gson, Boolean value) {
                    return gson.toJsonTree(value, Boolean.class);
                }

                @Override
                public Boolean deserialize(Gson gson, JsonElement jsonElement) {
                    return gson.fromJson(jsonElement, Boolean.class);
                }

            })
            .register();

        registration.newType(Type.class, "type", "type@s")
            .name("Type")
            .description("Represents a type/class.")
            .since("1.0.0")
            .literalParser(s -> TypeManager.getByExactName(s.toLowerCase()).orElse(null))
            .toStringFunction(Type::getBaseName)
            .register();

        registration.newType(FunctionParameter.class, "functionparameter", "functionparameter@s")
            .toStringFunction(parameter -> parameter.getName() + ": " + parameter.getType().getName())
            .noDoc()
            .register();

        registration.newType(Color.class, "color", "color@s")
            .name("Color")
            .description("Represents a color.")
            .since("1.0.0")
            .literalParser(s -> Color.ofLiteral(s).orElse(null))
            .toStringFunction(Color::toString)
            .register();

        registration.newType(Duration.class, "duration", "duration@s")
            .name("Duration")
            .description("Represents a time-based amount of time, such as '34.5 seconds'.")
            .usage(DurationUtils.getUsage())
            .since("1.0.0")
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
            .since("1.0.0")
            .register();

        registration.newType(Time.class, "time", "time@s")
            .name("Time")
            .description("A time, represented as a string in the format 'HH:mm:ss'.")
            .since("1.0.0")
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
            .serializer(new TypeSerializer<>() {
                @Override
                public JsonElement serialize(Gson gson, Time value) {
                    int nano = value.getTime().getNano();
                    return gson.toJsonTree(nano, Integer.class);
                }
                @Override
                public Time deserialize(Gson gson, JsonElement element) {
                    int asInt = element.getAsInt();
                    LocalTime localTime = LocalTime.ofNanoOfDay(asInt);
                    return Time.of(localTime);
                }
            })
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
                    return Relation.get(number.doubleValue() - number2.doubleValue());
                }
            }
        );
        Comparators.registerComparator(
            Integer.class,
            Integer.class,
            new Comparator<>(true) {
                @Override
                public Relation apply(Integer number, Integer number2) {
                    return Relation.get(number - number2);
                }
            }
        );
        Comparators.registerComparator(
            Double.class,
            Double.class,
            new Comparator<>(true) {
                @Override
                public Relation apply(Double number, Double number2) {
                    return Relation.get(number - number2);
                }
            }
        );
        Comparators.registerComparator(
            Float.class,
            Float.class,
            new Comparator<>(true) {
                @Override
                public Relation apply(Float number, Float number2) {
                    return Relation.get(number - number2);
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
        Ranges.registerRange(
            Integer.class,
            Integer.class,
            (l, r) -> {
                if (l >= r) {
                    return new Integer[0];
                } else {
                    List<Integer> elements = new ArrayList<>();
                    int current = l;
                    do {
                        elements.add(current);
                        current = current + 1;
                    } while (current <= r);
                    return elements.toArray(new Integer[0]);
                }
            }
        );
        Ranges.registerRange(
            Number.class,
            Number.class,
            (l, r) -> {
                if (l.doubleValue() >= r.doubleValue()) {
                    return new Number[0];
                } else {
                    List<Number> elements = new ArrayList<>();
                    Number current = l;
                    do {
                        elements.add(current);
                        current = current.doubleValue() + 1;
                    } while (current.doubleValue() <= r.doubleValue());
                    return elements.toArray(new Number[0]);
                }
            }
        );

        /*
         * Converters
         */

        registration.addConverter(SkriptDate.class, Time.class, da -> Optional.of(Time.of(da)));
        registration.addConverter(Long.class, Integer.class, l -> Optional.of(l.intValue()));
        registration.addConverter(Integer.class, Long.class, i -> Optional.of(i.longValue()));
        registration.addConverter(Double.class, Float.class, d -> Optional.of(d.floatValue()));
        registration.addConverter(Float.class, Double.class, f -> Optional.of(f.doubleValue()));
        registration.addConverter(Number.class, Integer.class, n -> Optional.of(n.intValue()));
        registration.addConverter(Number.class, Long.class, n -> Optional.of(n.longValue()));
        registration.addConverter(Number.class, Float.class, n -> Optional.of(n.floatValue()));
        registration.addConverter(Number.class, Double.class, n -> Optional.of(n.doubleValue()));
        registration.register(true); // Ignoring logs here, we control the input
    }
}
