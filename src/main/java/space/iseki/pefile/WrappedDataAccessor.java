package space.iseki.pefile;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

/**
 * Just for historical reason, provide some simple methods
 */
final class WrappedDataAccessor implements AutoCloseable {
    private final DataAccessorV2 dataAccessor;

    WrappedDataAccessor(DataAccessorV2 dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    public static WrappedDataAccessor of(Path path) throws IOException {
        var ch = FileChannel.open(path, StandardOpenOption.READ);
        try {
            return Optional.ofNullable(MmapHelper.accessorOf(ch))
                           .or(() -> Optional.of(new SeekableByteChannelDataAccessorV2(ch)))
                           .map(WrappedDataAccessor::new)
                           .get();
        } catch (Throwable th) {
            try {
                ch.close();
            } catch (Throwable t) {
                th.addSuppressed(t);
            }
            throw th;
        }
    }

    public static WrappedDataAccessor of(SeekableByteChannel channel) throws IOException {
        return new WrappedDataAccessor(new SeekableByteChannelDataAccessorV2(channel));
    }

    public void readFully(long pos, byte[] buf, int off, int len) throws IOException {
        var n = readAtMost(pos, buf, off, len);
        if (n != len) {
            throw new EOFException("Expected " + len + " bytes, but only read " + n + " bytes");
        }
    }

    public void readFully(long pos, byte[] buf) throws IOException {
        readFully(pos, buf, 0, buf.length);
    }

    public int readAtMost(long pos, byte[] buf, int off, int len) throws IOException {
        var buffer = java.nio.ByteBuffer.wrap(buf, off, len);
        return dataAccessor.read(buffer, pos);
    }

    public int readAtMost(long pos, byte[] buf) throws IOException {
        return readAtMost(pos, buf, 0, buf.length);
    }

    public boolean isOpen() {
        return dataAccessor.isOpen();
    }

    @Override
    public void close() throws Exception {
        dataAccessor.close();
    }
}
