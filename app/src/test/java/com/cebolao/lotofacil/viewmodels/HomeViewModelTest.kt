package com.cebolao.lotofacil.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.LastDrawStats
import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.domain.service.StatisticsAnalyzer
import com.cebolao.lotofacil.domain.usecase.GetHomeScreenDataUseCase
import com.cebolao.lotofacil.domain.usecase.HomeScreenData
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getHomeScreenDataUseCase: GetHomeScreenDataUseCase
    private lateinit var historyRepository: HistoryRepository
    private lateinit var statisticsAnalyzer: StatisticsAnalyzer
    private lateinit var dispatchersProvider: DispatchersProvider
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getHomeScreenDataUseCase = mock()
        historyRepository = mock()
        statisticsAnalyzer = mock()
        dispatchersProvider = mock()

        whenever(dispatchersProvider.default).thenReturn(testDispatcher)
        whenever(dispatchersProvider.main).thenReturn(testDispatcher)
        whenever(dispatchersProvider.io).thenReturn(testDispatcher)
        whenever(historyRepository.syncStatus).thenReturn(MutableStateFlow(SyncStatus.Idle))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have loading true and no error`() = runTest {
        whenever(getHomeScreenDataUseCase.invoke()).thenReturn(flowOf(
            AppResult.Success(
                HomeScreenData(
                    history = emptyList(),
                    lastDrawStats = null,
                    initialStats = StatisticsReport()
                )
            )
        ))

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase,
            historyRepository,
            statisticsAnalyzer,
            dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isScreenLoading)
        assertNull(state.errorMessageResId)
    }

    @Test
    fun `onTimeWindowSelected should update selectedTimeWindow`() = runTest {
        val lastDrawStats = LastDrawStats(
            contest = 3200,
            numbers = persistentSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
            sum = 120,
            evens = 7,
            odds = 8,
            primes = 5,
            frame = 8,
            portrait = 7,
            fibonacci = 4,
            multiplesOf3 = 5
        )
        val stats = StatisticsReport(
            mostFrequentNumbers = emptyList(),
            mostOverdueNumbers = emptyList(),
            averageSum = 195f
        )

        whenever(getHomeScreenDataUseCase.invoke()).thenReturn(flowOf(
            AppResult.Success(
                HomeScreenData(
                    history = emptyList(),
                    lastDrawStats = lastDrawStats,
                    initialStats = stats
                )
            )
        ))

        whenever(historyRepository.getHistory()).thenReturn(flowOf(emptyList()))

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase,
            historyRepository,
            statisticsAnalyzer,
            dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTimeWindowSelected(100)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(100, viewModel.uiState.value.selectedTimeWindow)
    }

    @Test
    fun `onPatternSelected should update selectedPattern`() = runTest {
        val lastDrawStats = LastDrawStats(
            contest = 3200,
            numbers = persistentSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
            sum = 120,
            evens = 7,
            odds = 8,
            primes = 5,
            frame = 8,
            portrait = 7,
            fibonacci = 4,
            multiplesOf3 = 5
        )
        val stats = StatisticsReport(
            mostFrequentNumbers = emptyList(),
            mostOverdueNumbers = emptyList(),
            averageSum = 195f
        )

        whenever(getHomeScreenDataUseCase.invoke()).thenReturn(flowOf(
            AppResult.Success(
                HomeScreenData(
                    history = emptyList(),
                    lastDrawStats = lastDrawStats,
                    initialStats = stats
                )
            )
        ))

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase,
            historyRepository,
            statisticsAnalyzer,
            dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onPatternSelected(StatisticPattern.EVENS)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(StatisticPattern.EVENS, viewModel.uiState.value.selectedPattern)
    }

    @Test
    fun `refreshData should update state and show success snackbar`() = runTest {
        val lastDrawStats = LastDrawStats(
            contest = 3200,
            numbers = persistentSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
            sum = 120,
            evens = 7,
            odds = 8,
            primes = 5,
            frame = 8,
            portrait = 7,
            fibonacci = 4,
            multiplesOf3 = 5
        )
        val stats = StatisticsReport(
            mostFrequentNumbers = emptyList(),
            mostOverdueNumbers = emptyList(),
            averageSum = 195f
        )

        whenever(getHomeScreenDataUseCase.invoke())
            .thenReturn(flowOf(
                AppResult.Success(
                    HomeScreenData(
                        history = emptyList(),
                        lastDrawStats = lastDrawStats,
                        initialStats = stats
                    )
                )
            ))

        whenever(historyRepository.syncHistory())
            .thenReturn(AppResult.Success(Unit))

        viewModel = HomeViewModel(
            getHomeScreenDataUseCase,
            historyRepository,
            statisticsAnalyzer,
            dispatchersProvider
        )

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.refreshData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isScreenLoading)
        assertNotNull(viewModel.uiState.value.lastDrawStats)
    }
}
