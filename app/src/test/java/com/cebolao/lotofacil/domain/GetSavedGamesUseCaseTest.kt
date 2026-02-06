package com.cebolao.lotofacil.domain.usecase

import app.cash.turbine.test
import com.cebolao.lotofacil.core.coroutine.TestDispatchersProvider
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.core.testing.FakeSavedGamesRepository
import com.cebolao.lotofacil.domain.model.LotofacilGame
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSavedGamesUseCaseTest {

    private lateinit var gameRepository: com.cebolao.lotofacil.domain.repository.GameRepository
    private lateinit var dispatchersProvider: TestDispatchersProvider
    private lateinit var useCase: GetSavedGamesUseCase

    @Before
    fun setup() {
        gameRepository = com.cebolao.lotofacil.core.testing.FakeGameRepository()
        dispatchersProvider = TestDispatchersProvider()
        useCase = GetSavedGamesUseCase(gameRepository, dispatchersProvider)
    }

    @Test
    fun `getSavedGames should return empty list when no games saved`() = runTest {
        useCase().test {
            val result = awaitItem()
            assertEquals(AppResult.Success(emptyList<LotofacilGame>()), result)
            awaitComplete()
        }
    }

    @Test
    fun `getSavedGames should return saved games`() = runTest {
        val game1 = LotofacilGame((1..15).toSet())
        val game2 = LotofacilGame((2..16).toSet())

        val repository = gameRepository as com.cebolao.lotofacil.core.testing.FakeGameRepository
        repository.saveGame(game1)
        repository.saveGame(game2)

        useCase().test {
            val result = awaitItem()
            assert(result is AppResult.Success)
            if (result is AppResult.Success) {
                assertEquals(2, result.data.size)
            }
            awaitComplete()
        }
    }
}
