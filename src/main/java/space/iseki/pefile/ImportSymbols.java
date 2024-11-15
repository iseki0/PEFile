package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class ImportSymbols implements Iterable<ImportSymbol> {
    private final PEFile peFile;
    private final ImportDirectoryTable idt;

    ImportSymbols(PEFile peFile, ImportDirectoryTable idt) {
        this.peFile = peFile;
        this.idt = idt;
    }

    @Override
    public @NotNull ImportSymbolIterator iterator() {
        return new ImportSymbolIterator(peFile, idt);
    }
}
