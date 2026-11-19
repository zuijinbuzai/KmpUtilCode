package io.github.zuijinbuzai.kmputilcode.core
import kotlin.test.*
class CoreTest {
    @Test fun textBoundaries() {
        assertEquals("138****5678", Text.mask("13812345678"))
        assertFailsWith<IllegalArgumentException> { Text.mask("12") }
        assertEquals("", Text.truncate("abcdef", 0))
        assertEquals("…", Text.truncate("abc", 1))
        assertEquals("http_server_port", Text.snakeCase("HTTPServerPort"))
        assertEquals("helloWorld", Text.camelCase("hello-world"))
        assertEquals("one_two", Text.snakeCase("one two"))
    }
    @Test fun validationRejectsMalformedInputs() {
        assertTrue(Validation.email("hello@example.com"))
        assertFalse(Validation.email("a..b@example.com"))
        assertFalse(Validation.email("a@-example.com"))
        assertTrue(Validation.ipv4("192.168.0.1"))
        listOf("256.1.1.1", "01.2.3.4", "1.2.3", "-1.2.3.4").forEach { assertFalse(Validation.ipv4(it)) }
        assertTrue(Validation.mainlandMobile("13812345678"))
        assertFalse(Validation.mainlandMobile("12812345678"))
        assertTrue(Validation.httpUrl("https://example.com:443/a?b=1"))
        assertFalse(Validation.httpUrl("https://example.com:99999"))
        assertFalse(Validation.httpUrl("file:///etc/passwd"))
        assertTrue(Validation.luhn("79927398713"))
        assertFalse(Validation.luhn("79927398714"))
        assertFalse(Validation.luhn(""))
        assertTrue(Validation.password("Abcd123!"))
        assertFalse(Validation.password("abcdefgh"))
    }
    @Test fun collectionPaginationAndOverflow() {
        assertEquals(listOf(4,5,6), (1..8).toList().page(1,3))
        assertEquals(listOf(7,8), (1..8).toList().page(2,3))
        assertEquals(emptyList(), listOf(1).page(Int.MAX_VALUE,Int.MAX_VALUE))
        assertFailsWith<IllegalArgumentException> { listOf(1).page(0,0) }
        assertEquals(mapOf("a" to 2, "b" to 1), listOf("a","b","a").frequencies())
        assertEquals(listOf(1,1), listOf(1,2,1).duplicatesBy { it })
        assertEquals(listOf(2,3,1), listOf(1,2,3).move(0,2))
    }
    @Test fun lruAccessAndReplacement() {
        val cache = LruCache<String,String>(2)
        cache.put("a","A"); cache.put("b","B")
        assertEquals("A",cache["a"])
        cache.put("c","C")
        assertNull(cache["b"])
        assertEquals("A",cache.put("a","AA"))
        assertEquals(2,cache.size)
        cache.clear(); assertEquals(0,cache.size)
    }
    @Test fun numbersAndLazyLog() {
        assertEquals("0 B",Numbers.formatBytes(0))
        assertEquals("1.5 KiB",Numbers.formatBytes(1536))
        assertEquals(6,Numbers.gcd(-54,24))
        assertFailsWith<IllegalArgumentException> { Numbers.gcd(Long.MIN_VALUE,1) }
        assertEquals(3.142,Numbers.round(3.14159,3))
        val entries = mutableListOf<LogEntry>()
        val logger = Logger("test",sink=LogSink { entries += it })
        logger.debug { error("Should not be evaluated") }
        logger.info { "hello" }
        assertEquals("hello",entries.single().message)
    }
}
