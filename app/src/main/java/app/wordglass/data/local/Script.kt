package app.wordglass.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A locally-stored script. The column set mirrors the Postgres `scripts` table, so slice-13
 * sync is a mapping rather than a migration, plus [deletedAt] — the soft-delete tombstone.
 *
 * - [id] is a client-generated UUID string, so a row can be created offline and still be
 *   globally unique once sync lands.
 * - [title] is the script's title. It auto-derives from the body's first line **until the user
 *   edits it directly** ([titleSetByUser]); from then on it is user-owned and the body no longer
 *   overwrites it. An empty title persists as "" and the list row renders "Untitled" as a
 *   view-layer substitution, not stored copy.
 * - [titleSetByUser] false = the title tracks the body's first line; true = the user has taken it
 *   over and `updateBody` must not re-derive it. Carried in the schema so the decoupling survives
 *   slice-13 sync (another device must not re-derive a user's chosen title from the body).
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
    val titleSetByUser: Boolean,
    val body: String,
    val createdBy: String,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
)
