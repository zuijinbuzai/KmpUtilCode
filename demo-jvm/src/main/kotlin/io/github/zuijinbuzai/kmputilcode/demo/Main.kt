package io.github.zuijinbuzai.kmputilcode.demo

import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.util.Comparator

/** Offline by default. Opt into the public HTTP example with --network. */
fun main(args: Array<String>) = runBlocking {
    val root = Files.createTempDirectory("kmputilcode-demo-")
    try {
        for (sample in DemoCatalog.cases) {
            if (sample.id == "network" && "--network" !in args) continue
            println("\n[${sample.category}] ${sample.title}")
            println(sample.description)
            println(DemoCatalog.run(sample.id, fileRoot = root.toString()))
        }
        println("\nKmpUtilCode demo completed.")
    } finally {
        Files.walk(root).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach { Files.delete(it) } }
    }
}
