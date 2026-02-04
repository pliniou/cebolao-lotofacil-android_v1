package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.model.HistoricalDraw
import com.cebolao.lotofacil.domain.model.StatisticsReport
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsAnalyzer @Inject constructor(
    private val dispatchersProvider: DispatchersProvider
) {

    companion object {
        private const val TOP_NUMBERS_COUNT = 5
    }

    suspend fun analyze(draws: List<HistoricalDraw>): StatisticsReport =
        withContext(dispatchersProvider.io) {
        if (draws.isEmpty()) return@withContext StatisticsReport()

        coroutineScope {
            val mostFrequentDeferred = async { calculateMostFrequent(draws) }
            val mostOverdueDeferred = async { calculateMostOverdue(draws) }
            val distributionsDeferred = async { calculateAllDistributions(draws) }
            val averageSumDeferred = async { calculateAverageSum(draws) }

            val distributions = distributionsDeferred.await()

            StatisticsReport(
                mostFrequentNumbers = mostFrequentDeferred.await(),
                mostOverdueNumbers = mostOverdueDeferred.await(),
                evenDistribution = distributions.evenDistribution,
                primeDistribution = distributions.primeDistribution,
                frameDistribution = distributions.frameDistribution,
                portraitDistribution = distributions.portraitDistribution,
                fibonacciDistribution = distributions.fibonacciDistribution,
                multiplesOf3Distribution = distributions.multiplesOf3Distribution,
                sumDistribution = distributions.sumDistribution,
                averageSum = averageSumDeferred.await(),
                totalDrawsAnalyzed = draws.size,
                analysisDate = System.currentTimeMillis()
            )
        }
    }

    private fun calculateMostFrequent(draws: List<HistoricalDraw>): List<Pair<Int, Int>> {
        val frequencies = IntArray(26)
        draws.forEach { draw ->
            draw.numbers.forEach { number ->
                if (number in 1..25) frequencies[number]++
            }
        }
        return (1..25).asSequence()
            .map { it to frequencies[it] }
            .sortedByDescending { it.second }
            .take(TOP_NUMBERS_COUNT)
            .toList()
    }

    private fun calculateMostOverdue(draws: List<HistoricalDraw>): List<Pair<Int, Int>> {
        if (draws.isEmpty()) return emptyList()

        val lastContestNumber = draws.first().contestNumber
        val lastSeenMap = IntArray(26)

        draws.forEach { draw ->
            draw.numbers.forEach { number ->
                if (number in 1..25 && lastSeenMap[number] == 0) {
                    lastSeenMap[number] = draw.contestNumber
                }
            }
        }

        return (1..25).asSequence()
            .map { number ->
                val lastSeen = lastSeenMap[number]
                val overdue = if (lastSeen > 0) {
                    (lastContestNumber - lastSeen).coerceAtLeast(1)
                } else {
                    draws.size + 1
                }
                number to overdue
            }
            .sortedByDescending { it.second }
            .take(TOP_NUMBERS_COUNT)
            .toList()
    }

    private suspend fun calculateAllDistributions(draws: List<HistoricalDraw>): DistributionResults = coroutineScope {
        val even = async { calculateDistribution(draws) { it.evens } }
        val prime = async { calculateDistribution(draws) { it.primes } }
        val frame = async { calculateDistribution(draws) { it.frame } }
        val portrait = async { calculateDistribution(draws) { it.portrait } }
        val fibonacci = async { calculateDistribution(draws) { it.fibonacci } }
        val multiplesOf3 = async { calculateDistribution(draws) { it.multiplesOf3 } }
        val sum = async { calculateDistribution(draws, 10) { it.sum } }

        DistributionResults(
            evenDistribution = even.await(),
            primeDistribution = prime.await(),
            frameDistribution = frame.await(),
            portraitDistribution = portrait.await(),
            fibonacciDistribution = fibonacci.await(),
            multiplesOf3Distribution = multiplesOf3.await(),
            sumDistribution = sum.await()
        )
    }

    private fun calculateDistribution(
        draws: List<HistoricalDraw>,
        grouping: Int = 1,
        valueExtractor: (HistoricalDraw) -> Int
    ): Map<Int, Int> {
        return draws.groupingBy { draw ->
            (valueExtractor(draw) / grouping) * grouping
        }.eachCount()
    }

    private fun calculateAverageSum(draws: List<HistoricalDraw>): Float {
        if (draws.isEmpty()) return 0f
        return draws.map { it.sum }.average().toFloat()
    }

    private data class DistributionResults(
        val evenDistribution: Map<Int, Int>,
        val primeDistribution: Map<Int, Int>,
        val frameDistribution: Map<Int, Int>,
        val portraitDistribution: Map<Int, Int>,
        val fibonacciDistribution: Map<Int, Int>,
        val multiplesOf3Distribution: Map<Int, Int>,
        val sumDistribution: Map<Int, Int>
    )
}
