package space.iseki.pefile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class ExportTable {
    private static final VarHandle TABLE;

    static {
        try {
            TABLE = MethodHandles.lookup()
                                 .findVarHandle(ExportTable.class, "table", ExportDirectoryTable.class)
                                 .withInvokeExactBehavior();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    private final PEFile peFile;
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private ExportDirectoryTable table;

    ExportTable(PEFile peFile) {
        this.peFile = peFile;
    }

    private ExportDirectoryTable getTable() {
        try {
            ExportDirectoryTable d = (ExportDirectoryTable) TABLE.getAcquire(this);
            if (d != null) {
                return d;
            }
            DataDirectories.Item exportTable = peFile.dataDirectories.getExportTable();
            if (exportTable == null || exportTable.getRva() == 0) {
                return null;
            }
            var data = new byte[ExportDirectoryTable.LENGTH];
            peFile.sectionSet.readBytes(data, exportTable.getRva());
            ExportDirectoryTable t = ExportDirectoryTable.parse(data, 0);
            TABLE.setRelease(this, t);
            return t;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    record ExportDirectoryTable(int exportFlags,
                                int timeDateStamp,
                                short majorVersion,
                                short minorVersion,
                                int nameRva,
                                int ordinalBase,
                                int addressTableEntries,
                                int numberOfNamePointers,
                                int exportAddressTableRva,
                                int namePointerRva,
                                int ordinalTableRva) {
        public static final int LENGTH = 40;

        public static ExportDirectoryTable parse(byte[] data, int off) {
            int exportFlags = I.u32(data, off);
            int timeDateStamp = I.u32(data, off + 4);
            short majorVersion = I.u16(data, off + 8);
            short minorVersion = I.u16(data, off + 10);
            int nameRva = I.u32(data, off + 12);
            int ordinalBase = I.u32(data, off + 16);
            int addressTableEntries = I.u32(data, off + 20);
            int numberOfNamePointers = I.u32(data, off + 24);
            int exportAddressTableRva = I.u32(data, off + 28);
            int namePointerRva = I.u32(data, off + 32);
            int ordinalTableRva = I.u32(data, off + 36);
            if (exportFlags != 0) {
                throw new PEFileException("Invalid export flags: " + Integer.toHexString(exportFlags));
            }
            return new ExportDirectoryTable(exportFlags,
                                            timeDateStamp,
                                            majorVersion,
                                            minorVersion,
                                            nameRva,
                                            ordinalBase,
                                            addressTableEntries,
                                            numberOfNamePointers,
                                            exportAddressTableRva,
                                            namePointerRva,
                                            ordinalTableRva);
        }
    }
}
