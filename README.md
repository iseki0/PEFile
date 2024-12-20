# PEFile

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/iseki0/PEFile/build.yml)](https://github.com/iseki0/PEFile/actions/workflows/build.yml)
[![Maven Central Version](https://img.shields.io/maven-central/v/space.iseki.pefile/pefile)](https://central.sonatype.com/artifact/space.iseki.pefile/pefile)
![License](https://img.shields.io/github/license/iseki0/PEFile)

Yet another library for Windows Portable Executable (PE) files

## Features

Reading the following data from PE file:
 - COFF Header
 - Optional Header(both of standard & Windows-specific fields)
 - Section Lists
 - Import Table(DLL names and symbols)
 - Resource Tree

## Requirements
 
- Java 17 or later
  - For Java >= 22, we read the PE file by MMAP

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
