package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class DataAccessorReadableChannel implements SeekableByteChannel {
    private static final VarHandle ACCESSOR;
    private static final VarHandle POSITION;

    static {
        try {
            ACCESSOR = MethodHandles.lookup()
                                    .findVarHandle(DataAccessorReadableChannel.class, "accessor", DataAccessorV2.class)
                                    .withInvokeExactBehavior();
            POSITION = MethodHandles.lookup()
                                    .findVarHandle(DataAccessorReadableChannel.class, "position", long.class)
                                    .withInvokeExactBehavior();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final long size;
    private final long offset;
    private final Lock readLock = new ReentrantLock();
    @SuppressWarnings("FieldMayBeFinal")
    private DataAccessorV2 accessor;
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    private long position = 0;


    public DataAccessorReadableChannel(@NotNull DataAccessorV2 accessor, long offset, long size) {
        if (size < 0 || offset < 0) {
            throw new IllegalArgumentException("size and offset must be non-negative");
        }
        this.size = size;
        this.offset = offset;
        this.accessor = Objects.requireNonNull(accessor);
    }

    @Override
    public boolean isOpen() {
        var r = (DataAccessorV2) ACCESSOR.getAcquire(this);
        return r != null && r.isOpen();
    }

    @Override
    public void close() {
        ACCESSOR.setRelease(this, null);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        var accessor = (DataAccessorV2) ACCESSOR.getAcquire(this);
        if (accessor == null || !accessor.isOpen()) {
            throw new ClosedChannelException();
        }
        try {
            readLock.lock();
            var pos = (long) POSITION.getAcquire(this);
            if (pos >= size) {
                return -1;
            }
            if (dst.remaining() > size - pos) {
                dst = dst.slice(dst.position(), (int) (size - pos));
            }
            var totalRead = this.accessor.read(dst, Math.addExact(offset, pos));
            if (totalRead > 0) {
                POSITION.setRelease(this, pos + totalRead);
            }
            return totalRead;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int write(ByteBuffer src) {
        throw new NonWritableChannelException();
    }

    @Override
    public long position() {
        return (long) POSITION.getAcquire(this);
    }

    @Override
    public SeekableByteChannel position(long newPosition) {
        if (newPosition < 0 || newPosition > size) {
            throw new IllegalArgumentException("newPosition must be non-negative and less than size");
        }
        POSITION.setRelease(this, newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        throw new NonWritableChannelException();
    }
}
