package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.domain.model.StatisticsReport
import com.cebolao.lotofacil.domain.repository.CacheStatistics
import com.cebolao.lotofacil.domain.repository.StatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepositoryImpl @Inject constructor() : StatisticsRepository {

    private val cacheMutex = Mutex()
    private val cacheMetadata = mutableMapOf<Int, CacheEntry>()
    private val cacheStats = MutableStateFlow(CacheStatistics())

    data class CacheEntry(
        val data: StatisticsReport,
        val timestamp: Long,
        val ttlMs: Long
    ) {
        fun isExpired(now: Long): Boolean = now - timestamp > ttlMs
    }

    override suspend fun getCachedStatistics(windowSize: Int): StatisticsReport? = cacheMutex.withLock {
        val now = System.currentTimeMillis()
        val entry = cacheMetadata[windowSize] ?: run {
            cacheStats.value = cacheStats.value.copy(
                cacheMisses = cacheStats.value.cacheMisses + 1,
                lastUpdated = now
            )
            updateEntryCountersLocked(now)
            return null
        }

        return if (!entry.isExpired(now)) {
            cacheStats.value = cacheStats.value.copy(
                cacheHits = cacheStats.value.cacheHits + 1,
                lastUpdated = now
            )
            updateEntryCountersLocked(now)
            entry.data
        } else {
            cacheMetadata.remove(windowSize)
            cacheStats.value = cacheStats.value.copy(
                cacheMisses = cacheStats.value.cacheMisses + 1,
                lastUpdated = now
            )
            updateEntryCountersLocked(now)
            null
        }
    }

    override suspend fun cacheStatistics(windowSize: Int, statistics: StatisticsReport, ttlMs: Long) {
        cacheMutex.withLock {
            cacheMetadata[windowSize] = CacheEntry(
                data = statistics,
                timestamp = System.currentTimeMillis(),
                ttlMs = ttlMs
            )
            val now = System.currentTimeMillis()
            cacheStats.value = cacheStats.value.copy(lastUpdated = now)
            updateEntryCountersLocked(now)
        }
    }

    override suspend fun clearCache() {
        cacheMutex.withLock {
            cacheMetadata.clear()
            val now = System.currentTimeMillis()
            cacheStats.value = CacheStatistics(lastUpdated = now)
            updateEntryCountersLocked(now)
        }
    }

    override suspend fun hasValidCache(windowSize: Int): Boolean = cacheMutex.withLock {
        val now = System.currentTimeMillis()
        val entry = cacheMetadata[windowSize] ?: return false
        !entry.isExpired(now)
    }

    override fun getCacheStatistics(): Flow<CacheStatistics> {
        return cacheStats.asStateFlow()
    }

    private fun updateEntryCountersLocked(now: Long) {
        val entries = cacheMetadata.values
        cacheStats.value = cacheStats.value.copy(
            totalEntries = entries.size,
            validEntries = entries.count { !it.isExpired(now) },
            expiredEntries = entries.count { it.isExpired(now) },
            lastUpdated = now
        )
    }
}
