package app.wordglass.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A locally-stored script. The column set mirrors the Postgres `scripts` table, so slice-13
 * sync is a mapping rather than a migration, plus [deletedAt] — the soft-delete tombstone.
 *
 * - [id] is a client-generated UUID string, so a row can be created offline and still be
 *   globally unique once sync lands.
 * - [title] is derived from the body's first line and persisted (the list row and the delete
 *   dialog both echo it; re-deriving on every read would be wasteful). An empty body persists
 *   an empty title; the row renders "Untitled" as a view-layer substitution, not stored copy.
 * - [createdAt] / [updatedAt] are epoch milliseconds. The list sorts by [updatedAt] and the row
 *   renders its relative date from [updatedAt] — one field for both (handoff addendum B4).
 * - [deletedAt] null = live; non-null = a tombstone. Deletion stamps it; it is never a row
 *   delete (handoff §10.1).
 */
@Entity(tableName = "scripts")
data class Script(
    @PrimaryKey val id: String,
    val orgId: String,
    val title: String,
    val body: String,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)
