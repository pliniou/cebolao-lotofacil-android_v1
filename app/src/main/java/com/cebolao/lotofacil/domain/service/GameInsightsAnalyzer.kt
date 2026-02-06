package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.domain.model.HistoricalDraw
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameInsightsAnalyzer @Inject constructor() {

    fun getNumberFrequencies(draws: List<HistoricalDraw>): Map<Int, Int> {
        val frequencies = IntArray(26)
        draws.forEach { draw ->
            draw.numbers.forEach { number ->
                if (number in 1..25) {
                    frequencies[number]++
                }
            }
        }
        return (1..25).associateWith { frequencies[it] }
    }

    fun getTopNumbers(frequencies: Map<Int, Int>, count: Int = 5): List<Int> {
        return frequencies.entries
            .sortedByDescending { it.value }
            .take(count)
            .map { it.key }
            .sorted()
    }

    fun getOverdueNumbers(draws: List<HistoricalDraw>): List<Pair<Int, Int>> {
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

        return (1..25).map { number ->
            val lastSeen = lastSeenMap[number]
            val overdue = if (lastSeen > 0) {
                (lastContestNumber - lastSeen).coerceAtLeast(0)
            } else {
                draws.size // Assume it's been missing since the beginning
            }
            number to overdue
        }.sortedByDescending { it.second }
    }

    /**
     * Detects common combinations of numbers (pairs, triplets, etc.) that appear most frequently.
     * @param draws List of draws to analyze.
     * @param size Size of the combination (2 for pairs, 3 for triplets).
     * @param limit Maximum number of patterns to return.
     */
    fun getCommonPatterns(
        draws: List<HistoricalDraw>,
        size: Int,
        limit: Int = 10
    ): List<Pair<Set<Int>, Int>> {
        if (draws.isEmpty()) return emptyList()

        val patternCounts = mutableMapOf<Set<Int>, Int>()

        draws.forEach { draw ->
            val numbersList = draw.numbers.toList().sorted()
            generateCombinations(numbersList, size).forEach { combination ->
                patternCounts[combination] = patternCounts.getOrDefault(combination, 0) + 1
            }
        }

        return patternCounts.entries
            .sortedByDescending { it.value }
            .take(limit)
            .map { it.key to it.value }
    }

    private fun generateCombinations(numbers: List<Int>, size: Int): List<Set<Int>> {
        val combinations = mutableListOf<Set<Int>>()
        
        fun combine(start: Int, current: MutableList<Int>) {
            if (current.size == size) {
                combinations.add(current.toSet())
                return
            }
            for (i in start until numbers.size) {
                current.add(numbers[i])
                combine(i + 1, current)
                current.removeAt(current.size - 1)
            }
        }
        
        combine(0, mutableListOf())
        return combinations
    }
}
