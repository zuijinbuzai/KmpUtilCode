package io.github.zuijinbuzai.kmputilcode.coroutines
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.*
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds
@OptIn(ExperimentalCoroutinesApi::class)
class AsyncTest {
    @Test fun retryBackoffAndExhaustion() = runTest {
        val attempts=mutableListOf<Int>()
        val result=retry(initialDelay=100.milliseconds) { n -> attempts+=n; if(n<3) error("temporary"); "ok" }
        assertEquals("ok",result); assertEquals(listOf(1,2,3),attempts); assertEquals(300,testScheduler.currentTime)
        var count=0
        assertFailsWith<IllegalStateException> { retry(maxAttempts=2) { count++; error("fail") } }
        assertEquals(2,count)
    }
    @Test fun cancellationIsNeverRetried() = runTest {
        var count=0
        assertFailsWith<CancellationException> { retry { count++; throw CancellationException("stop") } }
        assertEquals(1,count)
        val job=launch { retry { delay(1000); "never" } }
        runCurrent(); job.cancelAndJoin()
        assertTrue(job.isCancelled)
    }
    @Test fun parallelBoundAndStableOrder() = runTest {
        var active=0; var peak=0
        val out=(1..20).parallelMap(3) { value -> active++; peak=maxOf(peak,active); delay((21-value).toLong()); active--; value*2 }
        assertEquals((1..20).map { it*2 },out); assertEquals(3,peak)
        assertEquals(emptyList(),emptyList<Int>().parallelMap { it })
        assertEquals(listOf<String?>(null,"b"),listOf(0,1).parallelMap { if(it==0) null else "b" })
    }
    @Test fun tickerHasNoFinalDelay() = runTest {
        assertEquals(listOf(0L,1L,2L),tickerFlow(100.milliseconds,3).toList())
        assertEquals(200,testScheduler.currentTime)
        assertEquals(emptyList(),tickerFlow(10.milliseconds,0).toList())
    }
    @Test fun taskGateSerializesAndReleasesAfterFailure() = runTest {
        val gate=TaskGate(); val events=mutableListOf<Int>()
        coroutineScope { repeat(3) { i -> launch { gate.run { events+=i; delay(10); events+=i } } } }
        assertEquals(listOf(0,0,1,1,2,2),events)
        assertFailsWith<IllegalStateException> { gate.run { error("fail") } }
        assertEquals("ok",gate.run { "ok" })
    }
}
