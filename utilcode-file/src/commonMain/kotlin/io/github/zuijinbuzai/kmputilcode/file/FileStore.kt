package io.github.zuijinbuzai.kmputilcode.file

import okio.*
import okio.Path.Companion.toPath

/** App-owned files confined to root. Relative traversal and symlinks are rejected. Blocking I/O. */
class FileStore(root: Path, private val fs: FileSystem = FileSystem.SYSTEM) {
    private val root: Path
    init { fs.createDirectories(root); this.root = fs.canonicalize(root) }
    private fun resolve(relative: String): Path {
        require(relative.isNotBlank() && '\u0000' !in relative && '\\' !in relative) { "Expected a relative path" }
        val path = relative.toPath()
        require(!path.isAbsolute && path.volumeLetter == null && ':' !in relative && path.segments.none { it == ".." }) { "Unsafe relative path" }
        var current = root
        for (part in path.segments) {
            require(part != "." && part.isNotEmpty())
            current /= part
            require(fs.metadataOrNull(current)?.symlinkTarget == null) { "Symlinks are not allowed" }
        }
        require(current != root) { "Root itself is not a file entry" }
        return current
    }
    fun exists(path: String): Boolean = fs.exists(resolve(path))
    fun size(path: String): Long = fs.metadata(resolve(path)).size ?: 0L
    fun readBytes(path: String, maxBytes: Long = 16L * 1024 * 1024): ByteArray {
        require(maxBytes in 0 until Int.MAX_VALUE.toLong())
        return fs.source(resolve(path)).buffer().use { source ->
            val buffer = Buffer()
            var remaining = maxBytes + 1
            while (remaining > 0) {
                val count = source.read(buffer, minOf(8192L, remaining))
                if (count == -1L) break
                remaining -= count
            }
            require(buffer.size <= maxBytes) { "File exceeds maxBytes=$maxBytes" }
            buffer.readByteArray()
        }
    }
    fun readText(path: String, maxBytes: Long = 16L * 1024 * 1024): String = readBytes(path, maxBytes).decodeToString(throwOnInvalidSequence = true)
    fun writeBytes(path: String, value: ByteArray) {
        val target = resolve(path)
        target.parent?.let { fs.createDirectories(it) }
        fs.sink(target).buffer().use { it.write(value) }
    }
    fun writeText(path: String, value: String) = writeBytes(path, value.encodeToByteArray())
    fun appendText(path: String, value: String) {
        val target = resolve(path)
        target.parent?.let { fs.createDirectories(it) }
        fs.appendingSink(target).buffer().use { it.writeUtf8(value) }
    }
    fun list(directory: String? = null): List<String> = fs.list(directory?.let(::resolve) ?: root)
        .filter { fs.metadataOrNull(it)?.symlinkTarget == null }.map { it.relativeTo(root).toString() }.sorted()
    /** Deletes a single file or empty directory. Recursive deletion is intentionally explicit in callers. */
    fun delete(path: String, mustExist: Boolean = false) = fs.delete(resolve(path), mustExist)
    fun copy(source: String, destination: String) {
        val target = resolve(destination)
        target.parent?.let { fs.createDirectories(it) }
        fs.copy(resolve(source), target)
    }
}
