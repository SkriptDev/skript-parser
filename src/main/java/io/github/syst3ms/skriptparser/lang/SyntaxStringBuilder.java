package io.github.syst3ms.skriptparser.lang;

import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

/**
 * Utility class to build syntax strings, primarily intended for use
 * in {@link SyntaxElement#toString(TriggerContext, boolean)} implementations.
 * Spaces are automatically added between the provided objects.
 */
public class SyntaxStringBuilder {

    private final boolean debug;
    private final TriggerContext context;
    private final StringJoiner joiner = new StringJoiner(" ");

    /**
     * Creates a new SyntaxStringBuilder.
     *
     * @param context The context to get information from.
     * @param debug If true this should print more information, if false this should print what is shown to the end user
     */
    public SyntaxStringBuilder(TriggerContext context, boolean debug) {
        this.context = context;
        this.debug = debug;
    }

    /**
     * Adds an object to the string and returns the builder.
     * Spaces are automatically added between the provided objects.
     * If the object is a {@link SyntaxElement} it will be formatted using
     * {@link SyntaxElement#toString(TriggerContext, boolean)}.
     *
     * @param object The object to add.
     * @return The builder.
     * @see #appendIf(boolean, Object)
     */
    public SyntaxStringBuilder append(@NotNull Object object) {
        if (object instanceof SyntaxElement debuggable) {
            joiner.add(debuggable.toString(context, debug));
        } else {
            joiner.add(object.toString());
        }
        return this;
    }

    /**
     * Adds multiple objects to the string and returns the builder.
     * Spaces are automatically added between the provided objects.
     *
     * @param objects The objects to add.
     * @return The builder.
     * @see #appendIf(boolean, Object...)
     */
    public SyntaxStringBuilder append(@NotNull Object... objects) {
        for (Object object : objects) {
            append(object);
        }
        return this;
    }

    /**
     * Adds an object to the string and returns the builder, if the given condition is true.
     * Spaces are automatically added between the provided objects.
     * If the object is a {@link SyntaxElement} it will be formatted using
     * {@link SyntaxElement#toString(TriggerContext, boolean)}.
     *
     * @param condition The condition.
     * @param object The object to add. Ensure this is not null.
     * @return The builder.
     * @see #append(Object)
     */
    public SyntaxStringBuilder appendIf(boolean condition, Object object) {
        if (condition) {
            append(object);
        }
        return this;
    }

    /**
     * Adds multiple objects to the string and returns the builder, if the given condition is true.
     * Spaces are automatically added between the provided objects.
     *
     * @param condition The condition.
     * @param objects The objects to add. Ensure this is not null.
     * @return The builder.
     * @see #append(Object...)
     */
    public SyntaxStringBuilder appendIf(boolean condition, Object... objects) {
        if (condition) {
            append(objects);
        }
        return this;
    }

    @Override
    public String toString() {
        return joiner.toString();
    }

}

