package space.iseki.pefile;

public final class StandardHeader {
    @Field
    private final boolean isPE32Plus;
    @Field
    private final byte majorLinkerVersion;
    @Field
    private final byte minorLinkerVersion;
    @Field
    private final int sizeOfCode;
    @Field
    private final int sizeOfInitializedData;
    @Field
    private final int sizeOfUninitializedData;
    @Field(address = true)
    private final int addressOfEntryPoint;
    @Field(address = true)
    private final int baseOfCode;
    @Field(address = true)
    private final int baseOfData;

    StandardHeader(boolean isPE32Plus,
                   byte majorLinkerVersion,
                   byte minorLinkerVersion,
                   int sizeOfCode,
                   int sizeOfInitializedData,
                   int sizeOfUninitializedData,
                   int addressOfEntryPoint,
                   int baseOfCode,
                   int baseOfData) {
        this.isPE32Plus = isPE32Plus;
        this.majorLinkerVersion = majorLinkerVersion;
        this.minorLinkerVersion = minorLinkerVersion;
        this.sizeOfCode = sizeOfCode;
        this.sizeOfInitializedData = sizeOfInitializedData;
        this.sizeOfUninitializedData = sizeOfUninitializedData;
        this.addressOfEntryPoint = addressOfEntryPoint;
        this.baseOfCode = baseOfCode;
        this.baseOfData = baseOfData;
    }

    static StandardHeader parse(byte[] buf, int off) {
        var magic = I.u16(buf, off);
        var isPlus = magic == 0x20b;
        if (magic != 0x10b && !isPlus) {
            throw new PEFileException("Invalid magic: " + U.hex(magic));
        }
        return new StandardHeader(isPlus,
                                  buf[off + 2],
                                  buf[off + 3],
                                  I.u32(buf, off + 4),
                                  I.u32(buf, off + 8),
                                  I.u32(buf, off + 12),
                                  I.u32(buf, off + 16),
                                  I.u32(buf, off + 20),
                                  isPlus ? 0 : I.u32(buf, off + 24));
    }

    public int length() {
        return isPE32Plus ? 24 : 28;
    }

    @Override
    public String toString() {
        return U.structure("StandardHeader",
                           U.field("isPE32Plus", isPE32Plus),
                           U.field("majorLinkerVersion", majorLinkerVersion),
                           U.field("minorLinkerVersion", minorLinkerVersion),
                           U.field("sizeOfCode", sizeOfCode),
                           U.field("sizeOfInitializedData", sizeOfInitializedData),
                           U.field("sizeOfUninitializedData", sizeOfUninitializedData),
                           U.field("addressOfEntryPoint", addressOfEntryPoint),
                           U.field("baseOfCode", baseOfCode),
                           U.field("baseOfData", baseOfData));
    }

    public boolean isPE32Plus() {
        return isPE32Plus;
    }

    /**
     * The linker major version number.
     */
    public byte getMajorLinkerVersion() {
        return majorLinkerVersion;
    }

    /**
     * The linker minor version number.
     */
    public byte getMinorLinkerVersion() {
        return minorLinkerVersion;
    }

    /**
     * The size of the code (text) section, or the sum of all code sections if there are multiple sections.
     */
    public int getSizeOfCode() {
        return sizeOfCode;
    }

    /**
     * The size of the initialized data section, or the sum of all such sections if there are multiple data sections.
     */
    public int getSizeOfInitializedData() {
        return sizeOfInitializedData;
    }

    /**
     * The size of the uninitialized data section (BSS), or the sum of all such sections if there are multiple BSS sections.
     */
    public int getSizeOfUninitializedData() {
        return sizeOfUninitializedData;
    }

    /**
     * The address of the entry point relative to the image base when the executable file is loaded into memory. For program images, this is the starting address. For device drivers, this is the address of the initialization function. An entry point is optional for DLLs. When no entry point is present, this field must be zero.
     */
    public int getAddressOfEntryPoint() {
        return addressOfEntryPoint;
    }

    /**
     * The address that is relative to the image base of the beginning-of-code section when it is loaded into memory.
     */
    public int getBaseOfCode() {
        return baseOfCode;
    }

    /**
     * The address that is relative to the image base of the beginning-of-data section when it is loaded into memory.
     */
    public int getBaseOfData() {
        return baseOfData;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StandardHeader) obj;
        return this.isPE32Plus == that.isPE32Plus &&
               this.majorLinkerVersion == that.majorLinkerVersion &&
               this.minorLinkerVersion == that.minorLinkerVersion &&
               this.sizeOfCode == that.sizeOfCode &&
               this.sizeOfInitializedData == that.sizeOfInitializedData &&
               this.sizeOfUninitializedData == that.sizeOfUninitializedData &&
               this.addressOfEntryPoint == that.addressOfEntryPoint &&
               this.baseOfCode == that.baseOfCode &&
               this.baseOfData == that.baseOfData;
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(isPE32Plus);
        result = 31 * result + majorLinkerVersion;
        result = 31 * result + minorLinkerVersion;
        result = 31 * result + sizeOfCode;
        result = 31 * result + sizeOfInitializedData;
        result = 31 * result + sizeOfUninitializedData;
        result = 31 * result + addressOfEntryPoint;
        result = 31 * result + baseOfCode;
        result = 31 * result + baseOfData;
        return result;
    }
}
