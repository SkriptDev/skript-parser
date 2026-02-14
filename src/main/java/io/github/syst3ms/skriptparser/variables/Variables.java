package io.github.syst3ms.skriptparser.variables;

import io.github.syst3ms.skriptparser.config.Config.ConfigSection;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.Variable;
import io.github.syst3ms.skriptparser.lang.VariableString;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.types.TypeManager.StringMode;
import io.github.syst3ms.skriptparser.util.MultiMap;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * A class handling operations on variables.
 */
@SuppressWarnings("unused")
public class Variables {

    public static final Pattern REGEX_PATTERN = Pattern.compile("\\{([^{}]|%\\{|}%)+}");
    public static final String LOCAL_VARIABLE_TOKEN = "_";
    public static final String LIST_SEPARATOR = "::";
    static final MultiMap<Class<? extends VariableStorage>, String> AVAILABLE_STORAGES = new MultiMap<>();
    static final List<VariableStorage> STORAGES = new ArrayList<>();
    private static final Map<TriggerContext, VariableMap> localVariables = new HashMap<>(); // Make trigger-specific
    private static final VariableMap variableMap = new VariableMap();
    private static final ReentrantLock LOCK = new ReentrantLock();
    /**
     * Changes to variables that have not yet been performed.
     */
    private static final Queue<VariableChange> VARIABLE_CHANGE_QUEUE = new ConcurrentLinkedQueue<>();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean hasStorages() {
        return !STORAGES.isEmpty();
    }

    public static ReentrantLock getLock() {
        return LOCK;
    }

