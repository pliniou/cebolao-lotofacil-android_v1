package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.coroutine.TestDispatchersProvider
import com.cebolao.lotofacil.core.error.AppError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class HistoryRepositoryImplTest {

    private lateinit var localDataSource: HistoryLocalDataSource
    private lateinit var remoteDataSource: HistoryRemoteDataSource
    private lateinit var dispatchersProvider: TestDispatchersProvider
    private lateinit var repository: HistoryRepositoryImpl

    @Before
    fun setup() {
        localDataSource = mock()
        remoteDataSource = mock()
        dispatchersProvider = TestDispatchersProvider()
        repository = HistoryRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            dispatchersProvider = dispatchersProvider
        )
    }

    @Test
    fun `getHistoryFlow should return local data`() = runTest {
        val mockDraws = listOf(
            HistoricalDraw(1, "2025-01-01", setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), mapOf()),
            HistoricalDraw(2, "2025-01-02", setOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16), mapOf())
        )
        whenever(localDataSource.getAllDrawsFlow()).thenReturn(flowOf(mockDraws))

        val result = repository.getHistoryFlow()
        assertEquals(flowOf(mockDraws).collect { }, result.collect { })

        verify(localDataSource).getAllDrawsFlow()
    }

    @Test
    fun `getLatestDraw should return the most recent draw`() = runTest {
        val mockDraw = HistoricalDraw(1, "2025-01-01", setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), mapOf())
        whenever(localDataSource.getLatestDraw()).thenReturn(AppResult.Success(mockDraw))

        val result = repository.getLatestDraw()
        assertEquals(AppResult.Success(mockDraw), result)

        verify(localDataSource).getLatestDraw()
    }

    @Test
    fun `getLatestDraw should return failure when no data exists`() = runTest {
        whenever(localDataSource.getLatestDraw()).thenReturn(
            AppResult.Failure(AppError.EmptyHistoryError("No draws found"))
        )

        val result = repository.getLatestDraw()
        assert(result is AppResult.Failure)

        verify(localDataSource).getLatestDraw()
    }

    @Test
    fun `upsertDraw should save draw to local datasource`() = runTest {
        val mockDraw = HistoricalDraw(1, "2025-01-01", setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15), mapOf())
        whenever(localDataSource.upsertDraw(mockDraw)).thenReturn(AppResult.Success(Unit))

        val result = repository.upsertDraw(mockDraw)
        assertEquals(AppResult.Success(Unit), result)

        verify(localDataSource).upsertDraw(mockDraw)
    }
}
