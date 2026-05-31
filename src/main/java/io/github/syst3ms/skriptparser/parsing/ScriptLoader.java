package io.github.syst3ms.skriptparser.parsing;

import io.github.syst3ms.skriptparser.file.FileElement;
import io.github.syst3ms.skriptparser.file.FileParser;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.file.VoidElement;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.lang.TriggerMap;
import io.github.syst3ms.skriptparser.lang.UnloadedTrigger;
import io.github.syst3ms.skriptparser.lang.event.SkriptEvent;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.util.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Contains the logic for loading, parsing, and interpreting entire script files
 */
public class ScriptLoader {

    private static final List<Script> LOADED_SCRIPTS = new ArrayList<>();

    /**
     * Get a collection of all currently loaded scripts.
     *
     * @return A collection of loaded Script objects
     */
    public static Collection<Script> getLoadedScripts() {
        return Collections.unmodifiableCollection(LOADED_SCRIPTS);
    }

    /**
     * Get a loaded Script object by its parsed name.
     * This is method is inconsistent since scripts with the same name in different directories can exist.
     *
     * @param scriptName The name of the script
     * @return The Script object, or null if not loaded
     */
    @Deprecated(forRemoval = true)
    public static @Nullable Script getScriptByName(String scriptName) {
        return LOADED_SCRIPTS.stream().filter(s -> s.scriptName().equals(scriptName)).findFirst().orElse(null);
    }

    /**
     * Parses and loads the provided script in memory.
     *
     * @param scriptPath the script file to load.
     * @param debug      whether debug is enabled.
     */
    public static List<LogEntry> loadScript(Path scriptPath, boolean debug) {
        return loadScript(scriptPath, new SkriptLogger(debug), debug);
    }

    /**
     * Parses and loads the provided script in memory.
     * The provided SkriptLogger can be used within syntaxes to input erroring into the logs during parse time.
     *
     * @param scriptPath the script file to load.
     * @param logger     The {@link SkriptLogger} to use for the logged entries. Useful for custom logging.
     * @param debug      whether debug is enabled.
     */
    public static List<LogEntry> loadScript(Path scriptPath, SkriptLogger logger, boolean debug) {
        List<FileElement> elements;

        Script oldScript = LOADED_SCRIPTS.stream()
            .filter(s -> s.scriptPath().equals(scriptPath))
            .findFirst()
            .orElse(null);

        if (oldScript != null) {
            // Clean up the old instance from everywhere
            TriggerMap.clearTriggers(oldScript);
            LOADED_SCRIPTS.remove(oldScript);
        }

        Script script = new Script(scriptPath);
        String scriptName = script.scriptName();

        try {
            var lines = FileUtils.readAllLines(scriptPath);

            LOADED_SCRIPTS.add(script);

            elements = FileParser.parseFileLines(scriptPath,
                lines,
                0,
                1,
                logger
            );
            logger.finalizeLogs();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        logger.setFileInfo(script, elements);
        List<UnloadedTrigger> unloadedTriggers = new ArrayList<>();

        for (var element : elements) {
            logger.finalizeLogs();
            logger.nextLine();
            if (element instanceof VoidElement)
                continue;
            if (element instanceof FileSection) {
                var trig = SyntaxParser.parseTrigger((FileSection) element, logger);
                trig.ifPresent(t -> {
                    logger.setLine(logger.getLine() + ((FileSection) element).length());
                    unloadedTriggers.add(t);
                });
            } else {
                logger.error(
                    "Can't have code outside of a trigger",
                    ErrorType.STRUCTURE_ERROR,
                    "Code always starts with a trigger (or event). Refer to the documentation to see which event you need, or indent this line so it is part of a trigger"
                );
            }
        }
        unloadedTriggers.sort((a, b) -> b.trigger().getEvent().getLoadingPriority() - a.trigger().getEvent().getLoadingPriority());

        for (var unloaded : unloadedTriggers) {
            logger.finalizeLogs();
            logger.setLine(unloaded.line());
            var loaded = unloaded.trigger();
            loaded.loadSection(unloaded.section(), unloaded.parserState(), logger);
            //unloaded.getEventInfo().getRegisterer().handleTrigger(scriptName,loaded);

            SkriptEvent event = unloaded.trigger().getEvent();

            Set<Class<? extends TriggerContext>> contexts = unloaded.eventInfo().getContexts();
            if (contexts.isEmpty()) {
                // A dummy context will be used for this
                TriggerMap.addTrigger(script, TriggerContext.class, loaded);
            } else {
                for (Class<? extends TriggerContext> context : contexts) {
                    TriggerMap.addTrigger(script, context, loaded);
                }
            }
        }
        logger.finalizeLogs();
        return logger.close();
    }

    /**
     * Parses all items inside of a given section.
     *
     * @param section the section
     * @param logger  the logger
     * @return a list of {@linkplain Statement effects} inside of the section
     */
    public static List<Statement> loadItems(FileSection section, ParserState parserState, SkriptLogger logger) {
        logger.recurse();
        parserState.recurseCurrentStatements();
        List<Statement> items = new ArrayList<>();
        var elements = section.getElements();
        for (var element : elements) {
            logger.finalizeLogs();
            logger.nextLine();
            if (element instanceof VoidElement)
                continue;
            if (element instanceof FileSection) {
                var codeSection = SyntaxParser.parseSection((FileSection) element, parserState, logger);
                if (codeSection.isEmpty()) {
                    continue;
                }

                parserState.addCurrentStatement(codeSection.get());
                items.add(codeSection.get());
            } else {
                var statement = SyntaxParser.parseEffect(element.getLineContent(), parserState, logger);
                if (statement.isEmpty())
                    continue;

                parserState.addCurrentStatement(statement.get());
                items.add(statement.get());
            }
        }
        logger.finalizeLogs();
        for (var i = items.size() - 1; i > 0; i--) {
            items.get(i - 1).setNext(items.get(i));
        }
        logger.callback();
        parserState.callbackCurrentStatements();
        return items;
    }

}
