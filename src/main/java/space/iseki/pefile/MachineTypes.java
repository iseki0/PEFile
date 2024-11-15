package space.iseki.pefile;

public final class MachineTypes {
    /**
     * The content of this field is assumed to be applicable to any machine type
     */
    public static final short UNKNOWN = (short) 0x0;
    /**
     * Alpha AXP, 32-bit address space
     */
    public static final short ALPHA = (short) 0x184;
    /**
     * Alpha 64, 64-bit address space
     */
    public static final short ALPHA64 = (short) 0x284;
    /**
     * Matsushita AM33
     */
    public static final short AM33 = (short) 0x1d3;
    /**
     * x64
     */
    public static final short AMD64 = (short) 0x8664;
    /**
     * ARM little endian
     */
    public static final short ARM = (short) 0x1c0;
    /**
     * ARM64 little endian
     */
    public static final short ARM64 = (short) 0xaa64;
    /**
     * ARM Thumb-2 little endian
     */
    public static final short ARMNT = (short) 0x1c4;
    /**
     * EFI byte code
     */
    public static final short EBC = (short) 0xebc;
    /**
     * Intel 386 or later processors and compatible processors
     */
    public static final short I386 = (short) 0x14c;
    /**
     * Intel Itanium processor family
     */
    public static final short IA64 = (short) 0x200;
    /**
     * LoongArch 32-bit processor family
     */
    public static final short LOONGARCH32 = (short) 0x6232;
    /**
     * LoongArch 64-bit processor family
     */
    public static final short LOONGARCH64 = (short) 0x6264;
    /**
     * Mitsubishi M32R little endian
     */
    public static final short M32R = (short) 0x9041;
    /**
     * MIPS16
     */
    public static final short MIPS16 = (short) 0x266;
    /**
     * MIPS with FPU
     */
    public static final short MIPSFPU = (short) 0x366;
    /**
     * MIPS16 with FPU
     */
    public static final short MIPSFPU16 = (short) 0x466;
    /**
     * Power PC little endian
     */
    public static final short POWERPC = (short) 0x1f0;
    /**
     * Power PC with floating point support
     */
    public static final short POWERPCFP = (short) 0x1f1;
    /**
     * MIPS little endian
     */
    public static final short R4000 = (short) 0x166;
    /**
     * RISC-V 32-bit address space
     */
    public static final short RISCV32 = (short) 0x5032;
    /**
     * RISC-V 64-bit address space
     */
    public static final short RISCV64 = (short) 0x5064;
    /**
     * RISC-V 128-bit address space
     */
    public static final short RISCV128 = (short) 0x5128;
    /**
     * Hitachi SH3
     */
    public static final short SH3 = (short) 0x1a2;
    /**
     * Hitachi SH3 DSP
     */
    public static final short SH3DSP = (short) 0x1a3;
    /**
     * Hitachi SH4
     */
    public static final short SH4 = (short) 0x1a6;
    /**
     * Hitachi SH5
     */
    public static final short SH5 = (short) 0x1a8;
    /**
     * Thumb
     */
    public static final short THUMB = (short) 0x1c2;
    /**
     * MIPS little-endian WCE v2
     */
    public static final short WCEMIPSV2 = (short) 0x169;

    private MachineTypes() {
    }

    public static String nameOf(short machineType) {
        switch (machineType) {
            case UNKNOWN:
                return "UNKNOWN";
            case ALPHA:
                return "ALPHA";
            case ALPHA64:
                return "ALPHA64";
            case AM33:
                return "AM33";
            case AMD64:
                return "AMD64";
            case ARM:
                return "ARM";
            case ARM64:
                return "ARM64";
            case ARMNT:
                return "ARMNT";
            case EBC:
                return "EBC";
            case I386:
                return "I386";
            case IA64:
                return "IA64";
            case LOONGARCH32:
                return "LOONGARCH32";
            case LOONGARCH64:
                return "LOONGARCH64";
            case M32R:
                return "M32R";
            case MIPS16:
                return "MIPS16";
            case MIPSFPU:
                return "MIPSFPU";
            case MIPSFPU16:
                return "MIPSFPU16";
            case POWERPC:
                return "POWERPC";
            case POWERPCFP:
                return "POWERPCFP";
            case R4000:
                return "R4000";
            case RISCV32:
                return "RISCV32";
            case RISCV64:
                return "RISCV64";
            case RISCV128:
                return "RISCV128";
            case SH3:
                return "SH3";
            case SH3DSP:
                return "SH3DSP";
            case SH4:
                return "SH4";
            case SH5:
                return "SH5";
            case THUMB:
                return "THUMB";
            case WCEMIPSV2:
                return "WCEMIPSV2";
            default:
                return "UNKNOWN(" + U.hex(machineType) + ")";
        }
    }
}
