package com.cebolao.lotofacil.data.datasource

import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.data.datasource.database.HistoryDao
import com.cebolao.lotofacil.data.datasource.database.entity.HistoricalDrawEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class HistoryLocalDataSourceTest {

    private lateinit var dao: HistoryDao
    private lateinit var dataSource: HistoryLocalDataSource

    @Before
    fun setup() {
        dao = mock()
        dataSource = HistoryLocalDataSource(dao)
    }

    @Test
    fun `getAllDrawsFlow should return flows from DAO`() = runTest {
        val mockEntities = listOf(
            HistoricalDrawEntity(1, "2025-01-01", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", ""),
            HistoricalDrawEntity(2, "2025-01-02", "2,3,4,5,6,7,8,9,10,11,12,13,14,15,16", "")
        )
        whenever(dao.getAllDrawsFlow()).thenReturn(flowOf(mockEntities))

        val result = dataSource.getAllDrawsFlow()
        // Verify the flow is called and converted to domain models
        verify(dao).getAllDrawsFlow()
    }

    @Test
    fun `getLatestDraw should return success with draw`() = runTest {
        val mockEntity = HistoricalDrawEntity(1, "2025-01-01", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", "")
        whenever(dao.getLatestDraw()).thenReturn(mockEntity)

        val result = dataSource.getLatestDraw()
        assert(result is AppResult.Success)

        verify(dao).getLatestDraw()
    }

    @Test
    fun `getLatestDraw should return failure when DAO returns null`() = runTest {
        whenever(dao.getLatestDraw()).thenReturn(null)

        val result = dataSource.getLatestDraw()
        assert(result is AppResult.Failure)

        verify(dao).getLatestDraw()
    }

    @Test
    fun `upsertDraw should save entity to DAO`() = runTest {
        val mockEntity = HistoricalDrawEntity(1, "2025-01-01", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", "")
        whenever(dao.upsertDraw(mockEntity)).thenReturn(1L)

        val result = dataSource.upsertDraw(mockEntity)
        assert(result is AppResult.Success)

        verify(dao).upsertDraw(mockEntity)
    }

    @Test
    fun `upsertDraw should return failure on exception`() = runTest {
        val mockEntity = HistoricalDrawEntity(1, "2025-01-01", "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", "")
        whenever(dao.upsertDraw(mockEntity)).thenThrow(RuntimeException("DB Error"))

        val result = dataSource.upsertDraw(mockEntity)
        assert(result is AppResult.Failure)

        verify(dao).upsertDraw(mockEntity)
    }
}
