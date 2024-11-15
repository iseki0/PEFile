package space.iseki.pefile;

public final class WindowsSubsystems {
    /**
     * An unknown subsystem
     */
    public static final short UNKNOWN = 0;
    /**
     * Device drivers and native Windows processes
     */
    public static final short NATIVE = 1;
    /**
     * The Windows graphical user interface (GUI) subsystem
     */
    public static final short WINDOWS_GUI = 2;
    /**
     * The Windows character subsystem
     */
    public static final short WINDOWS_CUI = 3;
    /**
     * The OS/2 character subsystem
     */
    public static final short OS2_CUI = 5;
    /**
     * The Posix character subsystem
     */
    public static final short POSIX_CUI = 7;
    /**
     * Native Win9x driver
     */
    public static final short NATIVE_WINDOWS = 8;
    /**
     * Windows CE
     */
    public static final short WINDOWS_CE_GUI = 9;
    /**
     * An Extensible Firmware Interface (EFI) application
     */
    public static final short EFI_APPLICATION = 10;
    /**
     * An EFI driver with boot services
     */
    public static final short EFI_BOOT_SERVICE_DRIVER = 11;
    /**
     * An EFI driver with run-time services
     */
    public static final short EFI_RUNTIME_DRIVER = 12;
    /**
     * An EFI ROM image
     */
    public static final short EFI_ROM = 13;
    /**
     * XBOX
     */
    public static final short XBOX = 14;
    /**
     * Windows boot application.
     */
    public static final short WINDOWS_BOOT_APPLICATION = 16;

    private WindowsSubsystems() {
    }

    public static String toString(short i) {
        switch (i) {
            case UNKNOWN:
                return "UNKNOWN";
            case NATIVE:
                return "NATIVE";
            case WINDOWS_GUI:
                return "WINDOWS_GUI";
            case WINDOWS_CUI:
                return "WINDOWS_CUI";
            case OS2_CUI:
                return "OS2_CUI";
            case POSIX_CUI:
                return "POSIX_CUI";
            case NATIVE_WINDOWS:
                return "NATIVE_WINDOWS";
            case WINDOWS_CE_GUI:
                return "WINDOWS_CE_GUI";
            case EFI_APPLICATION:
                return "EFI_APPLICATION";
            case EFI_BOOT_SERVICE_DRIVER:
                return "EFI_BOOT_SERVICE_DRIVER";
            case EFI_RUNTIME_DRIVER:
                return "EFI_RUNTIME_DRIVER";
            case EFI_ROM:
                return "EFI_ROM";
            case XBOX:
                return "XBOX";
            case WINDOWS_BOOT_APPLICATION:
                return "WINDOWS_BOOT_APPLICATION";
            default:
                return "Unknown(" + Short.toUnsignedInt(i) + ")";
        }
    }
}
