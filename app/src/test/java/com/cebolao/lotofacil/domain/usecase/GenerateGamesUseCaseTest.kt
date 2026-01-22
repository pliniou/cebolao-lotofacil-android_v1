package com.cebolao.lotofacil.domain.usecase

import com.cebolao.lotofacil.core.coroutine.TestDispatchersProvider
import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.service.GameGenerationException
import com.cebolao.lotofacil.domain.service.GameGenerator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GenerateGamesUseCaseTest {

    private lateinit var gameGenerator: GameGenerator
    private lateinit var historyRepository: HistoryRepository
    private lateinit var dispatchersProvider: TestDispatchersProvider
    private lateinit var useCase: GenerateGamesUseCase

    @Before
    fun setup() {
        gameGenerator = mock()
        historyRepository = mock()
        dispatchersProvider = TestDispatchersProvider()
        useCase = GenerateGamesUseCase(gameGenerator, historyRepository, dispatchersProvider)
    }

    @Test
    fun `generate games without repetitive filter should succeed`() = runTest {
        val expectedGames = listOf(
            LotofacilGame((1..15).toSet()),
            LotofacilGame((11..25).toSet())
        )
        whenever(gameGenerator.generateGames(any(), any(), any())).thenReturn(expectedGames)

        val result = useCase(quantity = 2, activeFilters = emptyList())

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `generate games with repetitive filter should use last draw`() = runTest {
        setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val expectedGames = listOf(LotofacilGame((1..15).toSet()))
        whenever(historyRepository.getLastDraw()).thenReturn(mock())
        whenever(gameGenerator.generateGames(any(), any(), any())).thenReturn(expectedGames)

        val filters = listOf(
            FilterState(type = FilterType.REPETIDAS_CONCURSO_ANTERIOR, isEnabled = true)
        )
        val result = useCase(quantity = 1, activeFilters = filters)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `generate games with restrictive filters should throw exception`() = runTest {
        whenever(gameGenerator.generateGames(any(), any(), any()))
            .thenThrow(GameGenerationException("Unable to generate games"))

        val filters = listOf(
            FilterState(type = FilterType.SOMA_DEZENAS, isEnabled = true, selectedRange = 200f..210f)
        )
        val result = useCase(quantity = 10, activeFilters = filters)

        assertTrue(result.isFailure)
    }
}
