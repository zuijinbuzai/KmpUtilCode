package io.github.zuijinbuzai.kmputilcode.core

/** A page uses a zero-based index; requesting past the end returns an empty list. */
fun <T> List<T>.page(index: Int, size: Int): List<T> {
    require(index >= 0 && size > 0)
    val start = index.toLong() * size
    if (start >= this.size) return emptyList()
    return subList(start.toInt(), minOf(start + size, this.size.toLong()).toInt()).toList()
}
fun <T> Iterable<T>.frequencies(): Map<T, Int> = groupingBy { it }.eachCount()
fun <T, K> Iterable<T>.duplicatesBy(selector: (T) -> K): List<T> {
    val items = toList()
    val counts = items.groupingBy(selector).eachCount()
    return items.filter { counts.getValue(selector(it)) > 1 }
}
fun <T> List<T>.move(from: Int, to: Int): List<T> = toMutableList().apply {
    require(from in indices && to in indices)
    add(to, removeAt(from))
}

/** Capacity is counted in entries. Not thread safe: confine to one thread or guard with a Mutex. */
class LruCache<K, V : Any>(val capacity: Int) {
    init { require(capacity > 0) }
    private val entries = LinkedHashMap<K, V>()
    val size: Int get() = entries.size
    operator fun get(key: K): V? = entries.remove(key)?.also { entries[key] = it }
    fun put(key: K, value: V): V? {
        val previous = entries.remove(key)
        entries[key] = value
        if (entries.size > capacity) entries.remove(entries.keys.first())
        return previous
    }
    fun remove(key: K): V? = entries.remove(key)
    fun clear() = entries.clear()
    fun snapshot(): Map<K, V> = entries.toMap()
}
