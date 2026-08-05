package app.wordglass.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data-access object for [Script]. `@Dao` = Room generates the implementation at build time.
 * Every read filters out tombstones (`deletedAt IS NULL`); deletion is a soft-delete update.
 */
@Dao
interface ScriptDao {

    /**
     * Live scripts, most-recently-touched first. Returns a [Flow]: Room re-emits the list
     * whenever the table changes, so the UI updates itself without a manual refresh.
     */
    @Query("SELECT * FROM scripts WHERE deletedAt IS NULL ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<Script>>

    @Query("SELECT * FROM scripts WHERE id = :id")
    suspend fun getById(id: String): Script?

    /** Insert or update by primary key. */
    @Upsert
    suspend fun upsert(script: Script)

    /** Soft delete: stamp the tombstone and bump updatedAt. Never `DELETE FROM` (§10.1). */
    @Query("UPDATE scripts SET deletedAt = :now, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)
}
