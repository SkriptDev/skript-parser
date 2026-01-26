package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.lang.event.StartOnLoadEvent;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;

/**
 * The {@link SkriptAddon} representing Skript itself
 */
public class Skript extends SkriptAddon {

    private final String[] mainArgs;

    public Skript(String[] mainArgs) {
        this.mainArgs = mainArgs;
    }

    @Override
    public void finishedLoading() {
        ScriptLoader.getTriggerMap().values().forEach(triggers ->
            triggers.forEach(trigger -> {
                if (trigger.getEvent() instanceof StartOnLoadEvent event) {
                    event.onInitialLoad(trigger);
                }
            }));
    }

}
