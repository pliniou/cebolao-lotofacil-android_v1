package com.cebolao.lotofacil.data.datasource

import android.content.Context
import android.util.Log
import com.cebolao.lotofacil.BuildConfig
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.data.datasource.database.HistoryDao
import com.cebolao.lotofacil.data.datasource.database.entity.toDomain
import com.cebolao.lotofacil.data.datasource.database.entity.toEntity
import com.cebolao.lotofacil.data.parser.HistoryParser
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface HistoryLocalDataSource {
    suspend fun getLocalHistory(): List<HistoricalDraw>
    suspend fun saveNewContests(newDraws: List<HistoricalDraw>)
}

@Singleton
class HistoryLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val historyDao: HistoryDao,
    private val dispatchersProvider: DispatchersProvider,
    private val parser: HistoryParser
) : HistoryLocalDataSource {

    private val historyFileName = "lotofacil_resultados.txt"

    override suspend fun getLocalHistory(): List<HistoricalDraw> =
        withContext(dispatchersProvider.io) {
            val dbCount = historyDao.getCount()
            if (dbCount == 0) {
                val assetDraws = parseHistoryFromAssets()
                if (assetDraws.isNotEmpty()) {
                    historyDao.insertAll(assetDraws.map { it.toEntity() })
                }
                assetDraws
            } else {
                historyDao.getAll().map { it.toDomain() }
            }
        }

    override suspend fun saveNewContests(newDraws: List<HistoricalDraw>) {
        if (newDraws.isEmpty()) return
        withContext(dispatchersProvider.io) {
            historyDao.upsertAll(newDraws.map { it.toEntity() })
            if (BuildConfig.DEBUG) {
                Log.d("HistoryLocalDataSource", "Persisted ${newDraws.size} new contests locally.")
            }
        }
    }

    private fun parseHistoryFromAssets(): List<HistoricalDraw> {
        return try {
            context.assets.open(historyFileName).bufferedReader().use { reader ->
                parser.parse(reader.lineSequence())
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                Log.e("HistoryLocalDataSource", "Failed to read history file", e)
            }
            emptyList()
        }
    }
}

