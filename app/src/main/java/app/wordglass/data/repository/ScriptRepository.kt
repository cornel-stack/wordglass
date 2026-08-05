package app.wordglass.data.repository

import app.wordglass.data.local.Script
import kotlinx.coroutines.flow.Flow

/**
 * The single door the ViewModels use to reach scripts. It owns the details the UI shouldn't
 * know: id generation, org/user stamping, title derivation, timestamps, and soft-delete.
 */
interface ScriptRepository {

    /** Live scripts, most-recently-touched first. */
    fun observeScripts(): Flow<List<Script>>

    suspend fun getById(id: String): Script?

    /** Create a new script from [body]; returns the generated id. Title derives from [body]. */
    suspend fun create(body: String): String

    /**
     * Update an existing script's body, bumping updatedAt. Re-derives the title from the body's
     * first line **only while the user has not taken the title over** (`titleSetByUser`).
     */
    suspend fun updateBody(id: String, body: String)

    /** Set a user-authored title and mark it user-owned, so the body stops re-deriving it. */
    suspend fun updateTitle(id: String, title: String)

    /**
     * Resume automatic title derivation: clear the user-owned flag and re-derive the title from
     * the stored body's first line. Called when the user empties a taken-over title.
     */
    suspend fun resetTitleToAuto(id: String)

    /** Soft-delete: stamp the tombstone (never a row delete). */
    suspend fun softDelete(id: String)
}
