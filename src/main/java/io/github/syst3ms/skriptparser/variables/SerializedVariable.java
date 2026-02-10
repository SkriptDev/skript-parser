package io.github.syst3ms.skriptparser.variables;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

/**
 * @param name  The name of the variable.
 * @param value The serialized value of the variable.
 *              <p>
 *              A value of {@code null} indicates the variable will be deleted.
 */
public record SerializedVariable(String name, @Nullable Value value) {

    /**
     * Creates a new serialized variable with the given name and value.
     *
     * @param name  the given name.
     * @param value the given value, or {@code null} to indicate a deletion.
     */
    public SerializedVariable(String name, @Nullable Value value) {
        this.name = name;
        this.value = value;
    }

    /**
     * A serialized value of a variable.
     *
     * @param type The type of this value.
     * @param data The serialized value data.
     */
    public record Value(String type, JsonElement data) {

        /**
         * Creates a new serialized value.
         *
         * @param type the value type.
         * @param data the serialized value data.
         */
        public Value {
        }

    }

}
