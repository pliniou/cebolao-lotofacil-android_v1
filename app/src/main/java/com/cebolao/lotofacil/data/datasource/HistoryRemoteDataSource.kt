package com.cebolao.lotofacil.data.datasource

import android.util.Log
import com.cebolao.lotofacil.BuildConfig
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.data.network.ApiService
import com.cebolao.lotofacil.data.network.toHistoricalDraw
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface HistoryRemoteDataSource {
    suspend fun getLatestDraw(): HistoricalDraw?
    suspend fun getDrawsInRange(range: IntRange): List<HistoricalDraw>
}

@Singleton
class HistoryRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
    private val dispatchersProvider: DispatchersProvider
) : HistoryRemoteDataSource {

    companion object {
        private const val TAG = "HistoryRemoteDataSource"
        private const val BATCH_SIZE = 50
        private const val MAX_CONCURRENT_REQUESTS = 8
        private const val RETRY_ATTEMPTS = 2
        private const val RETRY_DELAY_MS = 250L
    }

    // Global semaphore to limit concurrent network requests
    private val networkSemaphore = Semaphore(MAX_CONCURRENT_REQUESTS)

    override suspend fun getLatestDraw(): HistoricalDraw? = withContext(dispatchersProvider.io) {
        try {
            val result = retry { apiService.getLatestResult() }
            result.toHistoricalDraw()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to fetch latest draw", e)
            }
            null
        }
    }

    override suspend fun getDrawsInRange(range: IntRange): List<HistoricalDraw> =
        withContext(dispatchersProvider.io) {
        if (range.isEmpty()) return@withContext emptyList()

        coroutineScope {
            range.chunked(BATCH_SIZE).flatMap { batch ->
                batch.chunked(MAX_CONCURRENT_REQUESTS).flatMap { window ->
                    window.map { contestNumber ->
                        async {
                            networkSemaphore.withPermit {
                                fetchContest(contestNumber)
                            }
                        }
                    }.awaitAll().filterNotNull()
                }
            }
        }
    }

    private suspend fun fetchContest(contestNumber: Int): HistoricalDraw? {
        return try {
            val result = retry { apiService.getResultByContest(contestNumber) }
            result.toHistoricalDraw()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Failed to fetch contest $contestNumber", e)
            }
            null
        }
    }

    private suspend fun <T> retry(
        attempts: Int = RETRY_ATTEMPTS,
        block: suspend () -> T
    ): T {
        var lastError: Throwable? = null
        repeat(attempts) { attempt ->
            try {
                return block()
            } catch (t: Throwable) {
                lastError = t
                if (attempt < attempts - 1) {
                    delay(RETRY_DELAY_MS * (attempt + 1))
                }
            }
        }
        throw lastError ?: IllegalStateException("Retry failed without exception")
    }
}
