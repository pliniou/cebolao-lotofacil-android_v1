package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.data.FilterState
import com.cebolao.lotofacil.data.FilterType
import com.cebolao.lotofacil.data.LotofacilGame
import com.cebolao.lotofacil.di.DefaultDispatcher
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.GameGenerationException
import com.cebolao.lotofacil.domain.service.GameGenerator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateGamesUseCase @Inject constructor(
    private val gameGenerator: GameGenerator,
    private val historyRepository: HistoryRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        quantity: Int,
        activeFilters: List<FilterState>
    ): Result<List<LotofacilGame>> = withContext(defaultDispatcher) {
        runCatching {
            val lastDraw = if (activeFilters.any { it.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR && it.isEnabled }) {
                historyRepository.getLastDraw()?.numbers
            } else {
                null
            }

            if (activeFilters.any { it.type == FilterType.REPETIDAS_CONCURSO_ANTERIOR && it.isEnabled } && lastDraw == null) {
                throw GameGenerationException("Histórico não disponível. Desative o filtro 'Repetidas' ou verifique sua conexão.")
            }

            gameGenerator.generateGames(
                activeFilters = activeFilters,
                count = quantity,
                lastDraw = lastDraw
            )
        }
    }
}