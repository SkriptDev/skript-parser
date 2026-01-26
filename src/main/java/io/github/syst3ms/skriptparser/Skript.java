package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.event.StartOnLoadEvent;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The {@link SkriptAddon} representing Skript itself
 */
public class Skript extends SkriptAddon {

    private final String[] mainArgs;

    public Skript(String[] mainArgs) {
        this.mainArgs = mainArgs;
    }

    @Override
    public void finishedLoading(@Nullable String scriptName) {
        List<Trigger> triggers;
        if (scriptName == null) {
            triggers = ScriptLoader.getTriggerMap().values().stream().flatMap(List::stream).toList();
        } else {
            triggers = ScriptLoader.getTriggerMap().get(scriptName);
        }
        triggers.forEach(trigger -> {
            if (trigger.getEvent() instanceof StartOnLoadEvent event) {
                event.onInitialLoad(trigger);
            }
        });
    }

}
