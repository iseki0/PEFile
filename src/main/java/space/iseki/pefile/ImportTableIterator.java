package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public final class ImportTableIterator extends AbstractIterator<@NotNull ImportEntry> {
    private final InputStream input;
    private final PEFile peFile;

    ImportTableIterator(PEFile peFile) {
        this.peFile = peFile;
        DataDirectories.Item importTable = peFile.dataDirectories.getImportTable();
        if (importTable == null) {
            input = null;
            return;
        }
        var section = peFile.sectionSet.find(importTable.getRva());
        if (section == null) {
            input = null;
            return;
        }
        input = section.inputStream(importTable.getRva());
    }

    /**
     * {@inheritDoc}
     *
     * @throws UncheckedIOException if an I/O error occurs
     * @throws PEFileException      if the file is not a valid PE file
     */
    @Override
    public boolean hasNext() {
        return super.hasNext();
    }

    @Override
    protected void computeNext() {
        try {
            if (input == null) {
                end();
                return;
            }
            var buf = input.readNBytes(ImportDirectoryTable.LENGTH);
            var idt = ImportDirectoryTable.parse(buf, 0);
            if (idt.importLookupTableRva() == 0) {
                end();
                return;
            }
            var name = peFile.sectionSet.readNullShortString(idt.nameRva());
            setNext(new ImportEntry(peFile, idt, name));
        } catch (EOFException | IndexOutOfBoundsException e) {
            throw new PEFileException("incomplete ImportDirectoryTable read");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
