package io.github.syst3ms.skriptparser.structures.functions;

import io.github.syst3ms.skriptparser.docs.Documentation;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Functions {

    private static final Map<String, List<Function<?>>> functionsMap = new HashMap<>();

    private static final String JAVA_FUNCTION_NAME = "java_functions_dont_change";
    static final String FUNCTION_NAME_REGEX = "^[a-zA-Z0-9_]*";
    private static final Pattern FUNCTION_NAME_PATTERN = Pattern.compile(FUNCTION_NAME_REGEX);
    static final String FUNCTION_CALL_PATTERN = "<(" + Functions.FUNCTION_NAME_REGEX + ")\\((.*)\\)>";

    private Functions() {
    }

    public static List<Function<?>> getFunctions(String scriptName) {
        return functionsMap.getOrDefault(scriptName, List.of());
    }

    public static List<Function<?>> getAllFunctions() {
        return functionsMap.values().stream().flatMap(List::stream).toList();
    }

    public static List<Function<?>> getJavaFunctions() {
        return functionsMap.getOrDefault(JAVA_FUNCTION_NAME, List.of());
    }

    static void preRegisterFunction(ScriptFunction<?> function) {
        String scriptName = function.getScriptName();
        functionsMap.computeIfAbsent(scriptName, k -> new ArrayList<>()).add(function);
    }

    public static void registerFunction(ScriptFunction<?> function, Trigger trigger) {
        function.setTrigger(trigger);
    }

    public static void removeFunctions(String scriptName) {
        if (functionsMap.containsKey(scriptName)) {
            for (Function<?> function : functionsMap.get(scriptName)) {
                if (function instanceof ScriptFunction<?> sf) {
                    sf.setTrigger(null);
                }
            }
        }
        functionsMap.put(scriptName, new ArrayList<>());
    }

    public static void registerFunction(JavaFunction<?> function) {
        functionsMap.computeIfAbsent(JAVA_FUNCTION_NAME, k -> new ArrayList<>()).add(function);
    }

    public static boolean isValidFunction(ScriptFunction<?> function, SkriptLogger logger) {
        String scriptName = function.getScriptName();
        for (Function<?> registeredFunction : functionsMap.computeIfAbsent(scriptName, k -> new ArrayList<>())) {
            String registeredFunctionName = registeredFunction.getName();
            String providedFunctionName = function.getName();
            if (!registeredFunctionName.equals(providedFunctionName)) continue;
            if (registeredFunction instanceof JavaFunction<?>) { // java functions take precedence over any script function
                logger.error("A java function already exists with the name '" + providedFunctionName + "'.",
                    ErrorType.SEMANTIC_ERROR);
                return false;
            }
            ScriptFunction<?> registeredScriptFunction = (ScriptFunction<?>) registeredFunction;
            String registeredScriptName = registeredScriptFunction.getScriptName();
            if (!registeredScriptFunction.isLocal()) { // already registered function is global so it takes name precedence
                logger.error("A global script function named '" + providedFunctionName + "' already exists in " +
                    registeredScriptName + ".", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            if (!function.isLocal()) {
                // if a global function is trying to be defined when a local function already has that name, there will be problems in the script where the local function lies
                logger.error("A local script function named '" + providedFunctionName + "' already exists in " +
                    registeredScriptName + ".", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            if (registeredScriptName.equals(function.getScriptName())) {
                logger.error("Two local functions with the same name ('" + registeredFunctionName + "')" +
                    " can't exist in the same script.", ErrorType.SEMANTIC_ERROR);
                return false;
            }
        }
        return true;
    }

    public static Optional<Function<?>> getFunctionByName(String name, String scriptName) {
        // Find a JavaFunction
        for (Function<?> function : functionsMap.computeIfAbsent(JAVA_FUNCTION_NAME, k -> new ArrayList<>())) {
            if (function.getName().equals(name)) {
                return Optional.of(function);
            }
        }

        // Find a function in a script file
        if (scriptName.endsWith(".sk")) scriptName = scriptName.substring(0, scriptName.length() - 3);

        for (Function<?> registeredFunction : getAllFunctions()) {
            if (!registeredFunction.getName().equals(name))
                continue; // we don't care then!!!! goodbye continue to the next one
            if (registeredFunction instanceof ScriptFunction<?> registeredScriptFunction
                && registeredScriptFunction.isLocal()
                && !scriptName.equals(registeredScriptFunction.getScriptName())) {
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


    public static FunctionDefinition newJavaFunction(JavaFunction<?> function) {
        return new FunctionDefinition(function);
    }

    public static class FunctionDefinition {
        private final Documentation documentation = new Documentation();
        private final JavaFunction<?> function;

        public FunctionDefinition(JavaFunction<?> function) {
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
            Functions.registerFunction(this.function);
        }
    }
}
