package app.wordglass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The local Room database — slice 01's source of truth.
 *
 * `exportSchema = false` still: migration tests want a schema baseline to diff against, which
 * needs the `room.schemaLocation` build arg; that is deferred to a slice that adds migration
 * tests. [MIGRATION_1_2] below is hand-written, so it does not need the exported schema to run.
 */
@Database(entities = [Script::class], version = 2, exportSchema = false)
abstract class WordglassDatabase : RoomDatabase() {
    abstract fun scriptDao(): ScriptDao

    companion object {
        /**
         * v1 → v2: add `titleSetByUser`. Defaults to 0 (false) for every existing row — those were
         * written when the title always derived from the body, which is exactly what `false` means,
         * so no existing title changes behaviour.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE scripts ADD COLUMN titleSetByUser INTEGER NOT NULL DEFAULT 0",
                )
            }
        }
    }
}
