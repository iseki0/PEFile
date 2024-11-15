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
    void readFully(long pos, byte[] buf, int off, int len) throws IOException;

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
    public void readFully(long pos, byte[] buf, int off, int len) throws IOException {
        try {
            lock.lock();
            channel.position(pos);
            var bb = ByteBuffer.wrap(buf, off, len);
            while (bb.hasRemaining()) {
                if (channel.read(bb) == -1) {
                    throw new EOFException();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int readAtMost(long pos, byte[] buf, int off, int len) throws IOException {
        try {
            lock.lock();
            channel.position(pos);
            var bb = ByteBuffer.wrap(buf, off, len);
            return channel.read(bb);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        if (closeUnderlyingChannel) channel.close();
    }
}

