package io.github.syst3ms.skriptparser.config;

import io.github.syst3ms.skriptparser.file.FileElement;
import io.github.syst3ms.skriptparser.file.FileParser;
import io.github.syst3ms.skriptparser.file.FileSection;
import io.github.syst3ms.skriptparser.lang.entries.OptionLoader;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import io.github.syst3ms.skriptparser.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Config {

    @SuppressWarnings("FieldCanBeLocal")
    private final SkriptLogger logger;
    private final List<FileElement> fileElements;

    public Config(Path path, String resourceToCopy, SkriptLogger logger) {
        this.logger = logger;
        File file = path.toFile();
        if (!file.exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("Failed to create directory for config file");
            }
            try {
                InputStream resourceAsStream = Config.class.getResourceAsStream(resourceToCopy);
                if (resourceAsStream == null) {
                    throw new IllegalArgumentException("Resource not found: " + resourceToCopy);
                }
                Files.copy(resourceAsStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy resource to config file", e);
            }
        }

        List<String> strings;
        try {
            strings = FileUtils.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.fileElements = FileParser.parseFileLines(path.toString(), strings, 0, 1, logger);
        logger.debug("Loaded config file at " + path + " with " + fileElements.size() + " lines");
    }

    public @Nullable ConfigSection getConfigSection(String name) {
        for (FileElement fileElement : this.fileElements) {
            if (fileElement instanceof FileSection sec && sec.getLineContent().split(OptionLoader.OPTION_SPLIT_PATTERN)[0].equals(name)) {
                return new ConfigSection(name, sec);
            }
        }
        return null;
    }

    public @Nullable String getString(String key) {
        return getString(key, null);
    }

    public @Nullable String getString(String key, String defaultValue) {
        String configValue = getConfigValue(key, String.class);
        if (configValue == null) return defaultValue;
        return configValue;
    }

    public int getInt(String key) {
        return getInt(key, -1);
    }

    public int getInt(String key, int defaultValue) {
        Integer configValue = getConfigValue(key, Integer.class);
        return configValue != null ? configValue : defaultValue;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean configValue = getConfigValue(key, Boolean.class);
        return configValue != null ? configValue : defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getConfigValue(String key, Class<T> classType) {
        for (FileElement fileElement : this.fileElements) {
            if (fileElement instanceof FileSection sec) {
                String s = sec.getLineContent().split(OptionLoader.OPTION_SPLIT_PATTERN)[0];
                ConfigSection configSection = new ConfigSection(s, sec);
                T value = configSection.getConfigValue(key, classType);
                if (value != null)
                    return value;
            } else {
                String[] split = fileElement.getLineContent().split(OptionLoader.OPTION_SPLIT_PATTERN);
                if (split.length < 2) {
                    continue;
                }
                if (!split[0].equals(key)) {
                    continue;
                }

                String content = split[1];
                if (classType.equals(String.class)) {
                    return (T) content;
                } else if (classType.equals(Boolean.class)) {
                    boolean b = Boolean.parseBoolean(content);
                    return (T) Boolean.valueOf(b);
                } else if (classType.equals(Integer.class)) {
                    try {
                        int i = Integer.parseInt(content);
                        return (T) Integer.valueOf(i);
                    } catch (NumberFormatException e) {
                        return (T) Integer.valueOf(-1);
                    }
                }

                Optional<? extends Type<T>> type = TypeManager.getByClassExact(classType);
                if (type.isEmpty()) {
                    return null;
                }

                Optional<Function<String, ? extends T>> parser = type.get().getLiteralParser();
                return parser.<T>map(stringFunction -> stringFunction.apply(content)).orElse(null);

            }
        }
        return null;
    }

    public SkriptLogger getLogger() {
        return this.logger;
    }

    public static class ConfigSection {
        private final String name;
        private final FileSection fileSection;

        public ConfigSection(String name, FileSection fileSection) {
            this.name = name;
            this.fileSection = fileSection;
        }

        public @NotNull String getName() {
            return this.name;
        }

        public @Nullable ConfigSection getSection(String name) {
            for (FileElement element : this.fileSection.getElements()) {
                if (element instanceof FileSection sec && sec.getLineContent().split(OptionLoader.OPTION_SPLIT_PATTERN)[0].equals(name)) {
                    return new ConfigSection(name, sec);
                }
            }
            return null;
        }

        public @NotNull List<ConfigSection> getSections() {
            List<ConfigSection> sections = new ArrayList<>();
            for (FileElement element : this.fileSection.getElements()) {
                if (element instanceof FileSection sec) {
                    String s = sec.getLineContent().split(OptionLoader.OPTION_SPLIT_PATTERN)[0];
                    sections.add(new ConfigSection(s, sec));
                }
            }

            return sections;
        }

        public @Nullable String getString(String key) {
            return getString(key, null);
        }

        public @Nullable String getString(String key, String defaultValue) {
            String configValue = getConfigValue(key, String.class);
            if (configValue == null) return defaultValue;
            return configValue;
        }

        public int getInt(String key) {
            return getInt(key, -1);
        }

        public int getInt(String key, int defaultValue) {
            Integer configValue = getConfigValue(key, Integer.class);
            return configValue != null ? configValue : defaultValue;
        }

        public boolean getBoolean(String key) {
            return getBoolean(key, false);
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            Boolean configValue = getConfigValue(key, Boolean.class);
            return configValue != null ? configValue : defaultValue;
        }

        @SuppressWarnings("unchecked")
        public <T> @Nullable T getConfigValue(String key, Class<T> classType) {
            Optional<FileElement> value = this.fileSection.get(key);
            if (value.isEmpty()) {
                return null;
            }
            String[] split = value.get().getLineContent().split(OptionLoader.OPTION_SPLIT_PATTERN);
            if (split.length < 2) {
                return null;
            }

            if (!split[0].equals(key)) {
                return null;
            }

            String content = split[1];
            if (classType.equals(String.class)) {
                return (T) content;
            } else if (classType.equals(Boolean.class)) {
                boolean b = Boolean.parseBoolean(content);
                return (T) Boolean.valueOf(b);
            } else if (classType.equals(Integer.class)) {
                try {
                    int i = Integer.parseInt(content);
                    return (T) Integer.valueOf(i);
                } catch (NumberFormatException e) {
                    return (T) Integer.valueOf(-1);
                }
            }

            Optional<? extends Type<T>> type = TypeManager.getByClassExact(classType);
            if (type.isEmpty()) {
                return null;
            }

            Optional<Function<String, ? extends T>> parser = type.get().getLiteralParser();
            return parser.<T>map(stringFunction -> stringFunction.apply(content)).orElse(null);
        }
    }

}
