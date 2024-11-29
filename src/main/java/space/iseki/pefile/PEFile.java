package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an opened PE file.
 *
 * <p>
 * All methods on the instance are thread-safe, but there's no guarantee of the returned objects.
 * <p>
 * Once the {@code PEFile} instance has been closed,
 * only {@link PEFile#close()} and methods inherited from {@link Object} should be invoked.
 * Invoking any other method after the file is closed may result unreliable,
 * and an {@link IllegalStateException} is not guaranteed to be thrown in such cases.
 */
public final class PEFile implements AutoCloseable {
    static final long PE_SIGNATURE_LE = 0x00004550;
    final SectionSet sectionSet;
    final CoffHeader coffHeader;
    final StandardHeader standardHeader;
    final OptionalHeader optionalHeader;
    final DataDirectories dataDirectories;
    private final WrappedDataAccessor accessor;
    private final ImportTable importTable = new ImportTable(this);
    private final ExportTable exportTable = new ExportTable(this);
    private final Section rsrcSection;
    private final ResourceNode resourceRoot;

    PEFile(WrappedDataAccessor accessor,
           Section[] sections,
           CoffHeader coffHeader,
           StandardHeader standardHeader,
           OptionalHeader optionalHeader,
           DataDirectories dataDirectories) throws IOException {
        this.accessor = accessor;
        this.sectionSet = new SectionSet(sections);
        this.coffHeader = coffHeader;
        this.standardHeader = standardHeader;
        this.optionalHeader = optionalHeader;
        this.dataDirectories = dataDirectories;
        this.rsrcSection = Optional.ofNullable(dataDirectories.getResourceTable())
                                   .map(i -> sectionSet.find(i.getRva()))
                                   .orElse(null);
        var resourceTable = dataDirectories.getResourceTable();
        if (resourceTable != null && resourceTable.getRva() != 0) {
            if (rsrcSection == null) {
                throw new PEFileException("Resource section not found, rva: " +
                                          Integer.toUnsignedLong(resourceTable.getRva()));
            }
            try {
                var data = new byte[ImageResourceDirectory.LENGTH];
                rsrcSection.copyBytes(data, resourceTable.getRva(), 0, data.length);
                var ird = ImageResourceDirectory.parse(data, 0);
                resourceRoot = new ResourceNode(this, "<ROOT>", 0, 0, ird);
            } catch (EOFException | IndexOutOfBoundsException e) {
                throw new PEFileException("Invalid resource section");
            }
        } else {
            resourceRoot = null;
        }
    }

    /**
     * Open a PE file from a {@link File}.
     *
     * @param file the file
     * @return the PEFile
     * @throws UncheckedIOException if an I/O error occurs
     * @throws PEFileException      if the file is not a valid PE file
     */
    public static @NotNull PEFile open(@NotNull File file) {
        return wrapUncheckIOException(() -> open(file.toPath()));
    }

    /**
     * Open a PE file from a {@link Path}.
     *
     * @param path the path of the PE file
     * @return the PEFile
     * @throws UncheckedIOException if an I/O error occurs
     * @throws PEFileException      if the file is not a valid PE file
     */
    public static @NotNull PEFile open(@NotNull Path path) {
        return wrapUncheckIOException(() -> safeOpen(WrappedDataAccessor.of(path)));
    }

    /**
     * Open a PE file from a {@link SeekableByteChannel}.
     * <p>
     * It's the caller's responsibility to close the channel when any exceptions thrown.
     *
     * @param channel the channel will be closed when the PEFile is closed.
     * @return the PEFile
     * @throws UncheckedIOException if an I/O error occurs
     * @throws PEFileException      if the file is not a valid PE file
     */
    public static @NotNull PEFile open(@NotNull SeekableByteChannel channel) {
        return wrapUncheckIOException(() -> open(WrappedDataAccessor.of(channel)));
    }

    private static long seekToCoffHeader(WrappedDataAccessor accessor) throws IOException {
        try {
            var buf = new byte[4];
            accessor.readFully(0x3c, buf);
            var peBeginAt = I.u32L(buf, 0);
            accessor.readFully(peBeginAt, buf);
            return peBeginAt;
        } catch (EOFException ignored) {
            throw new PEFileException("Invalid PE file, unexpected EOF during seek to COFF header");
        }
    }

    private static void doCheckSignature(int peSignature) {
        if (peSignature != PE_SIGNATURE_LE) {
            throw new PEFileException("Invalid PE signature: " + U.hex(peSignature));
        }
    }

    private static PEFile safeOpen(WrappedDataAccessor accessor) throws IOException {
        try {
            return open(accessor);
        } catch (Throwable th) {
            try {
                accessor.close();
            } catch (Exception e) {
                th.addSuppressed(e);
            }
            throw th;
        }
    }

    static PEFile open(WrappedDataAccessor accessor) throws IOException {
        var peBeginAt = seekToCoffHeader(accessor);
        var data = new byte[4096];
        var read = accessor.readAtMost(peBeginAt, data);
        if (read < 24) {
            throw new PEFileException("Invalid PE file, unexpected EOF during read PE signature and COFF header");
        }
        doCheckSignature(I.u32(data, 0));
        var coffHeader = CoffHeader.parse(data, 4);
        if (coffHeader.getSizeOfOptionalHeader() + 24 > read) {
            // refill buffer
            data = new byte[coffHeader.getSizeOfOptionalHeader() + 24];
            try {
                accessor.readFully(peBeginAt, data);
            } catch (EOFException ignored) {
                throw new PEFileException("Invalid PE file, unexpected EOF during read optional headers");
            }
        }
        var ptr = 24;
        var standardHeader = StandardHeader.parse(data, ptr);
        ptr += standardHeader.length();
        var optionalHeader = OptionalHeader.parse(data, ptr, standardHeader.isPE32Plus());
        ptr += optionalHeader.length();
        var dataDirectory = DataDirectories.parse(data, ptr, optionalHeader.getNumberOfRvaAndSizes());
        ptr += dataDirectory.length();
        if (coffHeader.getNumbersOfSections() > 96) {
            throw new PEFileException("Invalid PE file, too many sections: " +
                                      Integer.toUnsignedString(coffHeader.getNumbersOfSections()));
        }
        var sectionTableChunkSize = SectionTableItem.LENGTH * coffHeader.getNumbersOfSections();
        if (sectionTableChunkSize + ptr > data.length) {
            data = new byte[sectionTableChunkSize];
            try {
                accessor.readFully(U.add(peBeginAt, ptr), data);
            } catch (EOFException ignored) {
                throw new PEFileException("Invalid PE file, unexpected EOF during read section table");
            }
            ptr = 0;
        }
        var sectionTable = new SectionTableItem[coffHeader.getNumbersOfSections()];
        for (int i = 0; i < sectionTable.length; i++) {
            sectionTable[i] = SectionTableItem.parse(data, ptr);
            ptr += SectionTableItem.LENGTH;
        }

        var sections = new Section[coffHeader.getNumbersOfSections()];
        for (int i = 0; i < sections.length; i++) {
            var s = sectionTable[i];
            sections[i] = new Section(s, accessor);
        }
        return new PEFile(accessor, sections, coffHeader, standardHeader, optionalHeader, dataDirectory);
    }

    private static <R> R wrapUncheckIOException(Wrap<R> callable) {
        try {
            return callable.call();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    /**
     * Returns an object that provides an iterator over the import table.
     * <p/>
     * The returned iterator is not thread-safe.
     *
     * @return the object will be unusable after the PEFile is closed
     */
    public @NotNull ImportTable getImportTable() {
        return importTable;
    }

    /**
     * Returns an object that provides an iterator over the export table.
     * <p/>
     * The returned iterator is not thread-safe.
     *
     * @return the object will be unusable after the PEFile is closed
     */
    public @NotNull ExportTable getExportTable() {
        return exportTable;
    }

    public @NotNull CoffHeader getCoffHeader() {
        return coffHeader;
    }

    public @NotNull DataDirectories getDataDirectories() {
        return dataDirectories;
    }

    public @NotNull StandardHeader getStandardHeader() {
        return standardHeader;
    }

    public @NotNull OptionalHeader getOptionalHeader() {
        return optionalHeader;
    }

    /**
     * Returns an unmodifiable list of sections.
     * <p>
     * After the PEFile is closed, the returned list is still usable, but the sections are unreadable.
     *
     * @return the list of sections, immutable
     */
    public @Unmodifiable @NotNull List<@NotNull Section> getSections() {
        return List.of(sectionSet.sections);
    }

    /**
     * Close the underlying file.
     * <p>
     * This method will close the underlying file channel.
     * If the file channel is already closed, this method will do nothing.
     * After this method is called, the PEFile instance is no longer usable, some methods might throw
     * {@link IllegalStateException} when invoked.
     *
     * @throws UncheckedIOException if an I/O error occurs
     */
    @Override
    public void close() throws Exception {
        accessor.close();
    }

    public @Nullable ResourceNode getResourceRoot() {
        return resourceRoot;
    }

    /**
     * Returns a lazy-list of children for a given resource directory.
     * <p>
     * The returned list might throw the following exceptions:
     * <ul>
     *     <li>{@link PEFileException} if the PE file is invalid</li>
     *     <li>{@link UncheckedIOException} if an I/O error occurs</li>
     *     <li>{@link IllegalStateException} if the underlying file is already closed</li>
     *     </ul>
     *
     * @param dirNode the directory node
     * @return the list of children, unmodifiable
     * @throws IllegalArgumentException if the dirNode does not belong to this PEFile
     * @throws PEFileException          if the PE file is invalid
     * @throws NullPointerException     if the dirNode is null
     */
    public @NotNull List<@NotNull ResourceNode> listChildren(@NotNull ResourceNode dirNode) {
        if (dirNode.peFile != this) {
            throw new IllegalArgumentException("ResourceNode does not belong to this PEFile");
        }
        final var table = dirNode.entry instanceof ImageResourceDirectory ? (ImageResourceDirectory) dirNode.entry : null;
        if (table == null) {
            return List.of();
        }
        //noinspection DataFlowIssue
        var beginRva = dataDirectories.getResourceTable().getRva();

        return new AbstractList<>() {
            @Override
            public ResourceNode get(int index) {
                Objects.checkIndex(index, size());
                var data = new byte[8];
                var offset = index * 8 + ImageResourceDirectory.LENGTH;
                try {
                    rsrcSection.copyBytes(data, beginRva + offset + dirNode.irdOffset, 0, data.length);
                    var namePtr = I.u32(data, 0);
                    var offsetToData = I.u32(data, 4);
                    var resourceID = 0;
                    String name;
                    if (namePtr < 0) {
                        namePtr &= 0x7fffffff;
                        var lenBuf = new byte[2];
                        sectionSet.readBytes(lenBuf, namePtr + beginRva);
                        var len = Short.toUnsignedInt(I.u16(lenBuf, 0));
                        if (len > 4096) {
                            resourceNameTooLong(len, offset);
                        }
                        if (len == 0) {
                            resourceNameZeroLength(offset);
                        }
                        var nameBuf = new byte[len * 2];
                        sectionSet.readBytes(nameBuf, namePtr + beginRva + 2);
                        name = new String(nameBuf, 0, len * 2, StandardCharsets.UTF_16LE);
                    } else {
                        resourceID = namePtr;
                        name = "<ID:" + Integer.toUnsignedLong(resourceID) + ">";
                    }
                    Object newTable;
                    if (offsetToData < 0) {
                        // is directory
                        offsetToData = offsetToData & 0x7fffffff;
                        var dataBuf = new byte[ImageResourceDirectory.LENGTH];
                        rsrcSection.copyBytes(dataBuf, beginRva + offsetToData, 0, dataBuf.length);
                        newTable = ImageResourceDirectory.parse(dataBuf, 0);
                    } else {
                        // is leaf, data entry
                        var dataBuf = new byte[ImageResourceDataEntry.LENGTH];
                        rsrcSection.copyBytes(dataBuf, beginRva + offsetToData, 0, dataBuf.length);
                        newTable = ImageResourceDataEntry.parse(dataBuf, 0);
                    }
                    return new ResourceNode(PEFile.this, name, resourceID, offsetToData, newTable);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public int size() {
                return table.numberOfNamedEntries() + table.numberOfIdEntries();
            }
        };
    }

    private void resourceNameZeroLength(int entryOffset) {
        throw new PEFileException("Resource name zero length at offset: " + Integer.toUnsignedLong(entryOffset));
    }

    private void resourceNameTooLong(int len, int entryOffset) {
        throw new PEFileException("Resource name too long: " +
                                  Integer.toUnsignedLong(len) +
                                  " at offset: " +
                                  Integer.toUnsignedLong(entryOffset));
    }

    private interface Wrap<R> {
        R call() throws IOException;
    }
}

