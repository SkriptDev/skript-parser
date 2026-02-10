package io.github.syst3ms.skriptparser.registration;

import io.github.syst3ms.skriptparser.docs.Documentation;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SyntaxElement;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.pattern.PatternElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class containing info about a {@link SyntaxElement} that isn't an {@link Expression} or an {@link SkriptEvent}
 *
 * @param <C> the {@link SyntaxElement} class
 */
public class SyntaxInfo<C> {
    protected final Map<String, Object> data;
    private final Class<C> c;
    private final List<PatternElement> patterns;
    private final int priority;
    private final SkriptAddon registerer;
    private final Documentation documentation;

    public SyntaxInfo(SkriptAddon registerer, Class<C> c, int priority, List<PatternElement> patterns, Documentation documentation) {
        this(registerer, c, priority, patterns, documentation, new HashMap<>());
    }

    public SyntaxInfo(SkriptAddon registerer, Class<C> c, int priority, List<PatternElement> patterns, Documentation documentation, Map<String, Object> data) {
        this.c = c;
        this.patterns = patterns;
        this.priority = priority;
        this.registerer = registerer;
        this.documentation = documentation;
        this.data = data;
    }

    public SkriptAddon getRegisterer() {
        return registerer;
    }

    public Class<C> getSyntaxClass() {
        return c;
    }

    public int getPriority() {
        return priority;
    }

    public List<PatternElement> getPatterns() {
        return patterns;
    }

    public Documentation getDocumentation() {
        return this.documentation;
    }

    /**
     * Retrieves a data instance by its identifier.
     *
     * @param identifier the identifier
     * @param type       the expected data type
     * @return the data instance
     */
    public <T> T getData(String identifier, Class<T> type) {
        return type.cast(data.get(identifier));
    }
}
