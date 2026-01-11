package com.cebolao.lotofacil.data.datasource

import android.content.Context
import android.util.Log
import com.cebolao.lotofacil.data.HistoricalDraw
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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
    private val userPreferencesRepository: UserPreferencesRepository
) : HistoryLocalDataSource {

    private val lineRegex = """^\d+\s*-\s*[\d, ]+$""".toRegex()
    private val historyFileName = "lotofacil_resultados.txt"

    companion object {
        private const val TAG = "HistoryLocalDataSource"
    }

    override suspend fun getLocalHistory(): List<HistoricalDraw> = withContext(Dispatchers.IO) {
        val assetHistory = parseHistoryFromAssets()
        val savedHistoryStrings = userPreferencesRepository.getHistory()
        val savedHistory = savedHistoryStrings.mapNotNull { parseLine(it) }

        // Combina e deduplica, dando preferência a dados mais completos (com data) se houver conflito
        val allDraws = (assetHistory + savedHistory)
            .groupBy { it.contestNumber }
            .mapValues { (_, draws) -> draws.maxByOrNull { it.date?.isNotEmpty() ?: false } ?: draws.first() }
            .values
            .sortedByDescending { it.contestNumber }

        Log.d(TAG, "Loaded ${allDraws.size} contests from local sources (Assets: ${assetHistory.size}, DataStore: ${savedHistory.size})")
        allDraws
    }

    override suspend fun saveNewContests(newDraws: List<HistoricalDraw>) {
        if (newDraws.isEmpty()) return

        val newHistoryEntries = newDraws.map { draw ->
            // Formato padronizado: "CONCURSO - NUM1,NUM2,..."
            "${draw.contestNumber} - ${draw.numbers.sorted().joinToString(",")}"
        }.toSet()

        userPreferencesRepository.addDynamicHistoryEntries(newHistoryEntries)
        Log.d(TAG, "Persisted ${newDraws.size} new contests locally.")
    }

    private suspend fun parseHistoryFromAssets(): List<HistoricalDraw> = withContext(Dispatchers.IO) {
        try {
            context.assets.open(historyFileName).bufferedReader().use { reader ->
                reader.lineSequence()
                    .filter { it.isNotBlank() && it.matches(lineRegex) }
                    .mapNotNull { line -> parseLine(line) }
                    .toList()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read history file from assets: $historyFileName", e)
            emptyList()
        }
    }

    private fun parseLine(line: String): HistoricalDraw? {
        return try {
            val parts = line.split(" - ", limit = 2)
            if (parts.size != 2) {
                Log.w(TAG, "Invalid line format (missing ' - ' separator): $line")
                return null
            }

            val contestNumber = parts[0].trim().toInt()
            val numbers = parts[1].split(",")
                .map { it.trim().toInt() }
                .toSet()

            if (numbers.size >= 15 && contestNumber > 0) {
                HistoricalDraw(contestNumber, numbers)
            } else {
                Log.w(TAG, "Parsed line with invalid data: contest=$contestNumber, numbers=${numbers.size}")
                null
            }
        } catch (e: NumberFormatException) {
            Log.w(TAG, "Failed to parse numbers in line: $line", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred while parsing line: $line", e)
            null
        }
    }
}