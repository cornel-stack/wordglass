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

    /** Create a new script from [body]; returns the generated id. */
    suspend fun create(body: String): String

    /** Update an existing script's body (re-deriving the title, bumping updatedAt). */
    suspend fun updateBody(id: String, body: String)

    /** Soft-delete: stamp the tombstone (never a row delete). */
    suspend fun softDelete(id: String)
}
