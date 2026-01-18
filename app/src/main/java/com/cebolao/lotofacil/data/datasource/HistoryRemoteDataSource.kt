package com.cebolao.lotofacil.data.datasource

import android.util.Log
import com.cebolao.lotofacil.BuildConfig
import com.cebolao.lotofacil.data.network.ApiService
import com.cebolao.lotofacil.data.network.LotofacilApiResult
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface HistoryRemoteDataSource {
    suspend fun getLatestDraw(): HistoricalDraw?
    suspend fun getDrawsInRange(range: IntRange): List<HistoricalDraw>
}

@Singleton
class HistoryRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : HistoryRemoteDataSource {

    companion object {
        private const val TAG = "HistoryRemoteDataSource"
        private const val BATCH_SIZE = 50
    }

    override suspend fun getLatestDraw(): HistoricalDraw? = withContext(Dispatchers.IO) {
        try {
            val result = apiService.getLatestResult()
            parseApiResult(result)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to fetch latest draw", e)
            }
            null
        }
    }

    override suspend fun getDrawsInRange(range: IntRange): List<HistoricalDraw> = withContext(Dispatchers.IO) {
        if (range.isEmpty()) return@withContext emptyList()

        coroutineScope {
            range.chunked(BATCH_SIZE).flatMap { batch ->
                batch.map { contestNumber ->
                    async {
                        try {
                            val result = apiService.getResultByContest(contestNumber)
                            parseApiResult(result)
                        } catch (e: Exception) {
                            if (BuildConfig.DEBUG) {
                                Log.w(TAG, "Failed to fetch contest $contestNumber", e)
                            }
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }
        }
    }

    private fun parseApiResult(apiResult: LotofacilApiResult): HistoricalDraw? {
        return try {
            val contest = apiResult.concurso ?: return null
            val numbers = apiResult.dezenas?.mapNotNull { it.toIntOrNull() }?.toSet()

            if (numbers != null && numbers.size >= 15) {
                HistoricalDraw(
                    contestNumber = contest,
                    numbers = numbers,
                    date = apiResult.dataApuracao,
                    prizes = apiResult.premiacoes?.map { 
                        com.cebolao.lotofacil.domain.model.PrizeTier(it.descricao, it.ganhadores, it.valorPremio)
                    } ?: emptyList(),
                    winners = apiResult.localGanhadores?.map { 
                        com.cebolao.lotofacil.domain.model.WinnerLocation(it.ganhadores, it.municipio ?: "", it.uf ?: "")
                    } ?: emptyList(),
                    nextContest = apiResult.proximoConcurso,
                    nextDate = apiResult.dataProximoConcurso,
                    nextEstimate = apiResult.valorEstimadoProximoConcurso,
                    accumulated = apiResult.acumulou ?: false
                )
            } else {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "Invalid API result for contest $contest: insufficient numbers or missing data")
                }
                null
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Failed to parse API result", e)
            }
            null
        }
    }
}
