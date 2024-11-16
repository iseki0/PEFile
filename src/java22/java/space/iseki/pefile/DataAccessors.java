package space.iseki.pefile;

import java.io.EOFException;
import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;

@SuppressWarnings("unused")
interface DataAccessor extends AutoCloseable {
    default void readFully(long pos, byte[] buf, int off, int len) throws IOException {
        var n = readAtMost(pos, buf, off, len);
        if (n != len) {
            throw new EOFException("Expected " + len + " bytes, but only read " + n + " bytes");
        }
    }

    default void readFully(long pos, byte[] buf) throws IOException {
        readFully(pos, buf, 0, buf.length);
    }

    int readAtMost(long pos, byte[] buf, int off, int len) throws IOException;

    default int readAtMost(long pos, byte[] buf) throws IOException {
        return readAtMost(pos, buf, 0, buf.length);
    }
}

class MemorySegmentArenaDataAccessor implements DataAccessor {
    private final MemorySegment segment;
    private final Arena arena;

    MemorySegmentArenaDataAccessor(MemorySegment segment, Arena arena) {
        this.segment = segment;
        this.arena = arena;
    }

    @Override
    public void readFully(long pos, byte[] buf, int off, int len) throws IOException {
        var bs = segment.byteSize();
        if (pos > bs || pos + Integer.toUnsignedLong(len) > bs) {
            throw new EOFException();
        }
        MemorySegment.copy(segment,
                           ValueLayout.JAVA_BYTE,
                           pos,
                           MemorySegment.ofArray(buf),
                           ValueLayout.JAVA_BYTE,
                           off,
                           len);
    }

    @Override
    public int readAtMost(long pos, byte[] buf, int off, int len) throws IOException {
        var remain = segment.byteSize() - pos;
        if (remain < 1) {
            return -1;
        }
        var realLen = Long.compareUnsigned(Integer.toUnsignedLong(len), remain) < 0 ? len : (int) remain;
        readFully(pos, buf, off, realLen);
        return realLen;
    }

    @Override
    public void close() {
        arena.close();
    }
}

@SuppressWarnings("unused")
final class MmapHelper {
    public static boolean isSupported() {
        return true;
    }

    public static DataAccessor of(FileChannel ch) throws IOException {
        Arena arena = Arena.ofShared();
        try {
            var ms = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size(), arena);
            return new MemorySegmentArenaDataAccessor(ms, arena);
        } catch (Throwable th) {
            try {
                arena.close();
            } catch (Throwable t) {
                th.addSuppressed(t);
            }
            throw th;
        }
    }
}

