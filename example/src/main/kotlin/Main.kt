package example

import space.iseki.pefile.PEFile
import space.iseki.pefile.walk
import java.nio.file.Path

fun main() {
    val notepad = Path.of(System.getenv("SystemRoot"), "System32", "notepad.exe")
    PEFile.open(notepad).use { peFile ->
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
        peFile.resourceRoot?.walk()?.forEach {
            println("  ".repeat(it.depth) + it.node)
        }
    }
}
