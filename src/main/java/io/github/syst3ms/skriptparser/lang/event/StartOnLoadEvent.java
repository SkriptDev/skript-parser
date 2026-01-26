package io.github.syst3ms.skriptparser.lang.event;

import io.github.syst3ms.skriptparser.lang.Trigger;

/**
 * Represents an event that initially fires when the script is loaded.
 */
public interface StartOnLoadEvent {

    /**
     * Fired when a script is loaded.
     *
     * @param trigger The trigger that is fired when the script is loaded
     */
    void onInitialLoad(Trigger trigger);

}
