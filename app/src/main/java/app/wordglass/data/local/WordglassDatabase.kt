package app.wordglass.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * The local Room database — slice 01's source of truth. Version 1, single entity.
 *
 * `exportSchema = false` for now: there are no migrations yet, so there is no baseline schema
 * to diff against. Flip it to true (and set `room.schemaLocation`) when the first migration
 * lands, so migration tests have a schema to validate against.
 */
@Database(entities = [Script::class], version = 1, exportSchema = false)
abstract class WordglassDatabase : RoomDatabase() {
    abstract fun scriptDao(): ScriptDao
}
