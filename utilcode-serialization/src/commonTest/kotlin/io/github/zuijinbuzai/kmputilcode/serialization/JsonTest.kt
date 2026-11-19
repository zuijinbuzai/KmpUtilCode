package io.github.zuijinbuzai.kmputilcode.serialization
import kotlinx.serialization.json.*
import kotlin.test.*
class JsonTest {
    private val codec=JsonCodec()
    @Test fun primitiveCollectionsAndUnknownFields() {
        val data=mapOf("a" to listOf(1,2),"b" to emptyList())
        assertEquals(data,codec.decode<Map<String,List<Int>>>(codec.encode(data)))
        assertEquals("{\"a\":1}",codec.parse("{\"a\":1}").toString())
        assertFails { codec.parse("{bad json}") }
    }
    @Test fun mergePatchHandlesDeletionAndReplacement() {
        val target=codec.parse("""{"a":{"b":1,"c":2},"list":[1,2]}""")
        val patch=codec.parse("""{"a":{"b":null,"d":3},"list":[9]}""")
        assertEquals(codec.parse("""{"a":{"c":2,"d":3},"list":[9]}"""),codec.mergePatch(target,patch))
        assertEquals(JsonNull,codec.mergePatch(target,JsonNull))
        assertEquals(codec.parse("""{"x":1}"""),codec.mergePatch(JsonPrimitive(7),codec.parse("""{"x":1}""")))
    }
}
