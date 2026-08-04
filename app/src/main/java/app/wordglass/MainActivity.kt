package app.wordglass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.wordglass.ui.scripts.editor.ScriptEditorScreen
import app.wordglass.ui.theme.WordglassTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Phase A throwaway host: opens the editor directly for a new script so the editor's stateful
 * core can be exercised (write → autosave → process-death restore) before the list and navigation
 * exist. Navigation (ScriptList ↔ ScriptEditor) replaces this in Phase D.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordglassTheme {
                ScriptEditorScreen(onNavigateBack = { finish() })
            }
        }
    }
}
