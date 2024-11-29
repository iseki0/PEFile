package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class SeekableByteChannelDataAccessorV2 implements DataAccessorV2 {
    private final SeekableByteChannel channel;
    private final Lock lock = new ReentrantLock();

    SeekableByteChannelDataAccessorV2(@NotNull SeekableByteChannel channel) {
        this.channel = Objects.requireNonNull(channel);
    }

    @Override
    public int read(@NotNull ByteBuffer buffer, long position) throws IOException {
        try {
            lock.lock();
            channel.position(position);
            return channel.read(buffer);
        } catch (ClosedChannelException e) {
            throw new IllegalStateException("The file was closed", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        if (!channel.isOpen()) return;
        try {
            lock.lock();
            channel.close();
        } catch (ClosedChannelException ignored) {
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }
}

