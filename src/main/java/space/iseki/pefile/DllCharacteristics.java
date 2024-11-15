package space.iseki.pefile;

import java.util.ArrayList;
import java.util.List;

public final class DllCharacteristics {

    /**
     * Image can handle a high entropy 64-bit virtual address space.
     */
    public static final short HIGH_ENTROPY_VA = (short) 0x0020;
    /**
     * DLL can be relocated at load time.
     */
    public static final short DYNAMIC_BASE = (short) 0x0040;
    /**
     * Code Integrity checks are enforced.
     */
    public static final short FORCE_INTEGRITY = (short) 0x0080;
    /**
     * Image is NX compatible.
     */
    public static final short NX_COMPAT = (short) 0x0100;
    /**
     * Isolation aware, but do not isolate the image.
     */
    public static final short NO_ISOLATION = (short) 0x0200;
    /**
     * Does not use structured exception (SE) handling. No SE handler may be called in this image.
     */
    public static final short NO_SEH = (short) 0x0400;
    /**
     * Do not bind the image.
     */
    public static final short NO_BIND = (short) 0x0800;
    /**
     * Image must execute in an AppContainer.
     */
    public static final short APPCONTAINER = (short) 0x1000;
    /**
     * A WDM driver.
     */
    public static final short WDM_DRIVER = (short) 0x2000;
    /**
     * Image supports Control Flow Guard.
     */
    public static final short GUARD_CF = (short) 0x4000;
    /**
     * Terminal Server aware.
     */
    public static final short TERMINAL_SERVER_AWARE = (short) 0x8000;

    private DllCharacteristics() {
    }

    public static List<String> toList(short bits) {
        var l = new ArrayList<String>(16);
        if ((HIGH_ENTROPY_VA & bits) != 0) l.add("HIGH_ENTROPY_VA");
        if ((DYNAMIC_BASE & bits) != 0) l.add("DYNAMIC_BASE");
        if ((FORCE_INTEGRITY & bits) != 0) l.add("FORCE_INTEGRITY");
        if ((NX_COMPAT & bits) != 0) l.add("NX_COMPAT");
        if ((NO_ISOLATION & bits) != 0) l.add("NO_ISOLATION");
        if ((NO_SEH & bits) != 0) l.add("NO_SEH");
        if ((NO_BIND & bits) != 0) l.add("NO_BIND");
        if ((APPCONTAINER & bits) != 0) l.add("APPCONTAINER");
        if ((WDM_DRIVER & bits) != 0) l.add("WDM_DRIVER");
        if ((GUARD_CF & bits) != 0) l.add("GUARD_CF");
        if ((TERMINAL_SERVER_AWARE & bits) != 0) l.add("TERMINAL_SERVER_AWARE");
        return l;
    }

    public static String toString(short bits) {
        return String.join("|", toList(bits));
    }
}
