package space.iseki.pefile;
/*
This file is used within a Multi-Release Jar file.
All modifications should be reflected in other source-roots.
 */

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

interface DataAccessorV2 extends AutoCloseable {
    int read(@NotNull ByteBuffer buffer, long position) throws IOException;

    boolean isOpen();
}

final class MmapHelper {
    public static boolean isSupported() {
        return false;
    }

    @SuppressWarnings({"unused", "RedundantThrows"})
    public static DataAccessorV2 accessorOf(FileChannel channel) throws IOException {
        return null;
    }
}

