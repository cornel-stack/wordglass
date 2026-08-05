package app.wordglass.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.wordglass.ui.gallery.ComponentGalleryScreen

private const val ROUTE_GALLERY = "component_gallery"

// Debug implementation — registers the gallery route and provides the open callback.
// The main source set provides no-op stubs; these override them in debug builds only.
fun NavGraphBuilder.registerGalleryRoute(navController: NavController) {
    composable(ROUTE_GALLERY) {
        ComponentGalleryScreen(onBack = { navController.popBackStack() })
    }
}

fun galleryOpenCallback(navController: NavController): (() -> Unit)? =
    { navController.navigate(ROUTE_GALLERY) }
