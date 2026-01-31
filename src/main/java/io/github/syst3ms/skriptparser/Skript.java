package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
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
        super("Skript-Parser");
        this.mainArgs = mainArgs;
    }

    @Override
    public void finishedLoading(@Nullable String scriptName) {
        List<Trigger> triggers;
        if (scriptName == null) {
            triggers = TriggerMap.getAllTriggers();
        } else {
            triggers = TriggerMap.getTriggersByScript(scriptName).values().stream().flatMap(List::stream).toList();
        }
        triggers.forEach(trigger -> {
            if (trigger.getEvent() instanceof StartOnLoadEvent event) {
                event.onInitialLoad(trigger);
            }
        });
    }

}
