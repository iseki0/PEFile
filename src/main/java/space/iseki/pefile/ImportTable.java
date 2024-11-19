package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Iterator;

public final class ImportTable implements Iterable<ImportEntry> {
    private final PEFile peFile;

    ImportTable(PEFile peFile) {
        this.peFile = peFile;
    }

    /**
     * Returns an iterator over elements of type {@code ImportEntry}.
     * <p>
     * After the {@code PEFile} object is closed, the behavior of the iterator is undefined.
     * During the iteration, the following exceptions may be thrown:
     * <ul>
     *     <li>{@link IOException} if an I/O error occurs.</li>
     *     <li>{@link PEFileException} if the PE file is invalid.</li>
     *     <li>{@link IllegalStateException} if the PE file is closed.</li>
     *     </ul>
     *
     * @return an Iterator, which does not support the {@code remove} operation.
     */
    @Override
    public @NotNull Iterator<@NotNull ImportEntry> iterator() {
        return new ImportTableIterator(peFile);
    }
}
