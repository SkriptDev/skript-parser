package io.github.syst3ms.skriptparser.structures.functions;

import io.github.syst3ms.skriptparser.docs.Documentation;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.Script;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Functions {

    static final String FUNCTION_NAME_REGEX = "^[a-zA-Z0-9_]*";
    static final String FUNCTION_CALL_PATTERN = "<(" + Functions.FUNCTION_NAME_REGEX + ")\\((.*)\\)>";
    private static final Map<Script, List<Function<?>>> functionsMap = new HashMap<>();
    private static final Map<SkriptAddon, List<Function<?>>> FUNCTIONS_BY_ADDON = new HashMap<>();
    private static final Script JAVA_FUNCTIONS_SCRIPT = new Script(Path.of("java_functions_dont_change.sk"));
    private static final Pattern FUNCTION_NAME_PATTERN = Pattern.compile(FUNCTION_NAME_REGEX);

    private Functions() {
    }

    /**
     * @deprecated Use {@link #getFunctions(Script)} instead.
     */
    @Deprecated(forRemoval = true)
    public static List<Function<?>> getFunctions(String scriptName) {
        Script script = ScriptLoader.getScriptByName(scriptName);
        return script != null ? getFunctions(script) : List.of();
    }

    /**
     * Get all functions associated with a script.
     *
     * @param script Script to get functions for
     * @return List of functions
     */
    public static List<Function<?>> getFunctions(Script script) {
        return functionsMap.getOrDefault(script, List.of());
    }

    public static List<Function<?>> getAllFunctions() {
        return functionsMap.values().stream().flatMap(List::stream).toList();
    }

    public static List<Function<?>> getJavaFunctions(SkriptRegistration registration) {
        return FUNCTIONS_BY_ADDON.getOrDefault(registration.getRegisterer(), List.of());
    }

    static void preRegisterFunction(ScriptFunction<?> function) {
        Script script = function.getScript();
        functionsMap.computeIfAbsent(script, k -> new ArrayList<>()).add(function);
    }

    public static void registerFunction(ScriptFunction<?> function, Trigger trigger) {
        function.setTrigger(trigger);
    }

    /**
     * @deprecated Use {@link #removeFunctions(Script)} instead.
     */
    @Deprecated(forRemoval = true)
    public static void removeFunctions(String scriptName) {
        Script script = ScriptLoader.getScriptByName(scriptName);
        if (script != null) removeFunctions(script);
    }

    /**
     * Remove all functions for a script.
     *
     * @param script Script to remove functions for
     */
    public static void removeFunctions(Script script) {
        if (functionsMap.containsKey(script)) {
            for (Function<?> function : functionsMap.get(script)) {
                if (function instanceof ScriptFunction<?> sf) {
                    sf.setTrigger(null);
                }
            }
            functionsMap.remove(script);
        }
    }

    public static void registerFunction(SkriptRegistration registration, JavaFunction<?> function) {
        functionsMap.computeIfAbsent(JAVA_FUNCTIONS_SCRIPT, k -> new ArrayList<>()).add(function);
        FUNCTIONS_BY_ADDON.computeIfAbsent(registration.getRegisterer(), k -> new ArrayList<>()).add(function);
    }

    public static boolean isValidFunction(ScriptFunction<?> function, SkriptLogger logger) {
        Script script = function.getScript();
        for (Function<?> registeredFunction : functionsMap.computeIfAbsent(script, k -> new ArrayList<>())) {
            String registeredFunctionName = registeredFunction.getName();
            String providedFunctionName = function.getName();
            if (!registeredFunctionName.equals(providedFunctionName)) continue;
            if (registeredFunction instanceof JavaFunction<?>) { // java functions take precedence over any script function
                logger.error("A java function already exists with the name '" + providedFunctionName + "'.",
                    ErrorType.SEMANTIC_ERROR);
                return false;
            }
            ScriptFunction<?> registeredScriptFunction = (ScriptFunction<?>) registeredFunction;
            Script registeredScript = registeredScriptFunction.getScript();
            if (!registeredScriptFunction.isLocal()) { // already registered function is global so it takes name precedence
                logger.error("A global script function named '" + providedFunctionName + "' already exists in " +
                    registeredScript + ".", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            if (!function.isLocal()) {
                // if a global function is trying to be defined when a local function already has that name, there will be problems in the script where the local function lies
                logger.error("A local script function named '" + providedFunctionName + "' already exists in " +
                    registeredScript + ".", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            if (registeredScript.equals(function.getScript())) {
                logger.error("Two local functions with the same name ('" + registeredFunctionName + "')" +
                    " can't exist in the same script.", ErrorType.SEMANTIC_ERROR);
                return false;
            }
        }
        return true;
    }

    /**
     * @deprecated Use {@link #getFunctionByName(String, Script)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Optional<Function<?>> getFunctionByName(String name, String scriptName) {
        if (scriptName.endsWith(".sk")) scriptName = scriptName.substring(0, scriptName.length() - 3); // old functionality did this too
        Script script = ScriptLoader.getScriptByName(scriptName);
        return script != null ? getFunctionByName(name, script) : Optional.empty();
    }

    public static Optional<Function<?>> getFunctionByName(String name, Script script) {
        // Find a JavaFunction
        for (Function<?> function : functionsMap.computeIfAbsent(JAVA_FUNCTIONS_SCRIPT, k -> new ArrayList<>())) {
            if (function.getName().equals(name)) {
                return Optional.of(function);
            }
        }

        // Find a function in a script file
        for (Function<?> registeredFunction : getAllFunctions()) {
            if (!registeredFunction.getName().equals(name))
                continue; // we don't care then!!!! goodbye continue to the next one
            if (registeredFunction instanceof ScriptFunction<?> registeredScriptFunction
                && registeredScriptFunction.isLocal()
                && !script.equals(registeredScriptFunction.getScript())) {
                continue;
                //return Optional.of(registeredFunction); handled below
            }
            return Optional.of(registeredFunction); // java function or global script function at this point
        }
        return Optional.empty();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidFunctionName(String name) {
        return FUNCTION_NAME_PATTERN.matcher(name).matches();
    }


    /**
     * Use {@link SkriptRegistration#newJavaFunction(String, Class, boolean)} instead
     */
    @Deprecated(forRemoval = true)
    public static FunctionDefinition newJavaFunction(SkriptRegistration registration, JavaFunction<?> function) {
        return new FunctionDefinition(registration, function);
    }

    @Deprecated(forRemoval = true)
    public static class FunctionDefinition {
        private final SkriptRegistration registration;
        private final Documentation documentation = new Documentation();
        private final JavaFunction<?> function;

        public FunctionDefinition(SkriptRegistration registration, JavaFunction<?> function) {
            this.registration = registration;
            this.function = function;
        }

        public FunctionDefinition name(String name) {
            this.documentation.setName(name);
            return this;
        }

        public FunctionDefinition noDoc() {
            this.documentation.noDoc();
            return this;
        }

        public FunctionDefinition experimental() {
            this.documentation.experimental();
            return this;
        }

        public FunctionDefinition experimental(String message) {
            this.documentation.experimental(message);
            return this;
        }

        public FunctionDefinition description(String... description) {
            this.documentation.setDescription(description);
            return this;
        }

        public FunctionDefinition usage(String usage) {
            this.documentation.setUsage(usage);
            return this;
        }

        public FunctionDefinition examples(String... examples) {
            this.documentation.setExamples(examples);
            return this;
        }

        public FunctionDefinition since(String since) {
            this.documentation.setSince(since);
            return this;
        }

        public void register() {
            this.function.setDocumentation(this.documentation);
            Functions.registerFunction(this.registration, this.function);
        }
    }

}
