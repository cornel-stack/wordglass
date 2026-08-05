package app.wordglass.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/** Marks the app-lifetime coroutine scope (as opposed to a viewModelScope). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

/**
 * A coroutine scope that outlives any single ViewModel. The editor uses it for the final
 * autosave when the user navigates back: a `viewModelScope` write would be cancelled the instant
 * the editor is popped, losing the last edit. `SupervisorJob` = one failed write never tears the
 * scope down.
 */
@Module
@InstallIn(SingletonComponent::class)
object CoroutinesModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
