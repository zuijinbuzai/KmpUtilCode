package io.github.zuijinbuzai.kmputilcode.time

import kotlinx.datetime.*
import kotlin.time.Duration

/** Inject a clock to make application logic deterministic in tests. */
fun interface TimeSource { fun now(): Instant }
object SystemTimeSource : TimeSource { override fun now(): Instant = Clock.System.now() }
class TimeUtils(private val clock: TimeSource = SystemTimeSource) {
    fun now(): Instant = clock.now()
    fun today(zone: TimeZone = TimeZone.currentSystemDefault()): LocalDate = now().toLocalDateTime(zone).date
    fun startOfToday(zone: TimeZone = TimeZone.currentSystemDefault()): Instant = today(zone).atStartOfDayIn(zone)
    fun elapsedSince(instant: Instant): Duration = now() - instant
    companion object {
        fun parseIso(value: String): Instant = Instant.parse(value)
        fun fromEpochMillis(value: Long): Instant = Instant.fromEpochMilliseconds(value)
        fun iso(instant: Instant): String = instant.toString()
        fun date(instant: Instant, zone: TimeZone = TimeZone.UTC): String = instant.toLocalDateTime(zone).date.toString()
        fun startOfDay(date: LocalDate, zone: TimeZone): Instant = date.atStartOfDayIn(zone)
        fun daysBetween(start: LocalDate, end: LocalDate): Int = start.daysUntil(end)
        fun plusDays(date: LocalDate, days: Int): LocalDate = date.plus(days, DateTimeUnit.DAY)
        fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        /** ISO weekday: Monday=1, Sunday=7. */
        fun weekday(date: LocalDate): Int = date.dayOfWeek.isoDayNumber
    }
}
