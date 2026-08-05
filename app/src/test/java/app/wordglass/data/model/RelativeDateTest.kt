package app.wordglass.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

class RelativeDateTest {

    private val now: Instant = Instant.parse("2026-08-04T12:00:00Z")
    private val zone = ZoneOffset.UTC
    private val en = Locale.ENGLISH

    private fun format(then: Instant) = RelativeDate.format(then.toEpochMilli(), now, zone, en)

    @Test
    fun underOneMinute_isJustNow() {
        assertEquals("Just now", format(now.minusSeconds(30)))
    }

    @Test
    fun underOneHour_isMinutesAgo() {
        assertEquals("5m ago", format(now.minus(Duration.ofMinutes(5))))
    }

    @Test
    fun sameCalendarDay_isHoursAgo() {
        assertEquals("2h ago", format(now.minus(Duration.ofHours(2))))
    }

    @Test
    fun previousDay_isYesterday() {
        assertEquals("Yesterday", format(now.minus(Duration.ofDays(1))))
    }

    @Test
    fun withinAWeek_isWeekday() {
        val threeDaysAgo = now.minus(Duration.ofDays(3)) // 2026-08-01
        val expected = LocalDate.of(2026, 8, 1).dayOfWeek.getDisplayName(TextStyle.SHORT, en)
        assertEquals(expected, format(threeDaysAgo))
    }

    @Test
    fun olderSameYear_isDayMonth() {
        assertEquals("5 Jul", format(now.minus(Duration.ofDays(30)))) // 2026-07-05
    }

    @Test
    fun differentYear_includesYear() {
        assertEquals("28 Jul 2025", format(Instant.parse("2025-07-28T12:00:00Z")))
    }
}
