package app.wordglass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.wordglass.ui.scripts.list.ScriptListScreen
import app.wordglass.ui.theme.WordglassTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Throwaway host. Shows `ScriptList` — the app's home — so the list can be exercised on device.
 * The FAB and row-tap are **stubs** until **Phase D** wires the real `ScriptList ↔ ScriptEditor`
 * navigation (a `NavHost`); the editor itself is built and unit/preview-covered. Both this host and
 * the stubs disappear in Phase D.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordglassTheme {
                ScriptListScreen(
                    onNewScript = { /* Phase D: navigate to a blank ScriptEditor */ },
                    onOpenScript = { /* Phase D: navigate to ScriptEditor for this id */ },
                )
            }
        }
    }
}
