package space.iseki.pefile;

public final class OptionalHeader {
    @Field
    private final boolean isPE32Plus;
    @Field(address = true)
    private final long imageBase;
    @Field
    private final int sectionAlignment;
    @Field
    private final int fileAlignment;
    @Field
    private final short majorOperatingSystemVersion;
    @Field
    private final short minorOperatingSystemVersion;
    @Field
    private final short majorImageVersion;
    @Field
    private final short minorImageVersion;
    @Field
    private final short majorSubsystemVersion;
    @Field
    private final short minorSubsystemVersion;
    @Field
    private final int win32VersionValue;
    @Field
    private final int sizeOfImage;
    @Field
    private final int sizeOfHeaders;
    @Field
    private final int checkSum;
    @Field(reference = WindowsSubsystems.class)
    private final short subsystem;
    @Field(reference = DllCharacteristics.class)
    private final short dllCharacteristics;
    @Field
    private final long sizeOfStackReserve;
    @Field
    private final long sizeOfStackCommit;
    @Field
    private final long sizeOfHeapReserve;
    @Field
    private final long sizeOfHeapCommit;
    @Field(reference = SectionFlags.class)
    private final int loaderFlags;
    @Field
    private final int numberOfRvaAndSizes;

    OptionalHeader(boolean isPE32Plus,
                   long imageBase,
                   int sectionAlignment,
                   int fileAlignment,
                   short majorOperatingSystemVersion,
                   short minorOperatingSystemVersion,
                   short majorImageVersion,
                   short minorImageVersion,
                   short majorSubsystemVersion,
                   short minorSubsystemVersion,
                   int win32VersionValue,
                   int sizeOfImage,
                   int sizeOfHeaders,
                   int checkSum,
                   short subsystem,
                   short dllCharacteristics,
                   long sizeOfStackReserve,
                   long sizeOfStackCommit,
                   long sizeOfHeapReserve,
                   long sizeOfHeapCommit,
                   int loaderFlags,
                   int numberOfRvaAndSizes) {
        this.isPE32Plus = isPE32Plus;
        this.imageBase = imageBase;
        this.sectionAlignment = sectionAlignment;
        this.fileAlignment = fileAlignment;
        this.majorOperatingSystemVersion = majorOperatingSystemVersion;
        this.minorOperatingSystemVersion = minorOperatingSystemVersion;
        this.majorImageVersion = majorImageVersion;
        this.minorImageVersion = minorImageVersion;
        this.majorSubsystemVersion = majorSubsystemVersion;
        this.minorSubsystemVersion = minorSubsystemVersion;
        this.win32VersionValue = win32VersionValue;
        this.sizeOfImage = sizeOfImage;
        this.sizeOfHeaders = sizeOfHeaders;
        this.checkSum = checkSum;
        this.subsystem = subsystem;
        this.dllCharacteristics = dllCharacteristics;
        this.sizeOfStackReserve = sizeOfStackReserve;
        this.sizeOfStackCommit = sizeOfStackCommit;
        this.sizeOfHeapReserve = sizeOfHeapReserve;
        this.sizeOfHeapCommit = sizeOfHeapCommit;
        this.loaderFlags = loaderFlags;
        this.numberOfRvaAndSizes = numberOfRvaAndSizes;
    }

    static OptionalHeader parse(byte[] buf, int off, boolean isPE32Plus) {
        off -= isPE32Plus ? 24 : 28;
        var imageBase = isPE32Plus ? I.u64(buf, off + 24) : I.u32L(buf, off + 28);
        var sectionAlignment = I.u32(buf, off + 32);
        var fileAlignment = I.u32(buf, off + 36);
        var majorOperatingSystemVersion = I.u16(buf, off + 40);
        var minorOperatingSystemVersion = I.u16(buf, off + 42);
        var majorImageVersion = I.u16(buf, off + 44);
        var minorImageVersion = I.u16(buf, off + 46);
        var majorSubsystemVersion = I.u16(buf, off + 48);
        var minorSubsystemVersion = I.u16(buf, off + 50);
        var win32VersionValue = I.u32(buf, off + 52);
        var sizeOfImage = I.u32(buf, off + 56);
        var sizeOfHeaders = I.u32(buf, off + 60);
        var checkSum = I.u32(buf, off + 64);
        var subsystem = I.u16(buf, off + 68);
        var dllCharacteristics = I.u16(buf, off + 70);
        var sizeOfStackReserve = isPE32Plus ? I.u64(buf, off + 72) : I.u32L(buf, off + 72);
        var sizeOfStackCommit = isPE32Plus ? I.u64(buf, off + 80) : I.u32L(buf, off + 76);
        var sizeOfHeapReserve = isPE32Plus ? I.u64(buf, off + 88) : I.u32L(buf, off + 80);
        var sizeOfHeapCommit = isPE32Plus ? I.u64(buf, off + 96) : I.u32L(buf, off + 84);
        var loaderFlags = I.u32(buf, off + (isPE32Plus ? 104 : 88));
        var numberOfRvaAndSizes = I.u32(buf, off + (isPE32Plus ? 108 : 92));
        return new OptionalHeader(isPE32Plus,
                                  imageBase,
                                  sectionAlignment,
                                  fileAlignment,
                                  majorOperatingSystemVersion,
                                  minorOperatingSystemVersion,
                                  majorImageVersion,
                                  minorImageVersion,
                                  majorSubsystemVersion,
                                  minorSubsystemVersion,
                                  win32VersionValue,
                                  sizeOfImage,
                                  sizeOfHeaders,
                                  checkSum,
                                  subsystem,
                                  dllCharacteristics,
                                  sizeOfStackReserve,
                                  sizeOfStackCommit,
                                  sizeOfHeapReserve,
                                  sizeOfHeapCommit,
                                  loaderFlags,
                                  numberOfRvaAndSizes);
    }

