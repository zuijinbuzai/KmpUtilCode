package io.github.zuijinbuzai.kmputilcode.demo
import kotlin.test.*
import kotlinx.coroutines.test.runTest
class DemoTest {
    @Test fun catalogHasUniqueCompleteEntries() {
        assertEquals(DemoCatalog.cases.size,DemoCatalog.cases.map {it.id}.distinct().size)
        assertTrue(DemoCatalog.cases.all {it.description.isNotBlank() && it.code.isNotBlank()})
    }
    @Test fun offlineSamplesExecute() = runTest {
        for(sample in DemoCatalog.cases.filter {it.id !in setOf("persistent","file","network")}) {
            assertTrue(DemoCatalog.run(sample.id).isNotBlank(),sample.id)
        }
    }
}
