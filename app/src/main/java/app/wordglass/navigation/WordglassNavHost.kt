package app.wordglass.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.wordglass.ui.scripts.editor.ScriptEditorScreen
import app.wordglass.ui.scripts.list.ScriptListScreen

private const val ROUTE_SCRIPT_LIST = "script_list"
private const val ROUTE_SCRIPT_EDITOR = "script_editor"
private const val ARG_SCRIPT_ID = "scriptId"

// "script_editor?scriptId={scriptId}" — scriptId is optional (null = new blank editor)
private val ROUTE_SCRIPT_EDITOR_FULL = "$ROUTE_SCRIPT_EDITOR?$ARG_SCRIPT_ID={$ARG_SCRIPT_ID}"

/**
 * The app's navigation graph (§8 state-transition table, slice 01).
 *
 * Two slice-01 destinations:
 * - `script_list` — root; start destination. FAB and row tap navigate to `script_editor`.
 * - `script_editor?scriptId={id}` — new blank editor when `scriptId` is absent; opens the
 *   existing script with that id when present (§8 rows 1, 3, 4).
 *
 * `ScriptStart` (§8 rows 5, 17) has no route here — it activates in slice 06 per DEFERRED.md.
 *
 * **Component gallery** — registered by [registerGalleryRoute], which is a no-op in release
 * (main source set stub) and registers the real route in debug (src/debug override). The
 * triple-tap open gesture in [ScriptListScreen] is wired via [galleryOpenCallback], which
 * returns null in release so no gesture is added.
 */
@Composable
fun WordglassNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_SCRIPT_LIST,
        modifier = modifier,
    ) {
        composable(ROUTE_SCRIPT_LIST) {
            ScriptListScreen(
                onNewScript = {
                    // §8 row 4: + → ScriptEditor · new (sheet bypassed until slice 06)
                    navController.navigate(ROUTE_SCRIPT_EDITOR)
                },
                onOpenScript = { id ->
                    // §8 row 3: row tap → ScriptEditor · editing
                    navController.navigate("$ROUTE_SCRIPT_EDITOR?$ARG_SCRIPT_ID=$id")
                },
                onOpenGallery = galleryOpenCallback(navController),
            )
        }
        registerGalleryRoute(navController)
        composable(
            route = ROUTE_SCRIPT_EDITOR_FULL,
            arguments = listOf(
                navArgument(ARG_SCRIPT_ID) {
                    nullable = true
                    defaultValue = null
                    type = NavType.StringType
                },
            ),
        ) {
            ScriptEditorScreen(
                // §8 rows 11, 13: back or Scripts → ScriptList (saved, no confirmation)
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