    @Override
    public String toString() {
        return U.structure("OptionalHeader",
                           U.field("imageBase", imageBase),
                           U.field("sectionAlignment", sectionAlignment),
                           U.field("fileAlignment", fileAlignment),
                           U.field("majorOperatingSystemVersion", majorOperatingSystemVersion),
                           U.field("minorOperatingSystemVersion", minorOperatingSystemVersion),
                           U.field("majorImageVersion", majorImageVersion),
                           U.field("minorImageVersion", minorImageVersion),
                           U.field("majorSubsystemVersion", majorSubsystemVersion),
                           U.field("minorSubsystemVersion", minorSubsystemVersion),
                           U.field("win32VersionValue", win32VersionValue),
                           U.field("sizeOfImage", sizeOfImage),
                           U.field("sizeOfHeaders", sizeOfHeaders),
                           U.fieldHex("checkSum", checkSum),
                           U.field("subsystem", WindowsSubsystems.toString(subsystem)),
                           U.field("dllCharacteristics", DllCharacteristics.toString(dllCharacteristics)),
                           U.field("sizeOfStackReserve", sizeOfStackReserve),
                           U.field("sizeOfStackCommit", sizeOfStackCommit),
                           U.field("sizeOfHeapReserve", sizeOfHeapReserve),
                           U.field("sizeOfHeapCommit", sizeOfHeapCommit),
                           U.fieldHex("loaderFlags", loaderFlags),
                           U.field("numberOfRvaAndSizes", numberOfRvaAndSizes));
    }

    public int length() {
        return isPE32Plus ? 88 : 68;
    }

    public boolean isPE32Plus() {
        return isPE32Plus;
    }

    /**
     * The preferred address of the first byte of image when loaded into memory; must be a multiple of 64 K. The default for DLLs is 0x10000000. The default for Windows CE EXEs is 0x00010000. The default for Windows NT, Windows 2000, Windows XP, Windows 95, Windows 98, and Windows Me is 0x00400000.
     */
    public long getImageBase() {
        return imageBase;
    }

    /**
     * The alignment (in bytes) of sections when they are loaded into memory. It must be greater than or equal to FileAlignment. The default is the page size for the architecture.
     */
    public int getSectionAlignment() {
        return sectionAlignment;
    }

    /**
     * The alignment factor (in bytes) that is used to align the raw data of sections in the image file. The value should be a power of 2 between 512 and 64 K, inclusive. The default is 512. If the SectionAlignment is less than the architecture's page size, then FileAlignment must match SectionAlignment.
     */
    public int getFileAlignment() {
        return fileAlignment;
    }

    /**
     * The major version number of the required operating system.
     */
    public short getMajorOperatingSystemVersion() {
        return majorOperatingSystemVersion;
    }

    /**
     * The minor version number of the required operating system.
     */
    public short getMinorOperatingSystemVersion() {
        return minorOperatingSystemVersion;
    }

    /**
     * The major version number of the image.
     */
    public short getMajorImageVersion() {
        return majorImageVersion;
    }

    /**
     * The minor version number of the image.
     */
    public short getMinorImageVersion() {
        return minorImageVersion;
    }

    /**
     * The major version number of the subsystem.
     */
    public short getMajorSubsystemVersion() {
        return majorSubsystemVersion;
    }

    /**
     * The minor version number of the subsystem.
     */
    public short getMinorSubsystemVersion() {
        return minorSubsystemVersion;
    }

