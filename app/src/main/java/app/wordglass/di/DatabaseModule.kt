package app.wordglass.di

import android.content.Context
import androidx.room.Room
import app.wordglass.data.local.ScriptDao
import app.wordglass.data.local.WordglassDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Tells Hilt how to build the Room database and hand out its DAO. `@InstallIn(SingletonComponent)`
 * = these live for the whole app; one database instance, shared.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WordglassDatabase =
        Room.databaseBuilder(context, WordglassDatabase::class.java, "wordglass.db")
            .addMigrations(WordglassDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideScriptDao(database: WordglassDatabase): ScriptDao = database.scriptDao()
}
