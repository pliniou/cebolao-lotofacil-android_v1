package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.data.datasource.HistoryLocalDataSource
import com.cebolao.lotofacil.data.datasource.HistoryRemoteDataSource
import com.cebolao.lotofacil.di.ApplicationScope
import com.cebolao.lotofacil.domain.repository.HistoryRepository
import com.cebolao.lotofacil.domain.repository.SyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    override val syncStatus = _syncStatus.asStateFlow()

    init {
        applicationScope.launch {
            loadInitialHistory()
        }
    }

    override suspend fun getHistory(): List<HistoricalDraw> {
        if (historyCache.isEmpty()) {
            loadInitialHistory()
        }
        return cacheMutex.withLock {
            sortedHistoryCache ?: historyCache.values
                .sortedByDescending { it.contestNumber }
                .also { sortedHistoryCache = it }
        }
    }

    override suspend fun getLastDraw(): HistoricalDraw? {
        return try {
            remoteDataSource.getLatestDraw()?.also { lastRemote ->
                cacheMutex.withLock {
                    if (historyCache[lastRemote.contestNumber] == null) {
                        historyCache[lastRemote.contestNumber] = lastRemote
                        sortedHistoryCache = null
                    }
                }
                localDataSource.saveNewContests(listOf(lastRemote))
            } ?: historyCache.values.maxByOrNull { it.contestNumber }
        } catch (e: Exception) {
            historyCache.values.maxByOrNull { it.contestNumber }
        }
    }

    override fun syncHistory(): Job = applicationScope.launch {
        if (_syncStatus.value == SyncStatus.Syncing) return@launch
        _syncStatus.value = SyncStatus.Syncing
        try {
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
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Failed(mapErrorToMessage(e))
        }
    }

    private fun mapErrorToMessage(e: Exception): String {
        return when (e) {
            is java.net.SocketTimeoutException, is java.net.UnknownHostException -> 
                "Sem conexão com o servidor. Tente novamente."
            is java.io.IOException -> "Erro ao salvar os dados localmente."
            else -> "Ocorreu um erro inesperado na sincronização."
        }
    }

    private suspend fun loadInitialHistory() {
        try {
            val localHistory = localDataSource.getLocalHistory()
            if (localHistory.isNotEmpty()) {
                cacheMutex.withLock {
                    historyCache.clear()
                    historyCache.putAll(localHistory.associateBy { it.contestNumber })
                    sortedHistoryCache = null
                }
            }
            // Always try to sync after loading local cache
            syncHistory()
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Failed("Falha ao carregar histórico local.")
        }
    }
}

