package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.GameStatistic
import com.cebolao.lotofacil.domain.model.GameStatisticType
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameStatsAnalyzer @Inject constructor(private val dispatchersProvider: DispatchersProvider) {

    suspend fun analyze(game: LotofacilGame): List<GameStatistic> =
        withContext(dispatchersProvider.io) {
            listOf(
                GameStatistic(GameStatisticType.SUM, game.sum),
                GameStatistic(GameStatisticType.EVENS, game.evens),
                GameStatistic(GameStatisticType.ODDS, game.odds),
                GameStatistic(GameStatisticType.PRIMES, game.primes),
                GameStatistic(GameStatisticType.FIBONACCI, game.fibonacci),
                GameStatistic(GameStatisticType.FRAME, game.frame),
                GameStatistic(GameStatisticType.PORTRAIT, game.portrait),
                GameStatistic(GameStatisticType.MULTIPLES_OF_3, game.multiplesOf3)
            )
        }
}
