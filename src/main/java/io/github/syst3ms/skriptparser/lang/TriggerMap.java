package io.github.syst3ms.skriptparser.lang;

import io.github.syst3ms.skriptparser.structures.functions.Functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A map that holds triggers based on a script and {@link TriggerContext}.
 */
public class TriggerMap {

    private static final Map<String, Map<Class<? extends TriggerContext>, List<Trigger>>> TRIGGERS = new HashMap<>();

    /**
     * Add a trigger to the map.
     *
     * @param scriptName Name of a script
     * @param context    Trigger context class to which the trigger should be added
     * @param trigger    Trigger to add
     */
    public static void addTrigger(String scriptName, Class<? extends TriggerContext> context, Trigger trigger) {
        TRIGGERS.computeIfAbsent(scriptName, k -> new HashMap<>()).computeIfAbsent(context, k -> new ArrayList<>()).add(trigger);
    }

    /**
     * Clear all triggers for a script.
     *
     * @param scriptName Script name to clear triggers for
     */
    public static void clearTriggers(String scriptName) {
        TRIGGERS.remove(scriptName);
        Functions.removeFunctions(scriptName);
    }

    /**
     * Get all the triggers associated with a script.
     *
     * @param scriptName Script name to get triggers for
     * @return Map of trigger contexts to triggers
     */
    public static Map<Class<? extends TriggerContext>, List<Trigger>> getTriggersByScript(String scriptName) {
        return TRIGGERS.get(scriptName);
    }

    /**
     * Get all loaded triggers.
     *
     * @return All triggers
     */
    public static List<Trigger> getAllTriggers() {
        return TRIGGERS.values().stream().flatMap(m -> m.values().stream()).flatMap(List::stream).toList();
    }

    /**
     * Get all triggers associated with a {@link TriggerContext}.
     *
     * @param context Trigger context to get triggers for
     * @return List of triggers
     */
    public static <T extends TriggerContext> List<Trigger> getTriggersByContext(Class<T> context) {
        return TRIGGERS.values().stream().flatMap(m -> m.getOrDefault(context, List.of()).stream()).toList();
    }

    /**
     * Call a trigger with a specific {@link TriggerContext}.
     *
     * @param context Trigger context to call triggers for
     * @param <T>     Trigger context type
     */
    public static <T extends TriggerContext> void callTriggersByContext(T context) {
        TRIGGERS.values().stream()
            .flatMap(m -> m.getOrDefault(context.getClass(), List.of()).stream())
            .forEach(trigger -> Statement.runAll(trigger, context));
    }

}
