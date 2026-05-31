package io.github.syst3ms.skriptparser.file;

import java.nio.file.Path;

/**
 * A {@link FileElement} representing a blank line.
 */
public class VoidElement extends FileElement {

    /**
     * @deprecated Use {@link #VoidElement(Path, int, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public VoidElement(String fileName, int line, int indentation) {
        this(Path.of(fileName), line, indentation);
    }

    public VoidElement(Path filePath, int line, int indentation) {
        super(filePath, line, "", indentation);
    }
}
