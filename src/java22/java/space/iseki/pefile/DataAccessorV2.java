package space.iseki.pefile;

/*
This file is used within a Multi-Release Jar file.
All modifications should be reflected in other source-roots.
 */

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("unused")
interface DataAccessorV2 extends AutoCloseable {
    int read(ByteBuffer buffer, long position) throws IOException;

    boolean isOpen();
}

@SuppressWarnings("unused")
final class MmapHelper {
    public static boolean isSupported() {
        return true;
    }

    @SuppressWarnings({"unused", "RedundantThrows"})
    public static DataAccessorV2 accessorOf(FileChannel channel) throws IOException {
        Arena arena = Arena.ofShared();
        try {
            var ms = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size(), arena);
            return new MmapDataAccessorV2(ms, arena);
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


final class MmapDataAccessorV2 implements DataAccessorV2 {
    private final MemorySegment segment;
    private final Arena arena;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public MmapDataAccessorV2(MemorySegment segment, Arena arena) {
        this.segment = Objects.requireNonNull(segment);
        this.arena = Objects.requireNonNull(arena);
    }

    @Override
    public int read(ByteBuffer buffer, long position) {
        var readLock = lock.readLock();
        try {
            readLock.lock();
            if (!arena.scope().isAlive()) {
                throw new IllegalStateException("The file was closed");
            }
            var byteSize = segment.byteSize();
            if (position >= byteSize) {
                return -1;
            }
            var bytesToCopy = Math.min(Integer.toUnsignedLong(buffer.remaining()), byteSize - position);
            buffer = buffer.slice(buffer.position(), (int) bytesToCopy);
            MemorySegment.copy(segment, position, MemorySegment.ofBuffer(buffer), 0, bytesToCopy);
            return (int) bytesToCopy;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isOpen() {
        return arena.scope().isAlive();
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void close() throws Exception {
        var writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (!arena.scope().isAlive()) return;
            arena.close();
        } finally {
            writeLock.unlock();
        }
    }
}
