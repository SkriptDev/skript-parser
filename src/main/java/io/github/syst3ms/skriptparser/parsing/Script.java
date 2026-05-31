package io.github.syst3ms.skriptparser.parsing;

import java.nio.file.Path;

/**
 * Represents a script file.
 * This record just encapsulates the file path of the script.
 *
 * @param scriptPath the {@link Path} pointing to the script file
 */
public record Script(Path scriptPath) implements Comparable<Script> {

    /**
     * Get the name of the script, without the {@code .sk} extension
     */
    public String scriptName() {
        String fileName = scriptPath.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        return lastDot == -1 ? fileName : fileName.substring(0, lastDot);
    }

    @Override
    public int compareTo(Script o) {
        return this.scriptPath.compareTo(o.scriptPath);
    }

}
