package io.github.zuijinbuzai.kmputilcode.file
import okio.FileSystem
import okio.Path.Companion.toPath
import java.nio.file.Files
import java.util.Comparator
import kotlin.test.*
class FileStoreTest {
    private fun withStore(block:(FileStore,java.nio.file.Path)->Unit) {
        val root=Files.createTempDirectory("utilcode-test-")
        try { block(FileStore(root.toString().toPath()),root) }
        finally { Files.walk(root).use { it.sorted(Comparator.reverseOrder()).forEach { p -> Files.delete(p) } } }
    }
    @Test fun roundTripAndBoundedRead() = withStore { f,_ ->
        f.writeText("notes/a.txt","你好"); f.appendText("notes/a.txt"," KMP")
        assertEquals("你好 KMP",f.readText("notes/a.txt")); assertEquals(10,f.size("notes/a.txt"))
        assertEquals(listOf("notes/a.txt"),f.list("notes"))
        assertEquals(10,f.readBytes("notes/a.txt",10).size)
        assertFailsWith<IllegalArgumentException> {f.readBytes("notes/a.txt",9)}
        f.writeText("empty",""); assertContentEquals(byteArrayOf(),f.readBytes("empty",0))
        f.copy("notes/a.txt","copy.txt"); assertTrue(f.exists("copy.txt"))
        f.delete("copy.txt"); assertFalse(f.exists("copy.txt"))
    }
    @Test fun rejectsTraversalAndSymlinks() = withStore { f,root ->
        listOf("../escape","/etc/passwd","x/../../escape","x\\y", ".", "x/..", "C:/escape").forEach { path ->
            assertFailsWith<IllegalArgumentException>(path) {f.writeText(path,"no")}
        }
        val outside=Files.createTempDirectory("outside-")
        try {
            Files.createSymbolicLink(root.resolve("link"),outside)
            assertFailsWith<IllegalArgumentException> {f.writeText("link/escape","no")}
            assertFalse(Files.exists(outside.resolve("escape")))
        } finally {Files.delete(outside)}
    }
}
