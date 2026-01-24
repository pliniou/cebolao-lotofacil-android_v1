package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val localDataSource: HistoryLocalDataSource,
    private val remoteDataSource: HistoryRemoteDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope
) : HistoryRepository {

    private val syncMutex = Mutex()
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    override val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    // Holds the full list of draws sorted by contest number (descending).
    private val _history = MutableStateFlow<List<HistoricalDraw>>(emptyList())

    override suspend fun getHistory(): List<HistoricalDraw> {
        if (_history.value.isEmpty()) {
            initialiseHistory()
        }
        return _history.value
    }

    override suspend fun getLastDraw(): HistoricalDraw? {
        return getHistory().firstOrNull()
    }

    override suspend fun syncHistory(): AppResult<Unit> = syncMutex.withLock {
        if (_syncStatus.value == SyncStatus.Syncing) return AppResult.Success(Unit)
        _syncStatus.value = SyncStatus.Syncing
        return try {
            val latestRemote = remoteDataSource.getLatestDraw()
            val currentLatest = _history.value.firstOrNull()?.contestNumber ?: 0
            if (latestRemote != null && latestRemote.contestNumber > currentLatest) {
                val rangeToFetch = (currentLatest + 1)..latestRemote.contestNumber
                val newDraws = remoteDataSource.getDrawsInRange(rangeToFetch)
                if (newDraws.isNotEmpty()) {
                    // Prepend new draws and maintain sorted order
                    _history.value = (newDraws + _history.value).sortedByDescending { it.contestNumber }
                    localDataSource.saveNewContests(newDraws)
                }
            }
            _syncStatus.value = SyncStatus.Success
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val error = ErrorMapper.toAppError(e)
            _syncStatus.value = SyncStatus.Failed(ErrorMapper.messageFor(error))
            AppResult.Failure(error)
        }
    }

    private suspend fun initialiseHistory() {
        val localHistory = localDataSource.getLocalHistory()
        _history.value = localHistory.sortedByDescending { it.contestNumber }
        // Launch sync in the background but do not wait for it here
        applicationScope.launch { syncHistory() }
    }
}
