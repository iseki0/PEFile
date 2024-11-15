package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

public class PEFileException extends RuntimeException {
    public PEFileException(@NotNull String message) {
        super(message);
    }
}
