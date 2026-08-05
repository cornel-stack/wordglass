package app.wordglass.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ScriptDisplayTest {

    @Test
    fun title_nonBlankIsUnchanged() {
        assertEquals("My script", ScriptDisplay.title("My script"))
    }

    @Test
    fun title_emptyOrBlankBecomesUntitled() {
        assertEquals("Untitled", ScriptDisplay.title(""))
        assertEquals("Untitled", ScriptDisplay.title("   "))
        assertEquals("Untitled", ScriptDisplay.title("\t\n "))
    }
}
