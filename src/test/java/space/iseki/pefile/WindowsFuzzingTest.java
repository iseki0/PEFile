package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class WindowsFuzzingTest {
    @TestFactory
    @Execution(ExecutionMode.CONCURRENT)
    public Stream<DynamicTest> windowsFuzzingTest() {
        Assumptions.assumeTrue(System.getProperty("os.name").toLowerCase().contains("win"));

        var root = Path.of(System.getenv("SystemRoot")).resolve("System32").toFile();
        File[] files = Optional.ofNullable(root.listFiles())
                               .stream()
                               .flatMap(Arrays::stream)
                               .filter(i -> i.getName().toLowerCase().endsWith(".exe") ||
                                            i.getName().toLowerCase().endsWith(".dll"))
                               .toArray(File[]::new);
        if (files.length > 1000) {
            // random keep 1000 items
            Collections.shuffle(Arrays.asList(files));
            files = Arrays.copyOf(files, 1000);
        }
        Objects.requireNonNull(files);
        return Arrays.stream(files).map(i -> DynamicTest.dynamicTest(i.getName(), () -> {
            try {
                try (@NotNull PEFile peFile = PEFile.open(i.toPath())) {
                    Assertions.assertNotNull(peFile.getCoffHeader());
                    System.out.println(i.getName() + " " + peFile.getCoffHeader());
                    for (ImportEntry importEntry : peFile.getImportTable()) {
                        Assertions.assertNotNull(importEntry.getName());
                        for (ImportSymbol symbol : importEntry.symbols()) {
                            Assertions.assertNotNull(symbol.getName());
                            Assertions.assertTrue((!symbol.getName().isBlank()) || symbol.getOrdinal() != 0);
                        }
                    }
                    var r = peFile.getResourceRoot();
                    if (r != null) {
                        //noinspection StatementWithEmptyBody
                        for (ResourceWalker.Entry entry : new ResourceWalker(r)) {
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

    }

}
