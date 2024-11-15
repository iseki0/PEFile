package space.iseki.pefile;

import java.util.ArrayList;
import java.util.List;

public final class Characteristics {
    /**
     * Image only, Windows CE, and Microsoft Windows NT and later.
     * <p>
     * This indicates that the file does not contain base relocations and must therefore be loaded at its preferred base address.
     * If the base address is not available, the loader reports an error.
     * The default behavior of the linker is to strip base relocations from executable (EXE) files.
     */
    public static final short IMAGE_FILE_RELOCS_STRIPPED = (short) 0x0001;
    /**
     * Image only. This indicates that the image file is valid and can be run.
     * <p>
     * If this flag is not set, it indicates a linker error.
     */
    public static final short IMAGE_FILE_EXECUTABLE_IMAGE = (short) 0x0002;
    /**
     * COFF line numbers have been removed.
     * <p>
     * This flag is deprecated and should be zero.
     */
    public static final short IMAGE_FILE_LINE_NUMS_STRIPPED = (short) 0x0004;
    /**
     * COFF symbol table entries for local symbols have been removed.
     * <p>
     * This flag is deprecated and should be zero.
     */
    public static final short IMAGE_FILE_LOCAL_SYMS_STRIPPED = (short) 0x0008;
    /**
     * Obsolete. Aggressively trim working set.
     * <p>
     * This flag is deprecated for Windows 2000 and later and must be zero.
     */
    public static final short IMAGE_FILE_AGGRESSIVE_WS_TRIM = (short) 0x0010;
    /**
     * Application can handle > 2-GB addresses.
     */
    public static final short IMAGE_FILE_LARGE_ADDRESS_AWARE = (short) 0x0020;
    /**
     * Little endian: the least significant bit (LSB) precedes the most significant bit (MSB) in memory.
     * <p>
     * This flag is deprecated and should be zero.
     */
    public static final short IMAGE_FILE_BYTES_REVERSED_LO = (short) 0x0080;
    /**
     * Machine is based on a 32-bit-word architecture.
     */
    public static final short IMAGE_FILE_32BIT_MACHINE = (short) 0x0100;
    /**
     * Debugging information is removed from the image file.
     */
    public static final short IMAGE_FILE_DEBUG_STRIPPED = (short) 0x0200;
    /**
     * If the image is on removable media, fully load it and copy it to the swap file.
     */
    public static final short IMAGE_FILE_REMOVABLE_RUN_FROM_SWAP = (short) 0x0400;
    /**
     * If the image is on network media, fully load it and copy it to the swap file.
     */
    public static final short IMAGE_FILE_NET_RUN_FROM_SWAP = (short) 0x0800;
    /**
     * The image file is a system file, not a user program.
     */
    public static final short IMAGE_FILE_SYSTEM = (short) 0x1000;
    /**
     * The image file is a dynamic-link library (DLL).
     * <p>
     * Such files are considered executable files for almost all purposes, although they cannot be directly run.
     */
    public static final short IMAGE_FILE_DLL = (short) 0x2000;
    /**
     * The file should be run only on a uniprocessor machine.
     */
    public static final short IMAGE_FILE_UP_SYSTEM_ONLY = (short) 0x4000;
    /**
     * Big endian: the MSB precedes the LSB in memory.
     * <p>
     * This flag is deprecated and should be zero.
     */
    public static final short IMAGE_FILE_BYTES_REVERSED_HI = (short) 0x8000;

    private Characteristics() {
    }

    public static List<String> toList(short bits) {
        var l = new ArrayList<String>(16);
        if ((bits & IMAGE_FILE_RELOCS_STRIPPED) != 0) l.add("RELOCS_STRIPPED");
        if ((bits & IMAGE_FILE_EXECUTABLE_IMAGE) != 0) l.add("EXECUTABLE_IMAGE");
        if ((bits & IMAGE_FILE_LINE_NUMS_STRIPPED) != 0) l.add("LINE_NUMS_STRIPPED");
        if ((bits & IMAGE_FILE_LOCAL_SYMS_STRIPPED) != 0) l.add("LOCAL_SYMS_STRIPPED");
        if ((bits & IMAGE_FILE_AGGRESSIVE_WS_TRIM) != 0) l.add("AGGRESSIVE_WS_TRIM");
        if ((bits & IMAGE_FILE_LARGE_ADDRESS_AWARE) != 0) l.add("LARGE_ADDRESS_AWARE");
        if ((bits & IMAGE_FILE_BYTES_REVERSED_LO) != 0) l.add("BYTES_REVERSED_LO");
        if ((bits & IMAGE_FILE_32BIT_MACHINE) != 0) l.add("32BIT_MACHINE");
        if ((bits & IMAGE_FILE_DEBUG_STRIPPED) != 0) l.add("DEBUG_STRIPPED");
        if ((bits & IMAGE_FILE_REMOVABLE_RUN_FROM_SWAP) != 0) l.add("REMOVABLE_RUN_FROM_SWAP");
        if ((bits & IMAGE_FILE_NET_RUN_FROM_SWAP) != 0) l.add("NET_RUN_FROM_SWAP");
        if ((bits & IMAGE_FILE_SYSTEM) != 0) l.add("SYSTEM");
        if ((bits & IMAGE_FILE_DLL) != 0) l.add("DLL");
        if ((bits & IMAGE_FILE_UP_SYSTEM_ONLY) != 0) l.add("UP_SYSTEM_ONLY");
        if ((bits & IMAGE_FILE_BYTES_REVERSED_HI) != 0) l.add("BYTES_REVERSED_HI");
        return l;
    }

    public static String toString(short bits) {
        return String.join("|", toList(bits));
    }
}
