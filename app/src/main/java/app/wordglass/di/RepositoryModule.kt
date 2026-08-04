package app.wordglass.di

import app.wordglass.data.repository.ScriptRepository
import app.wordglass.data.repository.ScriptRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the [ScriptRepository] interface to its implementation, so anything asking for the
 * interface gets [ScriptRepositoryImpl]. `@Binds` is the lightweight way to wire an interface
 * to a class Hilt already knows how to construct (it has an `@Inject` constructor).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScriptRepository(impl: ScriptRepositoryImpl): ScriptRepository
}
