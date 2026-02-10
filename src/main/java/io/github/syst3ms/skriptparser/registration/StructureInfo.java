package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.docs.Documentation;
import io.github.syst3ms.skriptparser.lang.Structure;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.pattern.PatternElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class containing info about a {@link Structure} syntax
 *
 * @param <E> the {@link Structure} class
 */
public class StructureInfo<E extends Structure> extends SkriptEventInfo<E> {

    public StructureInfo(SkriptAddon registerer, Class<E> c, Set<Class<? extends TriggerContext>> handledContexts, int priority, List<PatternElement> patterns, Documentation documentation, Map<String, Object> data) {
        super(registerer, c, handledContexts, priority, patterns, documentation, data);
    }

}
