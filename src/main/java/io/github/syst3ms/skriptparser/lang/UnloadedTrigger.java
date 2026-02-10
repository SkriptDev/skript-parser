package io.github.syst3ms.skriptparser.lang;

import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.parsing.ParserState;
import io.github.syst3ms.skriptparser.registration.SkriptEventInfo;

/**
 * A {@link Trigger trigger}-to-be whose contents haven't been loaded yet. It will be loaded based on its event's
 * {@link SkriptEvent#getLoadingPriority() loading priority}.
 */
public record UnloadedTrigger(Trigger trigger, FileSection section, int line, SkriptEventInfo<?> eventInfo,
                              ParserState parserState) {
}
