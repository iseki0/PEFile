package space.iseki.pefile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

final class SectionSet {
    final Section[] sections;

    SectionSet(Section[] sections) {
        this.sections = sections;
    }

    public Section find(int rva) {
        for (Section section : sections) {
            if (U.gt(U.add(section.getVirtualAddress(), section.getVirtualSize()), rva) &&
                U.le(section.getVirtualAddress(), rva)) {
                return section;
            }
        }
        return null;
    }

    public void readBytes(byte[] buf, int rva) throws IOException {
        for (var s : sections) s.copyBytes(buf, rva, 0, buf.length);
    }

    public void readBytes(byte[] buf, int rva, int off, int len) throws IOException {
        for (var s : sections) s.copyBytes(buf, rva, off, len);
    }

    public String readNullShortString(int beginRva) throws IOException {
        for (var s : sections) {
            var str = s.readNullEndShortString(beginRva);
            if (str.length > 0) return new String(str, StandardCharsets.ISO_8859_1);
        }
        return null;
    }

    @Override
    public String toString() {
        return "SectionSet" + Arrays.toString(sections);
    }

}
