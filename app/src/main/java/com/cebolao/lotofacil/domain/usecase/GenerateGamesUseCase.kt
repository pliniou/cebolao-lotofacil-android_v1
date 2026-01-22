package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.GameGenerationException
import com.cebolao.lotofacil.domain.service.GameGenerator
import kotlinx.coroutines.withContext
import javax.inject.Inject

import com.cebolao.lotofacil.core.error.UnknownError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.result.toSuccess

class GenerateGamesUseCase @Inject constructor(
    private val gameGenerator: GameGenerator,
    private val historyRepository: HistoryRepository,
    private val dispatchersProvider: DispatchersProvider
) {
    suspend operator fun invoke(
        quantity: Int,
        activeFilters: List<FilterState>
    ): AppResult<List<LotofacilGame>> = withContext(dispatchersProvider.default) {
        try {
            val lastDraw = if (activeFilters.any { it.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR && it.isEnabled }) {
                historyRepository.getLastDraw()?.numbers
            } else {
                null
            }

            if (activeFilters.any { it.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR && it.isEnabled } && lastDraw == null) {
                throw GameGenerationException("Histórico não disponível. Desative o filtro 'Repetidas' ou verifique sua conexão.")
            }

            val games = gameGenerator.generateGames(
                activeFilters = activeFilters,
                count = quantity,
                lastDraw = lastDraw
            )
            games.toSuccess()
        } catch (e: Exception) {
             AppResult.Failure(UnknownError(e))
        }
    }
}
