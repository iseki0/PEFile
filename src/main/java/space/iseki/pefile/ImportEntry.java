package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ImportEntry {
    private final ImportDirectoryTable idt;
    private final @Nullable String name;
    private final PEFile peFile;

    ImportEntry(PEFile peFile, ImportDirectoryTable idt, String name) {
        this.idt = idt;
        this.peFile = peFile;
        this.name = name;
    }

    public @Nullable String getName() {
        return name;
    }

    @Override
    public String toString() {
        return U.structure("ImportEntry", name != null ? U.fieldEscape("name", name) : "null", U.field("idt", idt));
    }

    public @NotNull Iterable<@NotNull ImportSymbol> symbols() {
        return new ImportSymbols(peFile, idt);
    }
}

record ImportDirectoryTable(int importLookupTableRva,
                            int timeDateStamp,
                            int forwarderChain,
                            int nameRva,
                            int importAddressTableRva) {
    public static final int LENGTH = 20;

    /**
     * Parse ImportDirectoryTable from a byte array
     *
     * @param buf byte array
     * @param off offset
     * @return ImportDirectoryTable
     * @throws IndexOutOfBoundsException if the buffer is too small
     */
    static ImportDirectoryTable parse(byte[] buf, int off) {
        return new ImportDirectoryTable(I.u32(buf, off),
                                        I.u32(buf, off + 4),
                                        I.u32(buf, off + 8),
                                        I.u32(buf, off + 12),
                                        I.u32(buf, off + 16));
    }

    static List<ImportDirectoryTable> parseList(byte[] buf, int off) {
        var list = new ArrayList<ImportDirectoryTable>();
        var breakFlag = false;
        for (int i = off; i < buf.length; i += LENGTH) {
            var parsed = parse(buf, i);
            if (parsed.importLookupTableRva == 0) {
                breakFlag = true;
                break;
            }
            list.add(parsed);
        }
        if (!breakFlag) {
            throw new PEFileException("ImportDirectoryTable list is not terminated by a null entry");
        }
        return list;
    }

    @Override
    public String toString() {
        return U.structure("ImportDirectoryTable",
                           U.field("importLookupTableRva", importLookupTableRva),
                           U.field("timeDateStamp", U.timeDateU32(timeDateStamp)),
                           U.field("forwarderChain", forwarderChain),
                           U.field("nameRva", nameRva),
                           U.field("importAddressTableRva", importAddressTableRva));
    }

}

