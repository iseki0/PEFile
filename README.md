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

[Main.kt](example/src/main/kotlin/Main.kt)

```kotlin
fun main() {
  val notepad = Path.of(System.getenv("SystemRoot"), "System32", "notepad.exe")
  PEFile.open(notepad).use { peFile->
    // print COFF header
    println(peFile.coffHeader)

    // print import table
    for (entry in peFile.importTable) {
      println("DLL: ${entry.name}")
      for (symbol in entry.symbols()) {
        println("  $symbol")
      }
    }
    // print resource tree
    println("Resource Tree:")
    for (entry in ResourceWalker(peFile.resourceRoot!!)) {
      println("  ".repeat(entry.depth) + entry.node)
    }
  }
}
```
