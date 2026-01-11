package com.cebolao.lotofacil.viewmodels

import app.cash.turbine.test
import com.cebolao.lotofacil.data.FilterType
import com.cebolao.lotofacil.data.LotofacilGame
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.usecase.GenerateGamesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class FiltersViewModelTest {

    @get:Rule
    val instantExecutorRule = MainCoroutineRule()

    private lateinit var viewModel: FiltersViewModel
    private val mockGameRepository: GameRepository = mock()
    private val mockHistoryRepository: HistoryRepository = mock()
    private val mockGenerateGamesUseCase: GenerateGamesUseCase = mock()

    @Before
    fun setUp() {
        viewModel = FiltersViewModel(
            gameRepository = mockGameRepository,
            generateGamesUseCase = mockGenerateGamesUseCase,
            historyRepository = mockHistoryRepository
        )
    }

    @Test
    fun `initial state is correct`() = runTest {
        val initialState = viewModel.uiState.value
        assertEquals(FilterType.entries.size, initialState.filterStates.size)
        assertEquals(GenerationUiState.Idle, initialState.generationState)
        assertEquals(0, initialState.activeFiltersCount)
        assertEquals(1f, initialState.successProbability)
    }

    @Test
    fun `onFilterToggle updates filter state and active count`() = runTest {
        assertEquals(false, viewModel.uiState.value.filterStates.first().isEnabled)
        assertEquals(0, viewModel.uiState.value.activeFiltersCount)

        viewModel.onFilterToggle(FilterType.SOMA_DEZENAS, true)

        assertEquals(true, viewModel.uiState.value.filterStates.first { it.type == FilterType.SOMA_DEZENAS }.isEnabled)
        assertEquals(1, viewModel.uiState.value.activeFiltersCount)
    }

    @Test
    fun `generateGames success triggers navigation and adds games`() = runTest {
        val games = listOf(LotofacilGame(numbers = (1..15).toSet()))
        whenever(mockGenerateGamesUseCase.invoke(any(), any())).thenReturn(Result.success(games))

        viewModel.navigationEvent.test {
            viewModel.generateGames(1)

            assertTrue(viewModel.uiState.value.generationState is GenerationUiState.Loading)

            assertEquals(NavigationEvent.NavigateToGeneratedGames, awaitItem())

            verify(mockGameRepository).addGeneratedGames(games)

            assertEquals(GenerationUiState.Idle, viewModel.uiState.value.generationState)
        }
    }

    @Test
    fun `generateGames failure shows snackbar`() = runTest {
        val errorMessage = "Generation failed"
        whenever(mockGenerateGamesUseCase.invoke(any(), any())).thenReturn(Result.failure(Exception(errorMessage)))

        viewModel.navigationEvent.test {
            viewModel.generateGames(1)

            val event = awaitItem()
            assertTrue(event is NavigationEvent.ShowSnackbar)
            assertEquals(errorMessage, (event as NavigationEvent.ShowSnackbar).message)

            assertEquals(GenerationUiState.Idle, viewModel.uiState.value.generationState)
        }
    }

    @Test
    fun `resetAllFilters restores default state`() = runTest {
        viewModel.onFilterToggle(FilterType.PARES, true)
        assertNotEquals(0, viewModel.uiState.value.activeFiltersCount)

        viewModel.resetAllFilters()

        viewModel.uiState.value.filterStates.forEach {
            assertFalse(it.isEnabled)
            assertEquals(it.type.defaultRange, it.selectedRange)
        }
        assertEquals(0, viewModel.uiState.value.activeFiltersCount)
    }
}

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : org.junit.rules.TestWatcher() {
    override fun starting(description: org.junit.runner.Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: org.junit.runner.Description) {
        Dispatchers.resetMain()
    }
}