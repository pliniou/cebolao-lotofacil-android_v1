package com.cebolao.lotofacil.domain.usecase

import app.cash.turbine.test
import com.cebolao.lotofacil.core.coroutine.TestDispatchersProvider
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckGameUseCaseTest {

    private lateinit var gameRepository: GameRepository
    private lateinit var historyRepository: HistoryRepository
    private lateinit var dispatchersProvider: TestDispatchersProvider
    private lateinit var useCase: CheckGameUseCase

    @Before
    fun setup() {
        gameRepository = mock()
        historyRepository = mock()
        dispatchersProvider = TestDispatchersProvider()
        useCase = CheckGameUseCase(gameRepository, historyRepository, dispatchersProvider)
    }

    @Test
    fun `checkGame should return matches count for valid game`() = runTest {
        val gameNumbers = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val game = LotofacilGame(gameNumbers)
        val mockHistory = listOf(
            HistoricalDraw(1, "2025-01-01", setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), mapOf()),
            HistoricalDraw(2, "2025-01-02", setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16), mapOf())
        )

        whenever(historyRepository.getHistoryFlow()).thenReturn(flowOf(mockHistory))

        useCase(game).test {
            val item = awaitItem()
            assertTrue(item.scoreCounts.isNotEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `checkGame should handle empty history`() = runTest {
        val gameNumbers = setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val game = LotofacilGame(gameNumbers)

        whenever(historyRepository.getHistoryFlow()).thenReturn(flowOf(emptyList()))

        useCase(game).test {
            val item = awaitItem()
            assertEquals(0, item.scoreCounts.size)
            awaitComplete()
        }
    }
}
