package space.iseki.pefile;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class DataDirectories {
    @Field
    private final @Nullable Item exportTable;
    @Field
    private final @Nullable Item importTable;
    @Field
    private final @Nullable Item resourceTable;
    @Field
    private final @Nullable Item exceptionTable;
    @Field
    private final @Nullable Item certificateTable;
    @Field
    private final @Nullable Item baseRelocationTable;
    @Field
    private final @Nullable Item debug;
    @Field
    private final @Nullable Item architecture;
    @Field
    private final @Nullable Item globalPtr;
    @Field
    private final @Nullable Item tlsTable;
    @Field
    private final @Nullable Item loadConfigTable;
    @Field
    private final @Nullable Item boundImport;
    @Field
    private final @Nullable Item iat;
    @Field
    private final @Nullable Item delayImportDescriptor;
    @Field
    private final @Nullable Item clrRuntimeHeader;
    private final @Nullable Item reserved;
    private final int length;

    DataDirectories(@Nullable Item exportTable,
                    @Nullable Item importTable,
                    @Nullable Item resourceTable,
                    @Nullable Item exceptionTable,
                    @Nullable Item certificateTable,
                    @Nullable Item baseRelocationTable,
                    @Nullable Item debug,
                    @Nullable Item architecture,
                    @Nullable Item globalPtr,
                    @Nullable Item tlsTable,
                    @Nullable Item loadConfigTable,
                    @Nullable Item boundImport,
                    @Nullable Item iat,
                    @Nullable Item delayImportDescriptor,
                    @Nullable Item clrRuntimeHeader,
                    @Nullable Item reserved,
                    int length) {
        this.exportTable = exportTable;
        this.importTable = importTable;
        this.resourceTable = resourceTable;
        this.exceptionTable = exceptionTable;
        this.certificateTable = certificateTable;
        this.baseRelocationTable = baseRelocationTable;
        this.debug = debug;
        this.architecture = architecture;
        this.globalPtr = globalPtr;
        this.tlsTable = tlsTable;
        this.loadConfigTable = loadConfigTable;
        this.boundImport = boundImport;
        this.iat = iat;
        this.delayImportDescriptor = delayImportDescriptor;
        this.clrRuntimeHeader = clrRuntimeHeader;
        this.reserved = reserved;
        this.length = length;
    }

    private static String s(Item d) {
        if (d == null) return "null";
        return Integer.toUnsignedString(d.getRva()) +
               ".." +
               Integer.toUnsignedString(d.getRva() + d.getSize()) +
               "(size: " +
               Integer.toUnsignedString(d.getSize()) +
               ")";
    }

    private static Item b(byte[] buf, int off, int numbers, int index) {
        if (index > numbers) {
            return null;
        }
        var base = off + (index - 1) * 8;
        return Item.parse(buf, base);
    }

    static DataDirectories parse(byte[] buf, int off, int numbers) {
        Item exportTable = b(buf, off, numbers, 1);
        Item importTable = b(buf, off, numbers, 2);
        Item resourceTable = b(buf, off, numbers, 3);
        Item exceptionTable = b(buf, off, numbers, 4);
        Item certificateTable = b(buf, off, numbers, 5);
        Item baseRelocationTable = b(buf, off, numbers, 6);
        Item debug = b(buf, off, numbers, 7);
        Item architecture = b(buf, off, numbers, 8);
        Item globalPtr = b(buf, off, numbers, 9);
        Item tlsTable = b(buf, off, numbers, 10);
        Item loadConfigTable = b(buf, off, numbers, 11);
        Item boundImport = b(buf, off, numbers, 12);
        Item iat = b(buf, off, numbers, 13);
        Item delayImportDescriptor = b(buf, off, numbers, 14);
        Item clrRuntimeHeader = b(buf, off, numbers, 15);
        Item reserved = b(buf, off, numbers, 16);
        if (reserved != null && (reserved.getSize() != 0 || reserved.getRva() != 0)) {
            throw new PEFileException("Invalid PE File, reserved data directory is not zero");
        }
        return new DataDirectories(exportTable,
                                   importTable,
                                   resourceTable,
                                   exceptionTable,
                                   certificateTable,
                                   baseRelocationTable,
                                   debug,
                                   architecture,
                                   globalPtr,
                                   tlsTable,
                                   loadConfigTable,
                                   boundImport,
                                   iat,
                                   delayImportDescriptor,
                                   clrRuntimeHeader,
                                   reserved,
                                   numbers * 8);
    }

    @Override
    public String toString() {
        return U.structure("DataDirectory",
                           U.field("exportTable", s(exportTable)),
                           U.field("importTable", s(importTable)),
                           U.field("resourceTable", s(resourceTable)),
                           U.field("exceptionTable", s(exceptionTable)),
                           U.field("certificateTable", s(certificateTable)),
                           U.field("baseRelocationTable", s(baseRelocationTable)),
                           U.field("debug", s(debug)),
                           U.field("architecture", s(architecture)),
                           U.field("globalPtr", s(globalPtr)),
                           U.field("tlsTable", s(tlsTable)),
                           U.field("loadConfigTable", s(loadConfigTable)),
                           U.field("boundImport", s(boundImport)),
                           U.field("iat", s(iat)),
                           U.field("delayImportDescriptor", s(delayImportDescriptor)),
                           U.field("clrRuntimeHeader", s(clrRuntimeHeader)),
                           U.field("reserved", s(reserved)));
    }

    /**
     * The export table address and size. For more information see .edata Section (Image Only).
     */
    public @Nullable Item getExportTable() {
        return exportTable;
    }

    /**
     * The import table address and size. For more information, see The .idata Section.
     */
    public @Nullable Item getImportTable() {
        return importTable;
    }

    /**
     * The resource table address and size. For more information, see The .rsrc Section.
     */
    public @Nullable Item getResourceTable() {
        return resourceTable;
    }

    /**
     * The exception table address and size. For more information, see The .pdata Section.
     */
    public @Nullable Item getExceptionTable() {
        return exceptionTable;
    }

    /**
     * The attribute certificate table address and size. For more information, see The Attribute Certificate Table (Image Only).
     */
    public @Nullable Item getCertificateTable() {
        return certificateTable;
    }

    /**
     * The base relocation table address and size. For more information, see The .reloc Section (Image Only).
     */
    public @Nullable Item getBaseRelocationTable() {
        return baseRelocationTable;
    }

    /**
     * The debug data starting address and size. For more information, see The .debug Section.
     */
    public @Nullable Item getDebug() {
        return debug;
    }

    /**
     * Reserved, must be 0
     */
    public @Nullable Item getArchitecture() {
        return architecture;
    }

    /**
     * The RVA of the value to be stored in the global pointer register. The size member of this structure must be set to zero.
     */
    public @Nullable Item getGlobalPtr() {
        return globalPtr;
    }

    /**
     * The thread local storage (TLS) table address and size. For more information, see The .tls Section.
     */
    public @Nullable Item getTlsTable() {
        return tlsTable;
    }

    /**
     * The load configuration table address and size. For more information, see The Load Configuration Structure (Image Only).
     */
    public @Nullable Item getLoadConfigTable() {
        return loadConfigTable;
    }

    /**
     * The bound import table address and size.
     */
    public @Nullable Item getBoundImport() {
        return boundImport;
    }

    /**
     * The import address table address and size. For more information, see Import Address Table.
     */
    public @Nullable Item getIat() {
        return iat;
    }

    /**
     * The delay import descriptor address and size. For more information, see Delay-Load Import Tables (Image Only).
     */
    public @Nullable Item getDelayImportDescriptor() {
        return delayImportDescriptor;
    }

    /**
     * The CLR runtime header address and size. For more information, see The .cormeta Section (Object Only).
     */
    public @Nullable Item getClrRuntimeHeader() {
        return clrRuntimeHeader;
    }

    public int length() {
        return length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DataDirectories) obj;
        return Objects.equals(this.exportTable, that.exportTable) &&
               Objects.equals(this.importTable, that.importTable) &&
               Objects.equals(this.resourceTable, that.resourceTable) &&
               Objects.equals(this.exceptionTable, that.exceptionTable) &&
               Objects.equals(this.certificateTable, that.certificateTable) &&
               Objects.equals(this.baseRelocationTable, that.baseRelocationTable) &&
               Objects.equals(this.debug, that.debug) &&
               Objects.equals(this.architecture, that.architecture) &&
               Objects.equals(this.globalPtr, that.globalPtr) &&
               Objects.equals(this.tlsTable, that.tlsTable) &&
               Objects.equals(this.loadConfigTable, that.loadConfigTable) &&
               Objects.equals(this.boundImport, that.boundImport) &&
               Objects.equals(this.iat, that.iat) &&
               Objects.equals(this.delayImportDescriptor, that.delayImportDescriptor) &&
               Objects.equals(this.clrRuntimeHeader, that.clrRuntimeHeader) &&
               Objects.equals(this.reserved, that.reserved) &&
               this.length == that.length;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(exportTable);
        result = 31 * result + Objects.hashCode(importTable);
        result = 31 * result + Objects.hashCode(resourceTable);
        result = 31 * result + Objects.hashCode(exceptionTable);
        result = 31 * result + Objects.hashCode(certificateTable);
        result = 31 * result + Objects.hashCode(baseRelocationTable);
        result = 31 * result + Objects.hashCode(debug);
        result = 31 * result + Objects.hashCode(architecture);
        result = 31 * result + Objects.hashCode(globalPtr);
        result = 31 * result + Objects.hashCode(tlsTable);
        result = 31 * result + Objects.hashCode(loadConfigTable);
        result = 31 * result + Objects.hashCode(boundImport);
        result = 31 * result + Objects.hashCode(iat);
        result = 31 * result + Objects.hashCode(delayImportDescriptor);
        result = 31 * result + Objects.hashCode(clrRuntimeHeader);
        result = 31 * result + Objects.hashCode(reserved);
        result = 31 * result + length;
        return result;
    }

    public static final class Item {
        @Field(address = true)
        private final int rva;
        @Field
        private final int size;

        Item(int rva, int size) {
            this.rva = rva;
            this.size = size;
        }

        static Item parse(byte[] buf, int off) {
            return new Item(I.u32(buf, off), I.u32(buf, off + 4));
        }

        @Override
        public String toString() {
            return U.structure("DataDirectories.Item", U.field("rva", rva), U.field("size", size));
        }

        public int getRva() {
            return rva;
        }

        public int getSize() {
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Item) obj;
            return this.rva == that.rva && this.size == that.size;
        }

        @Override
        public int hashCode() {
            int result = rva;
            result = 31 * result + size;
            return result;
        }

    }
}

