package io.github.zuijinbuzai.kmputilcode.storage
import kotlin.test.*
class SettingsTest {
    private fun checkContract(s: Settings) {
        assertEquals("fallback",s.getString("missing","fallback"))
        assertFalse(s.contains("missing"))
        s.putString("name","你好"); assertEquals("你好",s.getString("name"))
        s.putLong("count",Long.MAX_VALUE); assertEquals(Long.MAX_VALUE,s.getLong("count"))
        s.putBoolean("enabled",true); assertTrue(s.getBoolean("enabled"))
        assertEquals(42,s.getLong("name",42))
        s.putString("name","b:true"); assertFalse(s.getBoolean("name"))
        s.remove("name"); assertFalse(s.contains("name"))
        s.clear(); assertFalse(s.contains("count")); assertFalse(s.contains("enabled"))
    }
    @Test fun memoryContract() = checkContract(MemorySettings())
    @Test fun encodedContract() {
        val map=mutableMapOf<String,String>()
        checkContract(object:StringSettings() {
            override fun read(key:String)=map[key]
            override fun write(key:String,value:String) {map[key]=value}
            override fun remove(key:String) {map.remove(key)}
            override fun clear()=map.clear()
        })
    }
    @Test fun namespaceValidation() {
        assertFailsWith<IllegalArgumentException> { persistentSettings("../other") }
        assertFailsWith<IllegalArgumentException> { persistentSettings("") }
    }
}
