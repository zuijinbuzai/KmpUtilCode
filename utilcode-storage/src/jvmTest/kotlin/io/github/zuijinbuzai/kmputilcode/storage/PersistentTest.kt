package io.github.zuijinbuzai.kmputilcode.storage
import kotlin.test.*
import java.util.UUID
import java.util.prefs.Preferences
class PersistentTest {
    @Test fun persistsAndIsolatesNamespaces() {
        val name="test-${UUID.randomUUID()}"
        val other="$name-other"
        try {
            val a=persistentSettings(name); val b=persistentSettings(other)
            a.putLong("count",Long.MAX_VALUE); b.putString("name","other")
            assertEquals(Long.MAX_VALUE,persistentSettings(name).getLong("count"))
            a.clear(); assertEquals("other",b.getString("name"))
        } finally {
            for(n in listOf(name,other)) Preferences.userRoot().node("io/github/zuijinbuzai/kmputilcode/$n").removeNode()
        }
    }
}
