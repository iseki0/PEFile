package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Section {
    private static final Pattern REPLACE = Pattern.compile("^SectionTableItem");
    private static final byte[] EMPTY = new byte[0];
    private final int pointerToRawData;
    private final int sizeOfRawData;
    private final int virtualAddress;
    private final int virtualSize;
    private final int characteristics;
    private final WrappedDataAccessor accessor;
    private final SectionTableItem tableItem;

    Section(SectionTableItem tableItem, WrappedDataAccessor accessor) {
        this.pointerToRawData = tableItem.pointerToRawData();
        this.sizeOfRawData = tableItem.sizeOfRawData();
        this.virtualAddress = tableItem.virtualAddress();
        this.virtualSize = tableItem.virtualSize();
        this.characteristics = tableItem.characteristics();
        this.tableItem = tableItem;
        this.accessor = accessor;
    }

    @Override
    public String toString() {
        return REPLACE.matcher(tableItem.toString()).replaceFirst("Section");
    }

    public int getPointerToRawData() {
        return pointerToRawData;
    }

    public int getSizeOfRawData() {
        return sizeOfRawData;
    }

    public int getVirtualAddress() {
        return virtualAddress;
    }

    public int getVirtualSize() {
        return virtualSize;
    }

    /**
     * @see SectionFlags#toString(int)
     */
    public int getCharacteristics() {
        return characteristics;
    }

    public @NotNull String getName() {
        return tableItem.name();
    }

    /**
     * Copies bytes from the specified section into the target buffer {@code buf}.
     *
     * <p>
     * This method reads bytes starting from the given Relative Virtual Address (RVA) {@code rva},
     * with the specified offset {@code off} and length {@code len}.
     * If the specified range exceeds
     * the actual size of the section, only the available data within the section is copied, leaving
     * any extra space in the buffer unaltered.
     *
     * @param buf the target buffer where the bytes will be copied
     * @param rva the starting Relative Virtual Address (RVA) within the section to copy from
     * @param off the offset within the buffer {@code buf} where writing begins
     * @param len the number of bytes to copy
     * @throws IOException           if an I/O error occurs during reading
     * @throws IllegalStateException if the underlying file is already closed
     */
    public void copyBytes(byte @NotNull [] buf, int rva, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, buf.length);
        var beginAt = U.max(virtualAddress, rva);
        var endBefore = U.min(virtualAddress + sizeOfRawData, U.add(rva, len));
        if (U.ge(beginAt, endBefore)) return;
        var readBegin = U.add(pointerToRawData, U.sub(beginAt, virtualAddress));
        var readLen = U.sub(endBefore, beginAt);
        accessor.readFully(readBegin, buf, U.sub(U.add(off, beginAt), rva), readLen);
    }

    /**
     * Returns an {@link InputStream} that reads bytes from the specified section.
     *
     * @param sinceRva the starting Relative Virtual Address (RVA) within the section to read from (inclusive)
     * @return an {@link InputStream} that reads bytes from the specified section
     * @throws IndexOutOfBoundsException if {@code sinceRva} is less than the {@link #virtualAddress}
     */
    public @NotNull InputStream inputStream(int sinceRva) {
        if (U.lt(sinceRva, virtualAddress)) {
            throw new IndexOutOfBoundsException("sinceRva[%s] < virtualAddress[%s]".formatted(Integer.toUnsignedString(
                    sinceRva), Integer.toUnsignedString(virtualAddress)));
        }
        var input = new InputStream() {
            int pos = sinceRva;

            @Override
            public int read() {
                throw new UnsupportedOperationException("use InputStream#read(byte[], int, int) instead");
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                Objects.checkFromIndexSize(off, len, b.length);
                if (U.ge(pos, U.add(virtualAddress, virtualSize))) return -1;
                var totalRead = U.min(U.sub(U.add(virtualSize, virtualAddress), pos), len);
                if (totalRead < 1) return -1;
                if (!accessor.isOpen()) {
                    throw new IOException("The underlying file might already be closed");
                }
                copyBytes(b, pos, off, totalRead);
                pos += totalRead;
                return pos;
            }
        };
        return new BufferedInputStream(input);
    }

    byte @NotNull [] readNullEndShortString(int beginAtRva) throws IOException {
        var beginAt = U.add(U.sub(U.max(virtualAddress, beginAtRva), virtualAddress), pointerToRawData);
        var endBefore = U.add(pointerToRawData, sizeOfRawData);
        var buf = new byte[256];
        ByteArrayOutputStream out = null;
        while (U.lt(beginAt, endBefore)) {
            if (out != null && out.size() > 16384) {
                throw new PEFileException("Too long string");
            }
            var n = accessor.readAtMost(beginAt, buf);
            if (n < 1) {
                return EMPTY;
            }
            for (int i = 0; i < n; i++) {
                if (buf[i] == 0) {
                    if (out != null) {
                        out.write(buf, 0, i);
                        return out.toByteArray();
                    }
                    return Arrays.copyOf(buf, i);
                }
            }
            if (out == null) {
                out = new ByteArrayOutputStream();
            }
            out.write(buf, 0, n);
            beginAt = U.add(beginAt, n);
        }
        return EMPTY;
    }
}


record SectionTableItem(@Field String name,
                        @Field int virtualSize,
                        @Field(address = true) int virtualAddress,
                        @Field int sizeOfRawData,
                        @Field(address = true) int pointerToRawData,
                        @Field(address = true) int pointerToRelocations,
                        @Field(address = true) int pointerToLinenumbers,
                        @Field short numberOfRelocations,
                        @Field short numberOfLinenumbers,
                        @Field(reference = SectionFlags.class) int characteristics) {

    public static final int LENGTH = 40;

    static SectionTableItem parse(byte[] buf, int off) {
        return new SectionTableItem(U.getNullString(buf, off, 8),
                                    I.u32(buf, off + 8),
                                    I.u32(buf, off + 12),
                                    I.u32(buf, off + 16),
                                    I.u32(buf, off + 20),
                                    I.u32(buf, off + 24),
                                    I.u32(buf, off + 28),
                                    I.u16(buf, off + 32),
                                    I.u16(buf, off + 34),
                                    I.u32(buf, off + 36));
    }

    @Override
    public String toString() {
        return U.structure("SectionTableItem",
                           U.fieldEscape("name", name),
                           U.field("virtualSize", virtualSize),
                           U.field("virtualAddress", virtualAddress),
                           U.field("sizeOfRawData", sizeOfRawData),
                           U.field("pointerToRawData", pointerToRawData),
                           U.field("pointerToRelocations", pointerToRelocations),
                           U.field("pointerToLinenumbers", pointerToLinenumbers),
                           U.field("numberOfRelocations", numberOfRelocations),
                           U.field("numberOfLinenumbers", numberOfLinenumbers),
                           U.field("characteristics", SectionFlags.toString(characteristics)));
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + virtualSize;
        result = 31 * result + virtualAddress;
        result = 31 * result + sizeOfRawData;
        result = 31 * result + pointerToRawData;
        result = 31 * result + pointerToRelocations;
        result = 31 * result + pointerToLinenumbers;
        result = 31 * result + numberOfRelocations;
        result = 31 * result + numberOfLinenumbers;
        result = 31 * result + characteristics;
        return result;
    }
}
