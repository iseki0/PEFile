package space.iseki.pefile;

record ImageResourceDirectory(int characteristics,
                              int timeDateStamp,
                              short majorVersion,
                              short minorVersion,
                              short numberOfNamedEntries,
                              short numberOfIdEntries) {
    public static final int LENGTH = 16;

    static ImageResourceDirectory parse(byte[] data, int offset) {
        return new ImageResourceDirectory(I.u32(data, offset),
                                          I.u32(data, offset + 4),
                                          I.u16(data, offset + 8),
                                          I.u16(data, offset + 10),
                                          I.u16(data, offset + 12),
                                          I.u16(data, offset + 14));
    }

    @Override
    public String toString() {
        return U.structure("ImageResourceDirectory",
                           U.fieldHex("characteristics", characteristics),
                           U.field("timeDateStamp", U.timeDateU32(timeDateStamp)),
                           U.field("majorVersion", majorVersion),
                           U.field("minorVersion", minorVersion),
                           U.field("numberOfNamedEntries", numberOfNamedEntries),
                           U.field("numberOfIdEntries", numberOfIdEntries));
    }
}

record ImageResourceDataEntry(int dataRva, int size, int codePage) {
    public static final int LENGTH = 16;

    static ImageResourceDataEntry parse(byte[] data, int offset) {
        if (I.u32(data, offset + 12) != 0) {
            throw new PEFileException("The 4th field of ImageResourceDataEntry should be zero, but it is not. Chunk: " +
                                      U.hex(data, offset, LENGTH));
        }
        return new ImageResourceDataEntry(I.u32(data, offset), I.u32(data, offset + 4), I.u32(data, offset + 8));
    }

    @Override
    public String toString() {
        return U.structure("ImageResourceDataEntry",
                           U.field("dataRva", dataRva),
                           U.field("size", size),
                           U.field("codePage", codePage));
    }
}