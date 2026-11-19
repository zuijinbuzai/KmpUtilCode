# 时间日期：utilcode-time

包 `io.github.zuijinbuzai.kmputilcode.time`；公开类型来自 `kotlinx-datetime 0.6.2` 的 `Instant`、`LocalDate`、`TimeZone`。当前没有使用 Kotlin 2.2 标准库的 Instant 类型，请避免混用两套 import。

```kotlin
import kotlinx.datetime.*
import io.github.zuijinbuzai.kmputilcode.time.*

val time = TimeUtils()
val instant = time.now()
val today = time.today(TimeZone.of("Asia/Shanghai"))
val start = time.startOfToday(TimeZone.of("Asia/Shanghai"))
val elapsed = time.elapsedSince(instant)
TimeUtils.parseIso("2026-11-19T16:30:00Z")
TimeUtils.fromEpochMillis(0)                         // 1970-01-01T00:00:00Z
TimeUtils.iso(instant)                               // UTC ISO 8601
TimeUtils.date(instant, TimeZone.UTC)                 // yyyy-MM-dd
TimeUtils.plusDays(LocalDate(2024, 2, 28), 1)          // 2024-02-29
TimeUtils.daysBetween(LocalDate(2026, 12, 31), LocalDate(2027, 1, 1)) // 1
TimeUtils.isLeapYear(2000)                            // true
TimeUtils.weekday(LocalDate(2026, 11, 16))             // 1，周一
```

实例的 `today/startOfToday` 默认使用系统时区；静态 `date` 默认 UTC。需要确定性结果时显式传时区。日期加一天是日历运算，不等于给 Instant 加 24 小时；夏令时地区的一天可能只有 23 小时或长达 25 小时。`startOfDay` 使用时区规则返回当地一天的实际起点。

## 注入时钟

```kotlin
val fixed = TimeUtils { Instant.parse("2026-11-19T16:30:00Z") }
fixed.today(TimeZone.of("Asia/Shanghai")) // 2026-11-20
fixed.startOfToday(TimeZone.of("Asia/Shanghai")) // 2026-11-19T16:00:00Z
```

`elapsedSince` 用墙上时间，不保证单调；性能耗时请使用 Kotlin `TimeSource.Monotonic`。解析失败、非法日期或非法时区遵循 kotlinx-datetime 的异常行为。未封装区域化星期名、任意格式字符串和人性化相对日期。

测试覆盖上海跨日、纽约 DST、跨年、闰日。命令：`./gradlew :utilcode-time:jvmTest`；Demo id：`time`。
