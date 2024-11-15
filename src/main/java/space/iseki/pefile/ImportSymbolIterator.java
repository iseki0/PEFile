package space.iseki.pefile;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public final class ImportSymbolIterator extends AbstractIterator<ImportSymbol> {
    private final PEFile peFile;
    private final InputStream input;
    private final ImportDirectoryTable idt;
    private int index;

    ImportSymbolIterator(PEFile peFile, ImportDirectoryTable idt) {
        this.peFile = peFile;
        this.idt = idt;
        Section section = peFile.sectionSet.find(idt.importLookupTableRva());
        if (section == null) {
            this.input = null;
            return;
        }
        input = section.inputStream(idt.importLookupTableRva());
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
        if (input == null) {
            end();
            return;
        }
        try {
            var plus = peFile.standardHeader.isPE32Plus();
            var buf = input.readNBytes(plus ? 8 : 4);
            long rawValue = plus ? I.u64(buf, 0) : I.u32L(buf, 0);
            if (rawValue == 0) {
                end();
                return;
            }
            long v = rawValue;
            if (!plus) {
                // move the 31bit to 63bit, and clear the 31bit
                v = (v & ~(1L << 31)) | ((v & (1L << 31)) << 32);
            }
            var isOrdinal = v < 0;
            // clear the sign bit
            v &= ~Long.MIN_VALUE;
            if (isOrdinal) {
                if ((v & ~0xffff) != 0) {
                    badImportLookupTableField(rawValue);
                }
            } else {
                if ((v & ~0x7fffffffL) != 0) {
                    badImportLookupTableField(rawValue);
                }
            }
            if (isOrdinal) {
                var ordinal = (int) v & 0xffff;
                setNext(new ImportSymbol("", ordinal));
            } else {
                var nameRva = (int) v & 0x7fffffff;
                setNext(new ImportSymbol(peFile.sectionSet.readNullShortString(nameRva + 2), 0));
            }
            index++;
        } catch (EOFException | IndexOutOfBoundsException e) {
            throw new PEFileException("incomplete ImportDirectoryTable read");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private void badImportLookupTableField(long v) {
        var m = "bad ImportLookupTable field[%d] in %s: %s, some bit must be 0".formatted(index, idt, U.hex(v));
        throw new PEFileException(m);
    }

}
