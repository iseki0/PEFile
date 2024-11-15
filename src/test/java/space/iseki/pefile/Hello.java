package space.iseki.pefile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class Hello {

    @Test
    public void hello() {
        System.out.println("Hello, World!");
        System.out.println("mmap: " + MmapHelper.isSupported());
    }

    @Test
    public void testNotepad() throws Exception {
        Assumptions.assumeTrue(System.getProperty("os.name").toLowerCase().contains("win"));
        try (var pe = PEFile.open(Path.of(System.getenv("SystemRoot"), "System32/notepad.exe"))) {
//        try (var pe = PEFile.open(Path.of("C:\\Program Files\\NetEase\\CloudMusic\\cloudmusic.exe"))) {
            System.out.println(pe.getCoffHeader());
            System.out.println(pe.getStandardHeader());
            System.out.println(pe.getOptionalHeader());
            System.out.println(pe.getResourceRoot());
            Assertions.assertTrue(pe.getImportTable().iterator().hasNext());
            for (var entry : pe.getImportTable()) {
                System.out.println(entry);
                Assertions.assertTrue(entry.symbols().iterator().hasNext());
                for (var symbol : entry.symbols()) {
                    System.out.println("  " + symbol);
                }
            }
            for (ResourceWalker.Entry entry : new ResourceWalker(pe.getResourceRoot())) {
                System.out.println("  ".repeat(entry.getDepth()) + entry.getNode());
            }
        } catch (NoSuchFileException ignored) {
            Assumptions.abort();
        } catch (Exception e) {
            throw e;
        }
    }


}

