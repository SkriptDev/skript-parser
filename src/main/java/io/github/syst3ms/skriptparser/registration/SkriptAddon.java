package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.Skript;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base for all addons, modules that hook into the API to register syntax and handle triggers.
 */
public abstract class SkriptAddon {

    private final List<Class<? extends SkriptEvent>> handledEvents = new ArrayList<>();
    private static final Map<String, SkriptAddon> ADDONS = new HashMap<>();

    /**
     * Returns an unmodifiable list of all SkriptAddons that are registered globally.
     *
     * @return SkriptAddons that are registered.
     */
    public static @NotNull List<SkriptAddon> getAddons() {
        return ADDONS.values().stream().toList();
    }

    /**
     * Get a SkriptAddon by its name.
     *
     * @param name Name of addon to get
     * @return SkriptAddon with the given name, or null if not found
     */
    public static @Nullable SkriptAddon getAddon(String name) {
        return ADDONS.get(name);
    }

    private final String addonName;

    public SkriptAddon(String name) {
        if (ADDONS.containsKey(name)) {
            throw new IllegalArgumentException("An addon with the name " + name + " is already registered!");
        }
        this.addonName = name;
        ADDONS.put(name, this);
    }

    public final String getAddonName() {
        return this.addonName;
    }

    /**
     * Is called when a script has finished loading. Optionally overridable.
     */
    public void finishedLoading(@Nullable String scriptName) {
    }

    /**
     * Is called when all scripts have finished loading. Optionally overridable.
     */
    public void finishedLoading() {
        finishedLoading(null);
    }

    /**
     * Checks to see whether the given event has been registered by this SkriptAddon ; a basic way to filter out
     * triggers you aren't able to deal with in {@link SkriptAddon#handleTrigger(String, Trigger)}.
     * A simple example of application can be found in {@link Skript#handleTrigger(Trigger)}.
     *
     * @param event the event to check
     * @return whether the event can be handled by the addon or not
     * @see Skript#handleTrigger(Trigger)
     */
    public final boolean canHandleEvent(SkriptEvent event) {
        return handledEvents.contains(event.getClass());
    }

    void addHandledEvent(Class<? extends SkriptEvent> event) {
        handledEvents.add(event);
    }

}
