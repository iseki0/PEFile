package space.iseki.pefile;

@SuppressWarnings("unused")
public final class CoffHeader {
    @Field(reference = MachineTypes.class)
    private final short machine;
    @Field
    private final short numbersOfSections;
    @Field
    private final int timeDateStamp;
    @Field(address = true)
    private final int pointerToSymbolTable;
    @Field
    private final int numberOfSymbols;
    @Field
    private final short sizeOfOptionalHeader;
    @Field(reference = Characteristics.class)
    private final short characteristics;

    CoffHeader(short machine,
               short numbersOfSections,
               int timeDateStamp,
               int pointerToSymbolTable,
               int numberOfSymbols,
               short sizeOfOptionalHeader,
               short characteristics) {
        this.machine = machine;
        this.numbersOfSections = numbersOfSections;
        this.timeDateStamp = timeDateStamp;
        this.pointerToSymbolTable = pointerToSymbolTable;
        this.numberOfSymbols = numberOfSymbols;
        this.sizeOfOptionalHeader = sizeOfOptionalHeader;
        this.characteristics = characteristics;
    }

    @SuppressWarnings("SameParameterValue")
    static CoffHeader parse(byte[] buf, int off) {
        return new CoffHeader(I.u16(buf, off),
                              I.u16(buf, off + 2),
                              I.u32(buf, off + 4),
                              I.u32(buf, off + 8),
                              I.u32(buf, off + 12),
                              I.u16(buf, off + 16),
                              I.u16(buf, off + 18));
    }

    @Override
    public String toString() {
        return U.structure("CoffHeader",
                           U.field("machine", MachineTypes.nameOf(machine)),
                           U.field("numbersOfSections", numbersOfSections),
                           U.field("timeDateStamp", U.timeDateU32(timeDateStamp)),
                           U.field("pointerToSymbolTable", pointerToSymbolTable),
                           U.field("numberOfSymbols", numberOfSymbols),
                           U.field("sizeOfOptionalHeader", sizeOfOptionalHeader),
                           U.field("characteristics", Characteristics.toString(characteristics)));
    }

    /**
     * The number that identifies the type of target machine.
     * <p>
     * For more information, see Machine Types.
     */
    public short getMachine() {
        return machine;
    }

    /**
     * The number of sections.
     * <p>
     * This indicates the size of the section table, which immediately follows the headers.
     */
    public short getNumbersOfSections() {
        return numbersOfSections;
    }

    /**
     * The low 32 bits of the number of seconds since 00:00 January 1, 1970 (a C run-time time_t value), which indicates when the file was created.
     */
    public int getTimeDateStamp() {
        return timeDateStamp;
    }

    /**
     * The file offset of the COFF symbol table, or zero if no COFF symbol table is present.
     * <p>
     * This value should be zero for an image because COFF debugging information is deprecated.
     */
    public int getPointerToSymbolTable() {
        return pointerToSymbolTable;
    }

    /**
     * The number of entries in the symbol table.
     * <p>
     * This data can be used to locate the string table, which immediately follows the symbol table. This value should be zero for an image because COFF debugging information is deprecated.
     */
    public int getNumberOfSymbols() {
        return numberOfSymbols;
    }

    /**
     * The size of the optional header, which is required for executable files but not for object files.
     * <p>
     * This value should be zero for an object file. For a description of the header format, see Optional Header (Image Only).
     */
    public short getSizeOfOptionalHeader() {
        return sizeOfOptionalHeader;
    }

    /**
     * The flags that indicate the attributes of the file.
     * <p>
     * For specific flag values, see Characteristics.
     */
    public short getCharacteristics() {
        return characteristics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoffHeader that = (CoffHeader) o;
        return machine == that.machine &&
               numbersOfSections == that.numbersOfSections &&
               timeDateStamp == that.timeDateStamp &&
               pointerToSymbolTable == that.pointerToSymbolTable &&
               numberOfSymbols == that.numberOfSymbols &&
               sizeOfOptionalHeader == that.sizeOfOptionalHeader &&
               characteristics == that.characteristics;
    }

    @Override
    public int hashCode() {
        int result = machine;
        result = 31 * result + numbersOfSections;
        result = 31 * result + timeDateStamp;
        result = 31 * result + pointerToSymbolTable;
        result = 31 * result + numberOfSymbols;
        result = 31 * result + sizeOfOptionalHeader;
        result = 31 * result + characteristics;
        return result;
    }
}
