package space.iseki.pefile;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.Objects;

final class I {
    public static final VarHandle IA = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN)
                                                    .withInvokeExactBehavior();
    public static final VarHandle SA = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.LITTLE_ENDIAN)
                                                    .withInvokeExactBehavior();
    public static final VarHandle LA = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN)
                                                    .withInvokeExactBehavior();

    public static short u16(byte[] buf, int off) {
        return (short) SA.get(buf, off);
    }

    public static int u32(byte[] buf, int off) {
        return (int) IA.get(buf, off);
    }

    public static long u32L(byte[] buf, int off) {
        return Integer.toUnsignedLong(u32(buf, off));
    }

    public static long u64(byte[] buf, int off) {
        return (long) LA.get(buf, off);
    }

}


final class U {
    public static String getNullString(byte[] buf, int off, int len) {
        var end = off + len;
        for (var i = off; i < end; i++) {
            if (buf[i] == 0) {
                return new String(buf, off, i - off, StandardCharsets.ISO_8859_1);
            }
        }
        return new String(buf, off, len, StandardCharsets.ISO_8859_1);
    }

    public static String hex(byte[] bytes, int off, int len){
        return HexFormat.of().formatHex(bytes, off, len);
    }

    public static String hex(int i) {
        return "0x" + Integer.toHexString(i);
    }

    public static String hex(long i) {
        return "0x" + Long.toHexString(i);
    }

    public static String hex(short i) {
        return "0x" + Integer.toHexString(i);
    }

    public static String hex(byte i) {
        return "0x" + Integer.toHexString(i);
    }

    public static String fieldHex(String name, long i) {
        return field(name, hex(i));
    }

    public static String fieldHex(String name, int i) {
        return field(name, hex(i));
    }

    public static String fieldHex(String name, short i) {
        return field(name, hex(i));
    }

    public static String fieldHex(String name, byte i) {
        return field(name, hex(i));
    }

    public static String field(String name, long i) {
        return field(name, Long.toUnsignedString(i));
    }

    public static String field(String name, int i) {
        return field(name, Integer.toUnsignedString(i));
    }

    public static String field(String name, short i) {
        return field(name, Short.toUnsignedInt(i));
    }

    public static String field(String name, byte i) {
        return field(name, Byte.toUnsignedInt(i));
    }

    public static String field(String name, boolean i) {
        return field(name, Boolean.toString(i));
    }

    public static String field(String name, Object i) {
        return field(name, Objects.toString(i));
    }

    public static String field(String name, String i) {
        return name + "=" + i;
    }

    public static String fieldEscape(String name, String i) {
        return name + "=\"" + i.translateEscapes() + "\"";
    }

    public static String structure(String name, String... fields) {
        return name + "{" + String.join(", ", fields) + "}";
    }

    public static String timeDateU32(int i) {
        return Instant.ofEpochSecond(Integer.toUnsignedLong(i)).toString();
    }

    public static long max(long a, long b) {
        return Long.compareUnsigned(a, b) > 0 ? a : b;
    }

    public static long min(long a, long b) {
        return Long.compareUnsigned(a, b) < 0 ? a : b;
    }

    public static int max(int a, int b) {
        return Integer.compareUnsigned(a, b) > 0 ? a : b;
    }

    public static int min(int a, int b) {
        return Integer.compareUnsigned(a, b) < 0 ? a : b;
    }

    private static void throwOverflow(String op, long a, long b) {
        throw new ArithmeticException("integer overflow: " +
                                      Long.toUnsignedString(a) +
                                      " " +
                                      op +
                                      " " +
                                      Long.toUnsignedString(b));
    }

    private static void throwOverflow(String op, int a, int b) {
        throwOverflow(op, Integer.toUnsignedLong(a), Integer.toUnsignedLong(b));
    }

    public static int add(int a, int b) {
        var r = a + b;
        if (Integer.compareUnsigned(r, a) < 0) {
            throwOverflow("+", a, b);
        }
        return r;
    }

    public static long add(long a, long b) {
        var r = a + b;
        if (Long.compareUnsigned(r, a) < 0) {
            throwOverflow("+", a, b);
        }
        return r;
    }

    public static int sub(int a, int b) {
        var r = a - b;
        if (Integer.compareUnsigned(r, a) > 0) {
            throwOverflow("-", a, b);
        }
        return r;
    }

    public static long sub(long a, long b) {
        var r = a - b;
        if (Long.compareUnsigned(r, a) > 0) {
            throwOverflow("-", a, b);
        }
        return r;
    }

    public static int cmp(int x, int y) {
        return Integer.compareUnsigned(x, y);
    }

    public static int cmp(long x, long y) {
        return Long.compareUnsigned(x, y);
    }

    public static boolean gt(int x, int y) {
        return Integer.compareUnsigned(x, y) > 0;
    }

    public static boolean gt(long x, long y) {
        return Long.compareUnsigned(x, y) > 0;
    }

    public static boolean ge(int x, int y) {
        return Integer.compareUnsigned(x, y) >= 0;
    }

    public static boolean ge(long x, long y) {
        return Long.compareUnsigned(x, y) >= 0;
    }

    public static boolean lt(int x, int y) {
        return Integer.compareUnsigned(x, y) < 0;
    }

    public static boolean lt(long x, long y) {
        return Long.compareUnsigned(x, y) < 0;
    }

    public static boolean le(int x, int y) {
        return Integer.compareUnsigned(x, y) <= 0;
    }

    public static boolean le(long x, long y) {
        return Long.compareUnsigned(x, y) <= 0;
    }

}

abstract class AbstractIterator<T> implements Iterator<T> {
    private static final Object END = new Object();
    private static final Object NOT_READY = new Object();
    private Object next = NOT_READY;

    abstract protected void computeNext();

    protected void end() {
        next = END;
    }

    protected void setNext(T value) {
        next = value;
    }

    @Override
    public boolean hasNext() {
        if (next == END) {
            return false;
        }
        if (next == NOT_READY) {
            computeNext();
            if (next == NOT_READY) {
                throw new IllegalStateException();
            }
            return hasNext();
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T next() {
        if (!hasNext()) {
            throw new IllegalStateException();
        }
        var result = next;
        next = NOT_READY;
        return (T) result;
    }
}
