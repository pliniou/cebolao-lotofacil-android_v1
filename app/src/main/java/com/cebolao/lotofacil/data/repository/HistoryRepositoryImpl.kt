package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.core.error.AppError
import com.cebolao.lotofacil.core.error.NetworkError
import com.cebolao.lotofacil.core.error.PersistenceError
import com.cebolao.lotofacil.core.error.UnknownError
import com.cebolao.lotofacil.core.result.AppResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import com.cebolao.lotofacil.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val localDataSource: HistoryLocalDataSource,
    private val remoteDataSource: HistoryRemoteDataSource,
    @ApplicationScope private val applicationScope: CoroutineScope
) : HistoryRepository {
    private val historyCache = ConcurrentHashMap<Int, HistoricalDraw>()
    private val cacheMutex = Mutex()
    private var sortedHistoryCache: List<HistoricalDraw>? = null
    private val initializationMutex = Mutex()
    private val syncMutex = Mutex()
    private var initialized = false

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    override val syncStatus = _syncStatus.asStateFlow()

    override suspend fun getHistory(): List<HistoricalDraw> {
        ensureHistoryLoaded()
        return cacheMutex.withLock {
            sortedHistoryCache ?: historyCache.values
                .sortedByDescending { it.contestNumber }
                .also { sortedHistoryCache = it }
        }
    }

    override suspend fun getLastDraw(): HistoricalDraw? {
        ensureHistoryLoaded()
        return try {
            val lastRemote = remoteDataSource.getLatestDraw()
            if (lastRemote != null) {
                cacheMutex.withLock {
                    if (historyCache[lastRemote.contestNumber] == null) {
                        historyCache[lastRemote.contestNumber] = lastRemote
                        sortedHistoryCache = null
                    }
                }
                localDataSource.saveNewContests(listOf(lastRemote))
                lastRemote
            } else {
                historyCache.values.maxByOrNull { it.contestNumber }
            }
        } catch (e: Exception) {
            historyCache.values.maxByOrNull { it.contestNumber }
        }
    }

    override suspend fun syncHistory(): AppResult<Unit> = syncMutex.withLock {
        if (_syncStatus.value == SyncStatus.Syncing) {
            return AppResult.Success(Unit)
        }
        _syncStatus.value = SyncStatus.Syncing
        return try {
            val latestRemote = remoteDataSource.getLatestDraw()
            val latestLocal = historyCache.values.maxByOrNull { it.contestNumber }?.contestNumber ?: 0

            if (latestRemote != null && latestRemote.contestNumber > latestLocal) {
                val rangeToFetch = (latestLocal + 1)..latestRemote.contestNumber
                val newDraws = remoteDataSource.getDrawsInRange(rangeToFetch)
                if (newDraws.isNotEmpty()) {
                    cacheMutex.withLock {
                        historyCache.putAll(newDraws.associateBy { it.contestNumber })
                        sortedHistoryCache = null
                    }
                    localDataSource.saveNewContests(newDraws)
                }
            }
            _syncStatus.value = SyncStatus.Success
            AppResult.Success(Unit)
        } catch (e: Exception) {
            val message = mapErrorToMessage(e)
            _syncStatus.value = SyncStatus.Failed(message)
            AppResult.Failure(mapErrorToAppError(e))
        }
    }

    private suspend fun ensureHistoryLoaded() {
        if (initialized) return
        initializationMutex.withLock {
            if (initialized) return
            loadInitialHistory()
            initialized = true
            applicationScope.launch {
                syncHistory()
            }
        }
    }

    private suspend fun loadInitialHistory() {
        try {
            val localHistory = localDataSource.getLocalHistory()
            if (localHistory.isNotEmpty()) {
                cacheMutex.withLock {
                    historyCache.clear()
                    historyCache.putAll(localHistory.associateBy { draw -> draw.contestNumber })
                    sortedHistoryCache = null
                }
            }
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Failed("Falha ao carregar historico local.")
        }
    }

    private fun mapErrorToMessage(e: Exception): String {
        return when (e) {
            is java.net.SocketTimeoutException, is java.net.UnknownHostException ->
                "Sem conexao com o servidor. Tente novamente."
            is java.io.IOException -> "Erro ao salvar os dados localmente."
            else -> "Ocorreu um erro inesperado na sincronizacao."
        }
    }

    private fun mapErrorToAppError(e: Exception): AppError {
        return when (e) {
            is java.net.SocketTimeoutException, is java.net.UnknownHostException -> NetworkError(e)
            is java.io.IOException -> PersistenceError(e)
            else -> UnknownError(e)
        }
    }
}