    /**
     * Reserved, must be zero.
     */
    public int getWin32VersionValue() {
        return win32VersionValue;
    }

    /**
     * The size (in bytes) of the image, including all headers, as the image is loaded in memory. It must be a multiple of SectionAlignment.
     */
    public int getSizeOfImage() {
        return sizeOfImage;
    }

    /**
     * The combined size of an MS-DOS stub, PE header, and section headers rounded up to a multiple of FileAlignment.
     */
    public int getSizeOfHeaders() {
        return sizeOfHeaders;
    }

    /**
     * The image file checksum. The algorithm for computing the checksum is incorporated into IMAGHELP.DLL. The following are checked for validation at load time: all drivers, any DLL loaded at boot time, and any DLL that is loaded into a critical Windows process.
     */
    public int getCheckSum() {
        return checkSum;
    }

    /**
     * The subsystem that is required to run this image. For more information, see Windows Subsystem.
     */
    public short getSubsystem() {
        return subsystem;
    }

    /**
     * For more information, see DLL Characteristics later in this specification.
     */
    public short getDllCharacteristics() {
        return dllCharacteristics;
    }

    /**
     * The size of the stack to reserve. Only SizeOfStackCommit is committed; the rest is made available one page at a time until the reserve size is reached.
     */
    public long getSizeOfStackReserve() {
        return sizeOfStackReserve;
    }

    /**
     * The size of the stack to commit.
     */
    public long getSizeOfStackCommit() {
        return sizeOfStackCommit;
    }

    /**
     * The size of the local heap space to reserve. Only SizeOfHeapCommit is committed; the rest is made available one page at a time until the reserve size is reached.
     */
    public long getSizeOfHeapReserve() {
        return sizeOfHeapReserve;
    }

    /**
     * The size of the local heap space to commit.
     */
    public long getSizeOfHeapCommit() {
        return sizeOfHeapCommit;
    }

    /**
     * Reserved, must be zero.
     */
    public int getLoaderFlags() {
        return loaderFlags;
    }

    /**
     * The number of data-directory entries in the remainder of the optional header. Each describes a location and size.
     */
    public int getNumberOfRvaAndSizes() {
        return numberOfRvaAndSizes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionalHeader that = (OptionalHeader) o;
        return isPE32Plus == that.isPE32Plus &&
               imageBase == that.imageBase &&
               sectionAlignment == that.sectionAlignment &&
               fileAlignment == that.fileAlignment &&
               majorOperatingSystemVersion == that.majorOperatingSystemVersion &&
               minorOperatingSystemVersion == that.minorOperatingSystemVersion &&
               majorImageVersion == that.majorImageVersion &&
               minorImageVersion == that.minorImageVersion &&
               majorSubsystemVersion == that.majorSubsystemVersion &&
               minorSubsystemVersion == that.minorSubsystemVersion &&
               win32VersionValue == that.win32VersionValue &&
               sizeOfImage == that.sizeOfImage &&
               sizeOfHeaders == that.sizeOfHeaders &&
               checkSum == that.checkSum &&
               subsystem == that.subsystem &&
               dllCharacteristics == that.dllCharacteristics &&
               sizeOfStackReserve == that.sizeOfStackReserve &&
               sizeOfStackCommit == that.sizeOfStackCommit &&
               sizeOfHeapReserve == that.sizeOfHeapReserve &&
               sizeOfHeapCommit == that.sizeOfHeapCommit &&
               loaderFlags == that.loaderFlags &&
               numberOfRvaAndSizes == that.numberOfRvaAndSizes;
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(isPE32Plus);
        result = 31 * result + Long.hashCode(imageBase);
        result = 31 * result + sectionAlignment;
        result = 31 * result + fileAlignment;
        result = 31 * result + majorOperatingSystemVersion;
        result = 31 * result + minorOperatingSystemVersion;
        result = 31 * result + majorImageVersion;
        result = 31 * result + minorImageVersion;
        result = 31 * result + majorSubsystemVersion;
        result = 31 * result + minorSubsystemVersion;
        result = 31 * result + win32VersionValue;
        result = 31 * result + sizeOfImage;
        result = 31 * result + sizeOfHeaders;
        result = 31 * result + checkSum;
        result = 31 * result + subsystem;
        result = 31 * result + dllCharacteristics;
        result = 31 * result + Long.hashCode(sizeOfStackReserve);
        result = 31 * result + Long.hashCode(sizeOfStackCommit);
        result = 31 * result + Long.hashCode(sizeOfHeapReserve);
        result = 31 * result + Long.hashCode(sizeOfHeapCommit);
        result = 31 * result + loaderFlags;
        result = 31 * result + numberOfRvaAndSizes;
        return result;
    }
}
