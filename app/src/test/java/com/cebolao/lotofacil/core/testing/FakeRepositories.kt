package com.cebolao.lotofacil.core.testing

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Fake repositories para testes de ViewModel e UseCase.
 * Simula comportamento real sem dependências externas.
 */
class FakeGameRepository : GameRepository {
    private val games = mutableListOf<LotofacilGame>()

    override suspend fun saveGame(game: LotofacilGame): AppResult<Unit> {
        games.add(game)
        return AppResult.Success(Unit)
    }

    override suspend fun deleteGame(gameId: String): AppResult<Unit> {
        games.removeAll { it.id == gameId }
        return AppResult.Success(Unit)
    }

    override suspend fun toggleGamePin(gameId: String): AppResult<Unit> {
        return AppResult.Success(Unit)
    }

    override fun getSavedGamesFlow(): Flow<ImmutableList<LotofacilGame>> {
        return flowOf(games.toImmutableList())
    }

    override suspend fun clearUnpinnedGames(): AppResult<Unit> {
        games.removeAll { !it.isPinned }
        return AppResult.Success(Unit)
    }

    override suspend fun getSavedGamesCount(): AppResult<Int> {
        return AppResult.Success(games.size)
    }
}

class FakeHistoryRepository : HistoryRepository {
    private val history = mutableListOf<HistoricalDraw>()

    init {
        // Popula com dados de teste
        history.add(
            HistoricalDraw(
                contestNumber = 1,
                date = "2025-01-01",
                numbers = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
                prizesByScore = mapOf()
            )
        )
    }

    override fun getHistoryFlow(): Flow<List<HistoricalDraw>> {
        return flowOf(history)
    }

    override suspend fun getLatestDraw(): AppResult<HistoricalDraw> {
        return if (history.isNotEmpty()) {
            AppResult.Success(history.last())
        } else {
            AppResult.Failure(com.cebolao.lotofacil.core.error.AppError.EmptyHistoryError("No history"))
        }
    }

    override suspend fun upsertDraw(draw: HistoricalDraw): AppResult<Unit> {
        history.add(draw)
        return AppResult.Success(Unit)
    }

    override suspend fun syncHistory(): AppResult<Int> {
        return AppResult.Success(history.size)
    }
}

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private var filters = listOf(
        FilterState(FilterType.WITHOUT_REPETITIVE, true, 0..0),
        FilterState(FilterType.EVEN_ODD_BALANCE, true, 0..0),
        FilterState(FilterType.SEQUENTIAL, false, 0..0)
    )

    override fun getFilterPreferences(): Flow<com.cebolao.lotofacil.domain.model.FilterPreferences> {
        return flowOf(
            com.cebolao.lotofacil.domain.model.FilterPreferences(filters.toImmutableList())
        )
    }

    override suspend fun saveFilterPreferences(filters: List<FilterState>): AppResult<Unit> {
        this.filters = filters
        return AppResult.Success(Unit)
    }
}

class FakeStatisticsRepository : StatisticsRepository {
    override fun getStatisticsFlow(): Flow<List<com.cebolao.lotofacil.domain.model.GameStatistic>> {
        return flowOf(emptyList())
    }

    override suspend fun getFrequencyAnalysis(): AppResult<List<com.cebolao.lotofacil.domain.model.GameStatistic>> {
        return AppResult.Success(emptyList())
    }

    override suspend fun getPatternAnalysis(): AppResult<List<com.cebolao.lotofacil.domain.model.GameStatistic>> {
        return AppResult.Success(emptyList())
    }

    override suspend fun getTrendAnalysis(): AppResult<List<com.cebolao.lotofacil.domain.model.GameStatistic>> {
        return AppResult.Success(emptyList())
    }
}
