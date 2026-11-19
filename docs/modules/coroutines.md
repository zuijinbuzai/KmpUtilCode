# 协程任务：utilcode-coroutines

包 `io.github.zuijinbuzai.kmputilcode.coroutines`。所有任务继承调用者的 Scope，不创建全局后台任务。

## 取消友好的重试

```kotlin
val result = retry(
    maxAttempts = 3,
    initialDelay = 200.milliseconds,
    maxDelay = 5_000.milliseconds,
    shouldRetry = { it is TemporaryFailure },
) { attempt ->
    repository.load(attempt)
}
```

`attempt` 从 1 开始。maxAttempts 包含第一次执行；第 1、2 次失败之后分别等待 200、400ms，再按 2 倍递增至 maxDelay。最后一次失败直接抛出，无额外等待。默认重试所有非取消异常，生产代码应通过 shouldRetry 精确筛选。`CancellationException` 始终抛出，外部取消在执行前/等待中生效。没有随机抖动；高并发集群建议在业务层增加 jitter。

不要对非幂等支付、下单、写入等操作盲目重试。HTTP 返回的 `ApiResult.HttpFailure` 是返回值，不会自动触发 retry；需要业务显式判断并在可重试条件下抛业务异常。

## 有界并发映射

```kotlin
val results = ids.parallelMap(concurrency = 4) { id ->
    repository.load(id)
}
```

只创建至多 concurrency 个工作协程，输出顺序与输入一致。单个任务失败会取消其余任务并向上传播异常。输入与输出都会保存在内存，适用于有限集合；不是无限流处理器，也不自动切到 IO 线程。在异步网络调用中可直接使用；阻塞 I/O 应在 transform 内切合适的 Dispatcher。

## 定时冷流

```kotlin
tickerFlow(interval = 1_000.milliseconds, count = 3).collect { tick ->
    // 立即 0，然后每隔 1 秒输出 1、2
}
```

每次 collect 独立计时；count=null 无限执行，0 不输出，结束后没有多余 delay。它是“发出 → 下游处理 → delay”，不补偿耗时、不保证墙上时间对齐，也不是后台持久任务调度器。应在合适的生命周期中收集并取消。

## 串行任务门

```kotlin
val gate = TaskGate()
gate.run { repository.refresh() }
```

同一 gate 的任务排队执行，异常与取消会释放许可。不可重入：不要在自己的 run 内再次调用同一 gate。它不会去重或合并结果；排队的每个任务最终都会执行（除非被取消）。

Android 用 Dispatchers.Main 需 `kotlinx-coroutines-android`，本模块不强制它成为通用依赖。测试使用虚拟时间验证退避、取消、并发上限、定时流和 gate 释放。`./gradlew :utilcode-coroutines:jvmTest`；Demo id：`retry`、`parallel`。
