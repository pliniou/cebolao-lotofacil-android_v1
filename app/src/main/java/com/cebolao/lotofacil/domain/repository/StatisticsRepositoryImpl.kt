package com.cebolao.lotofacil.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cebolao.lotofacil.domain.model.StatisticsReport
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.ObjectInputStream
import java.io.ByteArrayInputStream
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "statistics_cache")

@Singleton
class StatisticsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : StatisticsRepository {

    private val _cacheStatistics = MutableStateFlow(CacheStatistics())

    private val cacheMetadata = mutableMapOf<Int, CacheEntry>()
    
    data class CacheEntry(
        val data: StatisticsReport,
        val timestamp: Long,
        val ttlMs: Long
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > ttlMs
    }

    override suspend fun getCachedStatistics(windowSize: Int): StatisticsReport? {
        val entry = cacheMetadata[windowSize] ?: return null
        
        return if (!entry.isExpired()) {
            // Update hit statistics
            _cacheStatistics.value = _cacheStatistics.value.copy(
                cacheHits = _cacheStatistics.value.cacheHits + 1,
                lastUpdated = System.currentTimeMillis()
            )
            entry.data
        } else {
            // Remove expired entry and update miss statistics
            cacheMetadata.remove(windowSize)
            _cacheStatistics.value = _cacheStatistics.value.copy(
                cacheMisses = _cacheStatistics.value.cacheMisses + 1,
                validEntries = _cacheStatistics.value.validEntries - 1,
                expiredEntries = _cacheStatistics.value.expiredEntries + 1,
                lastUpdated = System.currentTimeMillis()
            )
            null
        }
    }

    override suspend fun cacheStatistics(
        windowSize: Int, 
        statistics: StatisticsReport, 
        ttlMs: Long
    ) {
        cacheMetadata[windowSize] = CacheEntry(
            data = statistics,
            timestamp = System.currentTimeMillis(),
            ttlMs = ttlMs
        )
        
        // Update cache statistics
        val currentStats = _cacheStatistics.value
        _cacheStatistics.value = currentStats.copy(
            totalEntries = cacheMetadata.size,
            validEntries = cacheMetadata.values.count { !it.isExpired() },
            expiredEntries = cacheMetadata.values.count { it.isExpired() },
            lastUpdated = System.currentTimeMillis()
        )
    }

    override suspend fun clearCache() {
        cacheMetadata.clear()
        _cacheStatistics.value = CacheStatistics(
            totalEntries = 0,
            validEntries = 0,
            expiredEntries = 0,
            lastUpdated = System.currentTimeMillis()
        )
    }

    override suspend fun hasValidCache(windowSize: Int): Boolean {
        val entry = cacheMetadata[windowSize] ?: return false
        return !entry.isExpired()
    }

    override fun getCacheStatistics(): Flow<CacheStatistics> {
        return _cacheStatistics.asStateFlow().map { stats ->
            stats.copy(
                totalEntries = cacheMetadata.size,
                validEntries = cacheMetadata.values.count { !it.isExpired() },
                expiredEntries = cacheMetadata.values.count { it.isExpired() }
            )
        }
    }
}
