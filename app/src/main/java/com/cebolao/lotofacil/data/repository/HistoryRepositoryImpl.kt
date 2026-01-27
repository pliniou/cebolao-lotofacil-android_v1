package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.error.ErrorMapper
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.di.ApplicationScope
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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

    init {
        applicationScope.launch {
            localDataSource.populateIfNeeded()
            syncHistory()
        }
    }

    override fun getHistory(): Flow<List<HistoricalDraw>> = localDataSource.getHistory()

    override suspend fun getLastDraw(): HistoricalDraw? {
        return localDataSource.getLatestDraw()
    }

    override suspend fun syncHistory(): AppResult<Unit> = syncMutex.withLock {
        if (_syncStatus.value == SyncStatus.Syncing) return AppResult.Success(Unit)
        _syncStatus.value = SyncStatus.Syncing
        return try {
            val latestRemote = remoteDataSource.getLatestDraw()
            val currentLatest = localDataSource.getLatestDraw()?.contestNumber ?: 0
            
            if (latestRemote != null && latestRemote.contestNumber > currentLatest) {
                val rangeToFetch = (currentLatest + 1)..latestRemote.contestNumber
                val newDraws = remoteDataSource.getDrawsInRange(rangeToFetch)
                if (newDraws.isNotEmpty()) {
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
}
