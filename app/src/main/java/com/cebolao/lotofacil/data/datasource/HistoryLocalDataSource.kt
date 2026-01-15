package com.cebolao.lotofacil.data.datasource

import android.content.Context
import android.util.Log
import com.cebolao.lotofacil.data.HistoricalDraw
import com.cebolao.lotofacil.data.LotofacilConstants
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val cacheMutex = Mutex()
    private var cachedHistory: List<HistoricalDraw>? = null

    companion object {
        private const val TAG = "HistoryLocalDataSource"
    }

    override suspend fun getLocalHistory(): List<HistoricalDraw> = withContext(Dispatchers.IO) {
        cachedHistory?.let { return@withContext it }

        val assetHistory = parseHistoryFromAssets()
        val savedHistoryStrings = userPreferencesRepository.getHistory()
        val savedHistory = savedHistoryStrings.mapNotNull { parseLine(it) }

        cacheMutex.withLock {
            cachedHistory?.let { return@withContext it }
            val allDraws = mergeHistory(assetHistory, savedHistory)
            cachedHistory = allDraws
            allDraws
        }
    }

    override suspend fun saveNewContests(newDraws: List<HistoricalDraw>) {
        if (newDraws.isEmpty()) return

        cacheMutex.withLock {
            cachedHistory = null
        }

        val newHistoryEntries = newDraws.map { draw ->
            "${draw.contestNumber} - ${draw.numbers.sorted().joinToString(",")}"
        }.toSet()

        userPreferencesRepository.addDynamicHistoryEntries(newHistoryEntries)
        Log.d(TAG, "Persisted ${newDraws.size} new contests locally.")
    }

    private fun mergeHistory(
        assetHistory: List<HistoricalDraw>,
        savedHistory: List<HistoricalDraw>
    ): List<HistoricalDraw> {
        Log.d(
            TAG,
            "Loaded ${assetHistory.size} contests from assets and ${savedHistory.size} from DataStore"
        )
        return (assetHistory + savedHistory)
            .groupBy { it.contestNumber }
            .mapValues { (_, draws) -> draws.maxByOrNull { it.date?.isNotEmpty() ?: false } ?: draws.first() }
            .values
            .sortedByDescending { it.contestNumber }
    }

    private fun parseHistoryFromAssets(): List<HistoricalDraw> {
        return try {
            context.assets.open(historyFileName).bufferedReader().use { reader ->
                reader.lineSequence()
                    .filter { it.isNotBlank() && it.matches(lineRegex) }
                    .mapNotNull { parseLine(it) }
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
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()

            val validNumbers = numbers.filter { it in LotofacilConstants.VALID_NUMBER_RANGE }.toSet()
            if (contestNumber > 0 && validNumbers.size == LotofacilConstants.GAME_SIZE) {
                HistoricalDraw(contestNumber, validNumbers)
            } else {
                Log.w(
                    TAG,
                    "Parsed line with invalid data: contest=$contestNumber, uniqueNumbers=${validNumbers.size}, values=${numbers}"
                )
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
