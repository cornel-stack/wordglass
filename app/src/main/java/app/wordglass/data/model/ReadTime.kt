package app.wordglass.data.model

import kotlin.math.roundToInt

/**
 * Word count and read-time — the single source used by BOTH the editor count line and the list
 * row, so the two can never disagree (handoff addendum, rulings 1 & 2).
 */
object ReadTime {

    const val WORDS_PER_MINUTE = 140

    /**
     * Trim, split on Unicode whitespace, count non-empty tokens. Hyphenated words count as one
     * (no internal whitespace); empty / whitespace-only text is 0. Implemented as a scan rather
     * than a regex so `Character.isWhitespace` gives us correct Unicode behaviour for free.
     */
    fun wordCount(text: CharSequence): Int {
        var count = 0
        var inWord = false
        for (c in text) {
            if (Character.isWhitespace(c)) {
                inWord = false
            } else if (!inWord) {
                inWord = true
                count++
            }
        }
        return count
    }

    /** Seconds to read [words] aloud at [wpm], rounded to the NEAREST second (ruling 1). */
    fun readTimeSeconds(words: Int, wpm: Int = WORDS_PER_MINUTE): Int {
        if (words <= 0) return 0
        return (words * 60.0 / wpm).roundToInt()
    }

    /**
     * "≈ 47 sec" under a minute; "≈ 2 min 14 sec" at/over a minute — **always** with seconds,
     * never rounded to whole minutes (ruling 1). Verified: 4,012 words → 1719 s → "≈ 28 min 39
     * sec" (not the frame's "40"; that frame is a logged canvas defect).
     */
    fun format(seconds: Int): String =
        if (seconds < 60) "≈ $seconds sec"
        else "≈ ${seconds / 60} min ${seconds % 60} sec"

    /** Convenience: read-time string straight from text. */
    fun formatFor(text: CharSequence): String = format(readTimeSeconds(wordCount(text)))

    /**
     * Spoken read-time for content descriptions — "about 47 seconds", "about 2 minutes 14 seconds".
     * Whole words, no "≈" (which TalkBack reads as "almost equal to"). Singular/plural handled so it
     * never says "1 seconds". Used by the row's a11y label, "$spoken to read aloud" (§4.7).
     */
    fun spoken(seconds: Int): String {
        fun unit(n: Int, one: String) = "$n $one${if (n == 1) "" else "s"}"
        return if (seconds < 60) "about ${unit(seconds, "second")}"
        else "about ${unit(seconds / 60, "minute")} ${unit(seconds % 60, "second")}"
    }
}
