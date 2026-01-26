package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.event.AtTimeContext;
import io.github.syst3ms.skriptparser.event.EvtAtTime;
import io.github.syst3ms.skriptparser.event.EvtPeriodical;
import io.github.syst3ms.skriptparser.event.EvtScriptLoad;
import io.github.syst3ms.skriptparser.event.EvtWhen;
import io.github.syst3ms.skriptparser.event.PeriodicalContext;
import io.github.syst3ms.skriptparser.event.ScriptLoadContext;
import io.github.syst3ms.skriptparser.event.WhenContext;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;
import io.github.syst3ms.skriptparser.util.DurationUtils;
import io.github.syst3ms.skriptparser.util.ThreadUtils;
import io.github.syst3ms.skriptparser.util.Time;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link SkriptAddon} representing Skript itself
 */
public class Skript extends SkriptAddon {

    private final String[] mainArgs;

    private final Map<String, List<Trigger>> mainTriggers = new HashMap<>();
    private final Map<String, List<Trigger>> periodicalTriggers = new HashMap<>();
    private final Map<String, List<Trigger>> whenTriggers = new HashMap<>();
    private final Map<String, List<Trigger>> atTimeTriggers = new HashMap<>();

    public Skript(String[] mainArgs) {
        this.mainArgs = mainArgs;
    }

    @Override
    public void handleTrigger(String scriptName, Trigger trigger) {
        SkriptEvent event = trigger.getEvent();

        if (!canHandleEvent(event))
            return;

        if (event instanceof EvtScriptLoad) {
            mainTriggers.getOrDefault(scriptName, new ArrayList<>()).add(trigger);
        } else if (event instanceof EvtPeriodical) {
            periodicalTriggers.getOrDefault(scriptName, new ArrayList<>()).add(trigger);
        } else if (event instanceof EvtWhen) {
            whenTriggers.getOrDefault(scriptName, new ArrayList<>()).add(trigger);
        } else if (event instanceof EvtAtTime) {
            atTimeTriggers.getOrDefault(scriptName, new ArrayList<>()).add(trigger);
        }
    }

    @Override
    public void finishedLoading() {
        for (Trigger trigger : mainTriggers.values().stream().flatMap(List::stream).toList()) {
            Statement.runAll(trigger, new ScriptLoadContext(mainArgs));
        }
        for (Trigger trigger : periodicalTriggers.values().stream().flatMap(List::stream).toList()) {
            var ctx = new PeriodicalContext();
            var dur = ((EvtPeriodical) trigger.getEvent()).getDuration().getSingle().orElseThrow(AssertionError::new);
            ThreadUtils.runPeriodically(() -> Statement.runAll(trigger, ctx), dur);
        }
        for (Trigger trigger : whenTriggers.values().stream().flatMap(List::stream).toList()) {
            var ctx = new WhenContext();
            ThreadUtils.runPeriodically(() -> Statement.runAll(trigger, ctx), Duration.ofMillis(DurationUtils.TICK));
        }
        for (Trigger trigger : atTimeTriggers.values().stream().flatMap(List::stream).toList()) {
            var ctx = new AtTimeContext();
            var time = ((EvtAtTime) trigger.getEvent()).getTime().getSingle().orElseThrow(AssertionError::new);
            var initialDelay = (Time.now().getTime().isAfter(time.getTime())
                ? Time.now().difference(Time.LATEST).plus(time.difference(Time.MIDNIGHT))
                : Time.now().difference(time));
            ThreadUtils.runPeriodically(() -> Statement.runAll(trigger, ctx), initialDelay, Duration.ofDays(1));
        }
    }
}
