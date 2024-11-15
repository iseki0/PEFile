package space.iseki.pefile;

import java.util.ArrayList;
import java.util.List;

public final class SectionFlags {
    /**
     * The section should not be padded to the next boundary.
     * <p>
     * This flag is obsolete and is replaced by IMAGE_SCN_ALIGN_1BYTES. This is valid only for object files.
     */
    public static final int TYPE_NO_PAD = 0x00000008;
    /**
     * The section contains executable code.
     */
    public static final int CNT_CODE = 0x00000020;
    /**
     * The section contains initialized data.
     */
    public static final int CNT_INITIALIZED_DATA = 0x00000040;
    /**
     * The section contains uninitialized data.
     */
    public static final int CNT_UNINITIALIZED_DATA = 0x00000080;
    /**
     * Reserved for future use.
     */
    public static final int LNK_OTHER = 0x00000100;
    /**
     * The section contains comments or other information.
     * <p>
     * The {@code .drectve} section has this type. This is valid for object files only.
     */
    public static final int LNK_INFO = 0x00000200;
    /**
     * The section will not become part of the image. This is valid only for object files.
     */
    public static final int LNK_REMOVE = 0x00000800;
    /**
     * The section contains COMDAT data. For more information, see COMDAT Sections (Object Only). This is valid only for object files.
     */
    public static final int LNK_COMDAT = 0x00001000;
    /**
     * The section contains data referenced through the global pointer (GP).
     */
    public static final int GPREL = 0x00008000;
    /**
     * Reserved for future use.
     */
    public static final int MEM_PURGEABLE = 0x00020000;
    /**
     * Reserved for future use.
     */
    public static final int MEM_16BIT = 0x00020000;
    /**
     * Reserved for future use.
     */
    public static final int MEM_LOCKED = 0x00040000;
    /**
     * Reserved for future use.
     */
    public static final int MEM_PRELOAD = 0x00080000;
    /**
     * Align data on a 1-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_1BYTES = 0x00100000;
    /**
     * Align data on a 2-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_2BYTES = 0x00200000;
    /**
     * Align data on a 4-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_4BYTES = 0x00300000;
    /**
     * Align data on an 8-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_8BYTES = 0x00400000;
    /**
     * Align data on a 16-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_16BYTES = 0x00500000;
    /**
     * Align data on a 32-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_32BYTES = 0x00600000;
    /**
     * Align data on a 64-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_64BYTES = 0x00700000;
    /**
     * Align data on a 128-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_128BYTES = 0x00800000;
    /**
     * Align data on a 256-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_256BYTES = 0x00900000;
    /**
     * Align data on a 512-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_512BYTES = 0x00A00000;
    /**
     * Align data on a 1024-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_1024BYTES = 0x00B00000;
    /**
     * Align data on a 2048-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_2048BYTES = 0x00C00000;
    /**
     * Align data on a 4096-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_4096BYTES = 0x00D00000;
    /**
     * Align data on an 8192-byte boundary. Valid only for object files.
     */
    public static final int ALIGN_8192BYTES = 0x00E00000;
    /**
     * The section contains extended relocations.
     */
    public static final int LNK_NRELOC_OVFL = 0x01000000;
    /**
     * The section can be discarded as needed.
     */
    public static final int MEM_DISCARDABLE = 0x02000000;
    /**
     * The section cannot be cached.
     */
    public static final int MEM_NOT_CACHED = 0x04000000;
    /**
     * The section is not pageable.
     */
    public static final int MEM_NOT_PAGED = 0x08000000;
    /**
     * The section can be shared in memory.
     */
    public static final int MEM_SHARED = 0x10000000;
    /**
     * The section can be executed as code.
     */
    public static final int MEM_EXECUTE = 0x20000000;
    /**
     * The section can be read.
     */
    public static final int MEM_READ = 0x40000000;
    /**
     * The section can be written to.
     */
    public static final int MEM_WRITE = 0x80000000;

    private SectionFlags() {
    }

    public static List<String> toList(int bits) {
        var l = new ArrayList<String>(16);
        if ((TYPE_NO_PAD & bits) != 0) l.add("TYPE_NO_PAD");
        if ((CNT_CODE & bits) != 0) l.add("CNT_CODE");
        if ((CNT_INITIALIZED_DATA & bits) != 0) l.add("CNT_INITIALIZED_DATA");
        if ((CNT_UNINITIALIZED_DATA & bits) != 0) l.add("CNT_UNINITIALIZED_DATA");
        if ((LNK_OTHER & bits) != 0) l.add("LNK_OTHER");
        if ((LNK_INFO & bits) != 0) l.add("LNK_INFO");
        if ((LNK_REMOVE & bits) != 0) l.add("LNK_REMOVE");
        if ((LNK_COMDAT & bits) != 0) l.add("LNK_COMDAT");
        if ((GPREL & bits) != 0) l.add("GPREL");
        if ((MEM_PURGEABLE & bits) != 0) l.add("MEM_PURGEABLE");
        if ((MEM_16BIT & bits) != 0) l.add("MEM_16BIT");
        if ((MEM_LOCKED & bits) != 0) l.add("MEM_LOCKED");
        if ((MEM_PRELOAD & bits) != 0) l.add("MEM_PRELOAD");
        if ((ALIGN_1BYTES & bits) != 0) l.add("ALIGN_1BYTES");
        if ((ALIGN_2BYTES & bits) != 0) l.add("ALIGN_2BYTES");
        if ((ALIGN_4BYTES & bits) != 0) l.add("ALIGN_4BYTES");
        if ((ALIGN_8BYTES & bits) != 0) l.add("ALIGN_8BYTES");
        if ((ALIGN_16BYTES & bits) != 0) l.add("ALIGN_16BYTES");
        if ((ALIGN_32BYTES & bits) != 0) l.add("ALIGN_32BYTES");
        if ((ALIGN_64BYTES & bits) != 0) l.add("ALIGN_64BYTES");
        if ((ALIGN_128BYTES & bits) != 0) l.add("ALIGN_128BYTES");
        if ((ALIGN_256BYTES & bits) != 0) l.add("ALIGN_256BYTES");
        if ((ALIGN_512BYTES & bits) != 0) l.add("ALIGN_512BYTES");
        if ((ALIGN_1024BYTES & bits) != 0) l.add("ALIGN_1024BYTES");
        if ((ALIGN_2048BYTES & bits) != 0) l.add("ALIGN_2048BYTES");
        if ((ALIGN_4096BYTES & bits) != 0) l.add("ALIGN_4096BYTES");
        if ((ALIGN_8192BYTES & bits) != 0) l.add("ALIGN_8192BYTES");
        if ((LNK_NRELOC_OVFL & bits) != 0) l.add("LNK_NRELOC_OVFL");
        if ((MEM_DISCARDABLE & bits) != 0) l.add("MEM_DISCARDABLE");
        if ((MEM_NOT_CACHED & bits) != 0) l.add("MEM_NOT_CACHED");
        if ((MEM_NOT_PAGED & bits) != 0) l.add("MEM_NOT_PAGED");
        if ((MEM_SHARED & bits) != 0) l.add("MEM_SHARED");
        if ((MEM_EXECUTE & bits) != 0) l.add("MEM_EXECUTE");
        if ((MEM_READ & bits) != 0) l.add("MEM_READ");
        if ((MEM_WRITE & bits) != 0) l.add("MEM_WRITE");
        return l;
    }

    public static String toString(int bits) {
        return String.join("|", toList(bits));
    }
}
