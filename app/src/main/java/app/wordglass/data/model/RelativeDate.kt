package app.wordglass.data.model

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Renders a script's `updatedAt` as a relative date for the list row (handoff addendum ruling
 * 3). `now`, `zone` and `locale` are parameters so the function is pure and testable; the UI
 * passes a single `now` so every row on screen agrees.
 *
 * Buckets, in order:
 *   < 1 min           → "Just now"        [NEW]
 *   < 1 hr            → "Nm ago"          [NEW]
 *   same calendar day → "Nh ago"
 *   previous day      → "Yesterday"
 *   2–6 days          → weekday abbrev    ("Mon")
 *   older, same year  → "28 Jul"
 *   different year    → "28 Jul 2025"     [NEW]
 *
 * java.time is native on minSdk 26 — no desugaring needed.
 */
object RelativeDate {

    fun format(
        updatedAtMillis: Long,
        now: Instant = Instant.now(),
        zone: ZoneId = ZoneId.systemDefault(),
        locale: Locale = Locale.getDefault(),
    ): String {
        val then = Instant.ofEpochMilli(updatedAtMillis)
        val elapsed = Duration.between(then, now)

        val minutes = elapsed.toMinutes()
        if (elapsed.isNegative || minutes < 1) return "Just now"
        if (minutes < 60) return "${minutes}m ago"

        val thenDate = then.atZone(zone).toLocalDate()
        val nowDate = now.atZone(zone).toLocalDate()
        if (thenDate == nowDate) return "${elapsed.toHours()}h ago"

        val days = ChronoUnit.DAYS.between(thenDate, nowDate)
        if (days == 1L) return "Yesterday"
        if (days < 7) return thenDate.dayOfWeek.getDisplayName(TextStyle.SHORT, locale)

        val pattern = if (thenDate.year == nowDate.year) "d MMM" else "d MMM yyyy"
        return thenDate.format(DateTimeFormatter.ofPattern(pattern, locale))
    }
}
