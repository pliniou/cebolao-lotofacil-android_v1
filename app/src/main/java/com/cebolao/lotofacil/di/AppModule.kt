package com.cebolao.lotofacil.di

import com.cebolao.lotofacil.core.coroutine.DefaultDispatchersProvider
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDispatchersProvider(): DispatchersProvider = DefaultDispatchersProvider()

    @Provides
    @Singleton
    fun provideRandom(): java.util.Random = java.util.Random()

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchersProvider.default)
}
