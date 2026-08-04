package app.wordglass.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ReadTimeTest {

    @Test
    fun wordCount_emptyOrWhitespace_isZero() {
        assertEquals(0, ReadTime.wordCount(""))
        assertEquals(0, ReadTime.wordCount("   \t\n "))
    }

    @Test
    fun wordCount_countsNonEmptyTokens() {
        assertEquals(1, ReadTime.wordCount("hello"))
        assertEquals(3, ReadTime.wordCount("  leading   and   trailing  "))
        assertEquals(4, ReadTime.wordCount("a\tb\nc\r d"))
    }

    @Test
    fun wordCount_hyphenatedIsOneWord() {
        assertEquals(2, ReadTime.wordCount("well-known thing"))
    }

    @Test
    fun readTimeSeconds_roundsToNearest() {
        assertEquals(0, ReadTime.readTimeSeconds(0))
        assertEquals(41, ReadTime.readTimeSeconds(96))     // 96*60/140 = 41.14 -> 41
        assertEquals(1719, ReadTime.readTimeSeconds(4012)) // 4012*60/140 = 1719.43 -> 1719
    }

    @Test
    fun format_underAMinuteIsSeconds() {
        assertEquals("≈ 0 sec", ReadTime.format(0))
        assertEquals("≈ 41 sec", ReadTime.format(41))
    }

    @Test
    fun format_atOrOverAMinuteAlwaysKeepsSeconds() {
        assertEquals("≈ 1 min 0 sec", ReadTime.format(60))
        // The logged canvas defect: 4,012 words is 28 min 39 sec, not "40".
        assertEquals("≈ 28 min 39 sec", ReadTime.format(1719))
    }
}
