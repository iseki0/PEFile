# PEFile

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/iseki0/PEFile/build.yml)
[![Maven Central Version](https://img.shields.io/maven-central/v/space.iseki.pefile/pefile)](https://central.sonatype.com/artifact/space.iseki.pefile/pefile)
![License](https://img.shields.io/github/license/iseki0/PEFile)

Yet another library for Windows Portable Executable (PE) files

## Requirements
 
- Java 17 or later
  - For Java >= 22, we read the PE file by MMAP

## Installation

For `build.gradle.kts`:
```kotlin
dependencies {
    implementation("space.iseki.pefile:pefile:0.+")
}
```

For `build.gradle`:
```groovy
dependencies {
    implementation 'space.iseki.pefile:pefile:0.+'
}
```

For `pom.xml`:
```xml
<dependency>
  <groupId>space.iseki.pefile</groupId>
  <artifactId>pefile</artifactId>
  <version>0.+</version>
</dependency>
```

## Usage

### Kotlin

```kotlin
import space.iseki.pefile.PEFile
import java.nio.file.Path

fun main() {
    val notepad = Path.of("C:\\Windows\\System32\\notepad.exe")
    val pe = PEFile.of(notepad)
    println(pe.coffHeader) // print COFF header
    for (entry in pe.importTable) { // print import table
        println(entry)
        assert(entry.symbols().iterator().hasNext())
        for (symbol in entry.symbols()) { // print imported symbols
            println("  $symbol")
        }
    }
    // print resource tree
    for (entry in ResourceWalker(pe.resourceRoot)) {
        println("  ".repeat(entry.depth) + entry.node)
    }
}
```

### Java

```java
import space.iseki.pefile.PEFile;
import java.io.Exception;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            var notepad = Path.of("C:\\Windows\\System32\\notepad.exe");
            PEFile pe = PEFile.of(Path.of(notepad));
            System.out.println(pe.getCoffHeader()); // print COFF header
            for (var entry : pe.getImportTable()) { // print import table
                System.out.println(entry);
                Assertions.assertTrue(entry.symbols().iterator().hasNext());
                for (var symbol : entry.symbols()) { // print imported symbols
                    System.out.println("  " + symbol);
                }
            }
            // print resource tree
            for (ResourceWalker.Entry entry : new ResourceWalker(pe.getResourceRoot())) {
                System.out.println("  ".repeat(entry.getDepth()) + entry.getNode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```
