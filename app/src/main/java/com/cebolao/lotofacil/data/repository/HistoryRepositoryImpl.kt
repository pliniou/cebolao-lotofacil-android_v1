package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.data.HistoricalDraw
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

    // CORREÇÃO: Substituído _isSyncing por _syncStatus para um controle de estado mais rico.
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
        return sortedHistoryCache ?: historyCache.values.sortedByDescending { it.contestNumber }.also {
            sortedHistoryCache = it
        }
    }

    override suspend fun getLastDraw(): HistoricalDraw? {
        val lastRemote = remoteDataSource.getLatestDraw()
        if (lastRemote != null) {
            cacheMutex.withLock {
                historyCache[lastRemote.contestNumber] = lastRemote
                sortedHistoryCache = null
            }
            localDataSource.saveNewContests(listOf(lastRemote))
            return lastRemote
        }
        return historyCache.values.maxByOrNull { it.contestNumber }
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
            val errorMessage = when {
                e is java.net.SocketTimeoutException || e is java.net.UnknownHostException -> 
                    "Falha de conexão. Verifique sua internet e tente novamente."
                e is java.io.IOException -> 
                    "Erro ao acessar os dados. Tente novamente em alguns instantes."
                else -> 
                    "Ocorreu um erro inesperado. Tente novamente mais tarde."
            }
            _syncStatus.value = SyncStatus.Failed(errorMessage)
        }
    }

    private suspend fun loadInitialHistory() {
        if (historyCache.isNotEmpty()) return
        try {
            val localHistory = localDataSource.getLocalHistory()
            cacheMutex.withLock {
                historyCache.clear()
                historyCache.putAll(localHistory.associateBy { it.contestNumber })
                sortedHistoryCache = null
            }
            syncHistory()
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Failed("Não foi possível carregar o histórico local.")
        }
    }
}