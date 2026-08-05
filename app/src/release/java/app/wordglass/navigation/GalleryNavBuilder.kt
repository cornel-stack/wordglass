package app.wordglass.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder

// Release stub — no gallery route registered, no open callback provided.
// The debug source set provides the real implementation.
fun NavGraphBuilder.registerGalleryRoute(navController: NavController) = Unit
fun galleryOpenCallback(navController: NavController): (() -> Unit)? = null
