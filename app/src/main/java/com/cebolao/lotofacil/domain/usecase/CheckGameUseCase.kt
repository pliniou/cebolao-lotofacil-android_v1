package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.R
import com.cebolao.lotofacil.domain.model.CheckResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.di.DefaultDispatcher
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.GameStatsAnalyzer
import com.cebolao.lotofacil.viewmodels.CheckerUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CheckGameUseCase @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val gameStatsAnalyzer: GameStatsAnalyzer,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    private val recentContestsCount = 15

    operator fun invoke(gameNumbers: Set<Int>): Flow<CheckerUiState> = flow {
        emit(CheckerUiState.Loading(progress = 0.1f, message = "Carregando histórico..."))
        val history = historyRepository.getHistory()
        if (history.isEmpty()) {
            emit(CheckerUiState.Error(messageResId = R.string.error_no_history, canRetry = true))
            return@flow
        }

        emit(CheckerUiState.Loading(progress = 0.5f, message = "Calculando resultados..."))
        val result = calculateResult(gameNumbers, history)

        emit(CheckerUiState.Loading(progress = 0.8f, message = "Analisando estatísticas..."))
        val gameForAnalysis = LotofacilGame(numbers = gameNumbers)
        val simpleStats = gameStatsAnalyzer.analyze(gameForAnalysis)

        // CORREÇÃO: Converte a List retornada pelo analyzer para a ImmutableList esperada pelo UiState.
        emit(CheckerUiState.Success(result = result, simpleStats = simpleStats.toImmutableList()))
    }.flowOn(defaultDispatcher)

    private fun calculateResult(
        gameNumbers: Set<Int>,
        history: List<HistoricalDraw>
    ): CheckResult {
        val scoreCounts = mutableMapOf<Int, Int>()
        var lastHitContest: Int? = null
        var lastHitScore: Int? = null

        val recentHits = history.take(recentContestsCount)
            .map { draw -> draw.contestNumber to draw.numbers.intersect(gameNumbers).size }
            .reversed()

        history.forEach { draw ->
            val hits = draw.numbers.intersect(gameNumbers).size
            if (hits >= 11) {
                scoreCounts[hits] = (scoreCounts[hits] ?: 0) + 1
                if (lastHitContest == null) { // Captures the most recent hit first
                    lastHitContest = draw.contestNumber
                    lastHitScore = hits
                }
            }
        }

        return CheckResult(
            scoreCounts = scoreCounts.toImmutableMap(),
            lastHitContest = lastHitContest,
            lastHitScore = lastHitScore,
            lastCheckedContest = history.firstOrNull()?.contestNumber ?: 0,
            recentHits = recentHits.toImmutableList()
        )
    }
}
