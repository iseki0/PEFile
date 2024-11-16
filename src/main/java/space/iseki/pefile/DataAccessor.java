package space.iseki.pefile;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

final class DataAccessors {

    public static DataAccessor of(Path path) throws IOException {
        var ch = FileChannel.open(path, StandardOpenOption.READ);
        try {
            return Optional.ofNullable(MmapHelper.of(ch)).orElseGet(() -> new SeekableByteChannelDataAccessor(ch));
        } catch (Throwable th) {
            try {
                ch.close();
            } catch (Throwable t) {
                th.addSuppressed(t);
            }
            throw th;
        }
    }
}

final class MmapHelper {
    public static boolean isSupported() {
        return false;
    }

    @SuppressWarnings("unused")
    public static DataAccessor of(FileChannel channel) throws IOException {
        return null;
    }
}

final class SeekableByteChannelDataAccessor implements DataAccessor {
    private final Lock lock = new ReentrantLock();
    private final SeekableByteChannel channel;
    boolean closeUnderlyingChannel = true;

    SeekableByteChannelDataAccessor(SeekableByteChannel channel) {
        this.channel = channel;
    }

    @Override
    public int readAtMost(long pos, byte[] buf, int off, int len) throws IOException {
        try {
            lock.lock();
            channel.position(pos);
            var bb = ByteBuffer.wrap(buf, off, len);
            int totalRead = 0;
            while (bb.hasRemaining()) {
                var n = channel.read(bb);
                if (n == -1) break;
                totalRead += n;
            }
            return totalRead;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (closeUnderlyingChannel) channel.close();
    }
}

