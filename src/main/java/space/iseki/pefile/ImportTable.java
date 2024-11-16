package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class ImportTable implements Iterable<ImportEntry> {
    private final PEFile peFile;

    ImportTable(PEFile peFile) {
        this.peFile = peFile;
    }

    @Override
    public @NotNull Iterator<@NotNull ImportEntry> iterator() {
        return new ImportTableIterator(peFile);
    }
}
