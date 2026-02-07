package io.github.syst3ms.skriptparser.event;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.event.StartOnLoadEvent;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.util.ThreadUtils;

import java.time.Duration;

/**
 * The periodical event.
 * This will be triggered after each interval of a certain duration.
 * Note that when the duration is very precise, like milliseconds, it may be executed a bit later.
 * Large duration that go up to days are not recommended.
 *
 * @author Mwexim
 * @name Periodical
 * @type EVENT
 * @pattern every %*duration%
 * @since ALPHA
 */
public class EvtPeriodical extends SkriptEvent implements StartOnLoadEvent {
    static {
        Parser.getMainRegistration()
            .newEvent(EvtPeriodical.class, "*every %*duration%")
            .setHandledContexts(PeriodicalContext.class)
            .name("Periodical")
            .description("Triggered every interval of a certain duration.")
            .since("1.0.0")
            .register();
    }

    private Literal<Duration> duration;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, ParseContext parseContext) {
        duration = (Literal<Duration>) expressions[0];
        return true;
    }

    @Override
    public boolean check(TriggerContext ctx) {
        return ctx instanceof PeriodicalContext && duration.getSingle(ctx).isPresent();
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        return "every " + duration.toString(ctx, debug);
    }

    public Literal<Duration> getDuration() {
        return duration;
    }

    @Override
    public void onInitialLoad(Trigger trigger) {
        var ctx = new PeriodicalContext();
        var dur = getDuration().getSingle().orElseThrow(AssertionError::new);
        ThreadUtils.runPeriodically(() -> TriggerMap.callTriggersByContext(ctx), dur);
    }
}
