package io.github.syst3ms.skriptparser;

import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;
import io.github.syst3ms.skriptparser.syntax.EvtTest;
import io.github.syst3ms.skriptparser.syntax.TestContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAddon extends SkriptAddon {
    private final Map<String,List<Trigger>> testTriggers = new HashMap<>();

    @Override
    public void handleTrigger(String scriptName, Trigger trigger) {
        SkriptEvent event = trigger.getEvent();

        if (!canHandleEvent(event))
            return;

        if (event instanceof EvtTest) {
            testTriggers.getOrDefault(scriptName, new ArrayList<>()).add(trigger);
        }
    }

    @Override
    public void finishedLoading() {
        for (Trigger trigger : testTriggers.values().stream().flatMap(List::stream).toList()) {
            Statement.runAll(trigger, new TestContext.SubTestContext());
        }

        // Clear triggers for next test.
        testTriggers.clear();
    }
}
