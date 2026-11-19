package io.github.zuijinbuzai.kmputilcode.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/** Cancellation always escapes. At most maxAttempts invocations, with capped exponential backoff. */
suspend fun <T> retry(
    maxAttempts: Int = 3,
    initialDelay: Duration = 200.milliseconds,
    maxDelay: Duration = 5_000.milliseconds,
    shouldRetry: (Throwable) -> Boolean = { true },
    block: suspend (attempt: Int) -> T,
): T {
    require(maxAttempts > 0 && initialDelay >= Duration.ZERO && maxDelay >= initialDelay && maxDelay.isFinite())
    var pause = initialDelay
    repeat(maxAttempts) { index ->
        currentCoroutineContext().ensureActive()
        try { return block(index + 1) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (failure: Throwable) {
            if (index == maxAttempts - 1 || !shouldRetry(failure)) throw failure
            delay(pause)
            pause = minOf(pause * 2, maxDelay)
        }
    }
    error("Unreachable")
}

/** Preserves input order; creates at most concurrency worker coroutines. */
suspend fun <T, R> Iterable<T>.parallelMap(concurrency: Int = 4, transform: suspend (T) -> R): List<R> = coroutineScope {
    require(concurrency > 0)
    val input = this@parallelMap.toList()
    val output = MutableList<R?>(input.size) { null }
    val queue = kotlinx.coroutines.channels.Channel<IndexedValue<T>>(concurrency)
    launch { try { input.forEachIndexed { index, value -> queue.send(IndexedValue(index, value)) } } finally { queue.close() } }
    List(minOf(concurrency, input.size)) {
        launch { for ((index, value) in queue) output[index] = transform(value) }
    }.joinAll()
    @Suppress("UNCHECKED_CAST")
    output as List<R>
}

/** Cold flow. Emits 0 immediately then through count-1 at interval; count=null is unbounded. */
fun tickerFlow(interval: Duration, count: Long? = null): Flow<Long> = flow {
    require(interval > Duration.ZERO && interval.isFinite() && (count == null || count >= 0))
    var tick = 0L
    while (count == null || tick < count) {
        currentCoroutineContext().ensureActive()
        emit(tick++)
        if (count == null || tick < count) delay(interval)
    }
}

/** Run at most one block concurrently. Ownership stays with the caller's coroutine. */
class TaskGate {
    private val semaphore = Semaphore(1)
    suspend fun <T> run(block: suspend () -> T): T = semaphore.withPermit { block() }
}
