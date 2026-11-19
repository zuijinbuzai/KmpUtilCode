package io.github.zuijinbuzai.kmputilcode.time
import kotlinx.datetime.*
import kotlin.test.*
class TimeTest {
    @Test fun injectedClockAndZoneBoundary() {
        val time=TimeUtils { Instant.parse("2026-11-19T16:30:00Z") }
        assertEquals(LocalDate(2026,11,20),time.today(TimeZone.of("Asia/Shanghai")))
        assertEquals(Instant.parse("2026-11-19T16:00:00Z"),time.startOfToday(TimeZone.of("Asia/Shanghai")))
        assertEquals("2026-11-19",TimeUtils.date(time.now()))
    }
    @Test fun dstStartAndCalendarArithmetic() {
        val zone=TimeZone.of("America/New_York")
        val a=TimeUtils.startOfDay(LocalDate(2024,3,10),zone)
        val b=TimeUtils.startOfDay(LocalDate(2024,3,11),zone)
        assertEquals(23,(b-a).inWholeHours)
        assertEquals(1,TimeUtils.daysBetween(LocalDate(2024,3,10),LocalDate(2024,3,11)))
        assertEquals(LocalDate(2024,2,29),TimeUtils.plusDays(LocalDate(2024,2,28),1))
        assertTrue(TimeUtils.isLeapYear(2000)); assertFalse(TimeUtils.isLeapYear(1900))
        assertEquals(1,TimeUtils.weekday(LocalDate(2026,11,16)))
    }
}