    public static void shutdown() {
        for (VariableStorage storage : STORAGES) {
            try {
                storage.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        localVariables.clear();
        variableMap.clearVariables();
    }

    public static VariableMap getVariableMap() {
        return variableMap;
    }

    /**
     * Register a VariableStorage class.
     *
     * @param storage The class of the VariableStorage implementation.
     * @param names   The names used to reference this storage.
     * @param <T>     Generic representing class that extends VariableStorage.
     * @return if the storage was registered, false if it's already registered.
     */
    public static <T extends VariableStorage> boolean registerStorage(Class<T> storage, String... names) {
        if (AVAILABLE_STORAGES.containsKey(storage))
            return false;
        for (String name : names) {
            if (AVAILABLE_STORAGES.getAllValues().contains(name.toLowerCase(Locale.ENGLISH)))
                return false;
        }
        for (String name : names)
            AVAILABLE_STORAGES.putOne(storage, name.toLowerCase(Locale.ENGLISH));
        return true;
    }

    /**
     * Loads a section configuration containing all the database info.
     * Parent section node name must be 'databases:'.
     *
     * @param logger  the SkriptLogger to print errors to.
     * @param section the ConfigSection to load for the databases.
     * @throws IllegalArgumentException throws when the section is not valid.
     */
    public static void load(SkriptLogger logger, ConfigSection section) throws IllegalArgumentException {
        for (ConfigSection databaseSection : section.getSections()) {
            String databaseName = databaseSection.getName();

            boolean enabled = databaseSection.getBoolean("enabled");
            if (!enabled) {
                logger.warn("Database '" + databaseName + "' is disabled. Skipping...");
                continue;
            }

            String databaseType = databaseSection.getString("type");
            if (databaseType == null) {
                logger.error("The configuration is missing the type for the database '" + databaseName + "'", ErrorType.SEMANTIC_ERROR);
                continue;
            }
            logger.debug("Database type: " + databaseType);

            Class<? extends VariableStorage> storageClass = AVAILABLE_STORAGES.entrySet().stream()
                .filter(entry -> entry.getValue().contains(databaseType.toLowerCase(Locale.ENGLISH)))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
            if (storageClass == null) {
                logger.error("There is no database registered with the name '" + databaseName + "'", ErrorType.SEMANTIC_ERROR);
                continue;
            }

            try {
                Constructor<? extends VariableStorage> constructor = storageClass.getConstructor(SkriptLogger.class, String.class);
                VariableStorage storage = constructor.newInstance(logger, databaseName);
                if (storage.loadConfiguration(databaseSection))
                    STORAGES.add(storage);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                logger.error("VariableStorage class '" + storageClass.getName() + "' does not implement the correct constructors.", ErrorType.SEMANTIC_ERROR);
                logger.debug("Exception: " + e.getLocalizedMessage());
            }
        }
        logger.debug("Loaded " + STORAGES.size() + " databases.");
        STORAGES.forEach(VariableStorage::allLoaded);
        processChangeQueue();
    }

    public static <T> Optional<? extends Expression<T>> parseVariable(String input, Class<? extends T> types, ParserState parserState, SkriptLogger logger) {
        input = input.strip();
        if (REGEX_PATTERN.matcher(input).matches()) {
            input = input.substring(1, input.length() - 1);
        } else {
            return Optional.empty();
        }
        if (!isValidVariableName(input, true, logger)) {
            return Optional.empty();
        }
        var vs = VariableString.newInstance(
            input.startsWith(LOCAL_VARIABLE_TOKEN) ? input.substring(LOCAL_VARIABLE_TOKEN.length()).strip() : input,
            parserState,
            logger, StringMode.VARIABLE
        );
        var finalS = input;
        return vs.map(v -> new Variable<>(v, finalS.startsWith(LOCAL_VARIABLE_TOKEN), finalS.endsWith(LIST_SEPARATOR + "*"), types));
    }

    /**
     * Checks whether a string is a valid variable name.
     *
     * @param name        The name to test
     * @param printErrors Whether to print errors when they are encountered
     * @param logger      the logger
     * @return true if the name is valid, false otherwise.
     */
    public static boolean isValidVariableName(String name, boolean printErrors, SkriptLogger logger) {
        name = name.startsWith(LOCAL_VARIABLE_TOKEN) ? name.substring(LOCAL_VARIABLE_TOKEN.length()).strip() : name.strip();
        if (name.startsWith(LIST_SEPARATOR) || name.endsWith(LIST_SEPARATOR)) {
            if (printErrors) {
                logger.error("A variable name cannot start nor end with the list separator " + LIST_SEPARATOR, ErrorType.MALFORMED_INPUT);
            }
            return false;
        } else if (name.contains("*") && (name.indexOf("*") != name.length() - 1 || !name.endsWith(LIST_SEPARATOR + "*"))) {
            if (printErrors) {
                logger.error("A variable name cannot contain an asterisk outside of a list declaration", ErrorType.MALFORMED_INPUT);
            }
            return false;
        } else if (name.contains(LIST_SEPARATOR + LIST_SEPARATOR)) {
            if (printErrors) {
                logger.error("A variable name cannot contain two list separators stuck together", ErrorType.MALFORMED_INPUT);
            }
            return false;
        }
        return true;
    }

    /**
     * Returns the internal value of the requested variable.
     * <p>
     * <b>Do not modify the returned value!</b>
     *
     * @param name the name of the variable
     * @return an Object for a normal Variable or a Map<String, Object> for a list variable, or null if the variable is not set.
     */
    public static Optional<Object> getVariable(String name, TriggerContext e, boolean local) {
        if (local) {
            var map = localVariables.get(e);
            if (map == null)
                return Optional.empty();
            return map.getVariable(name);
        } else {
            return variableMap.getVariable(name);
        }
    }

    /**
     * Sets a variable.
     *
     * @param name  The variable's name. Can be a "list variable::*" (<tt>value</tt> must be <tt>null</tt> in this case)
     * @param value The variable's value. Use <tt>null</tt> to delete the variable.
     */
    public static void setVariable(String name, @Nullable Object value, @Nullable TriggerContext e, boolean local) {
        if (local) {
            assert e != null : name;
            var map = localVariables.get(e);
            if (map == null)
                localVariables.put(e, map = new VariableMap());
            map.setVariable(name, value);
        } else {
            variableMap.setVariable(name, value);
            if (!hasStorages())
                return;
            queueVariableChange(name, value);
            try {
                if (LOCK.tryLock())
                    processChangeQueue();
            } finally {
                LOCK.unlock();
            }
        }
    }

    public static void queueVariableChange(String name, @Nullable Object value) {
        if (!hasStorages())
            return;
        VARIABLE_CHANGE_QUEUE.add(new VariableChange(name, value));
    }

    /**
     * Processes all entries in variable change queue.
     * <p>
     * Note that caller must acquire write lock before calling this,
     * then release it.
     */
    private static void processChangeQueue() {
        while (true) {
            VariableChange change = VARIABLE_CHANGE_QUEUE.poll();
            if (change == null)
                break;

            variableMap.setVariable(change.name, change.value);
            STORAGES.stream()
                .filter(storage -> storage.accept(change.name))
                .forEach(storage -> {
                    SerializedVariable serialized = storage.serialize(change.name, change.value);
                    if (serialized == null) return;
                    storage.save(serialized);
                });
        }
    }

    public static void clearVariables() {
        variableMap.clearVariables();
    }

    /**
     * Copy local variables from one TriggerContext to another.
     *
     * @param from The source TriggerContext
     * @param to   The destination TriggerContext
     */
    public static void copyLocalVariables(TriggerContext from, TriggerContext to) {
        if (localVariables.containsKey(from)) {
            localVariables.put(to, localVariables.get(from));
        }
    }

    /**
     * Get a copy of local variables from a trigger.
     *
     * @param from TriggerContext to get variables from
     * @return A copy of local variables
     */
    public static VariableMap copyLocalVariables(TriggerContext from) {
        if (localVariables.containsKey(from)) {
            return localVariables.get(from);
        }
        return new VariableMap();
    }

    /**
     * Set the local variables for a specific TriggerContext.
     *
     * @param context The TriggerContext to set variables for
     * @param map     The VariableMap to set
     */
    public static void setLocalVariables(TriggerContext context, VariableMap map) {
        localVariables.put(context, map);
    }

    /**
     * Clear local variables for a specific TriggerContext.
     *
     * @param ctx The TriggerContext to clear variables for
     */
    public static void clearLocalVariables(TriggerContext ctx) {
        localVariables.remove(ctx);
    }

    /**
     * Represents a variable that is to be changed.
     * Key-value pair. Key being the variable name.
     *
     * @param name  The variable name of the changed variable.
     * @param value The value of the variable change.
     */
    private record VariableChange(String name, @Nullable Object value) {

        /**
         * Creates a new {@link VariableChange} with the given name and value.
         *
         * @param name  the variable name.
         * @param value the new variable value.
         */
        private VariableChange {
        }

    }

}
