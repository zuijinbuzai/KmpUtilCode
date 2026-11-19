package io.github.zuijinbuzai.kmputilcode.platform
import kotlin.test.*
class PlatformTest {
    @Test fun uuidAndRuntimeInfo() {
        val a=randomUuid(); val b=randomUuid()
        assertNotEquals(a,b)
        assertTrue(Regex("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}").matches(a))
        assertTrue(platformInfo().name.isNotBlank())
    }
}
