package com.cebolao.lotofacil.di

import com.cebolao.lotofacil.core.coroutine.DefaultDispatchersProvider
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.service.GameGenerator
import com.cebolao.lotofacil.domain.service.GameStatsAnalyzer
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
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
    fun provideGameGenerator(dispatchersProvider: DispatchersProvider): GameGenerator =
        GameGenerator(dispatchersProvider)

    @Provides
    @Singleton
    fun provideStatisticsAnalyzer(dispatchersProvider: DispatchersProvider): StatisticsAnalyzer =
        StatisticsAnalyzer(dispatchersProvider)

    @Provides
    @Singleton
    fun provideGameStatsAnalyzer(dispatchersProvider: DispatchersProvider): GameStatsAnalyzer =
        GameStatsAnalyzer(dispatchersProvider)

    @Provides
    @Singleton
    fun provideApplicationScope(dispatchersProvider: DispatchersProvider): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchersProvider.default)
}
