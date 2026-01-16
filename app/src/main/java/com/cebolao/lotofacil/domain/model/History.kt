package com.cebolao.lotofacil.domain.model

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Immutable
@Serializable
data class HistoricalDraw(
    val contestNumber: Int,
    val numbers: Set<Int>,
    val date: String? = null,
    val prizes: List<PrizeTier> = emptyList(),
    val winners: List<WinnerLocation> = emptyList(),
    val nextContest: Int? = null,
    val nextDate: String? = null,
    val nextEstimate: Double? = null,
    val accumulated: Boolean = false
) {
    val sum: Int by lazy { numbers.sum() }
    val evens: Int by lazy { numbers.count { it % 2 == 0 } }
    val odds: Int by lazy { 15 - evens }
    val primes: Int by lazy { numbers.count { it in setOf(2, 3, 5, 7, 11, 13, 17, 19, 23) } }
    val fibonacci: Int by lazy { numbers.count { it in setOf(1, 2, 3, 5, 8, 13, 21) } }
    val frame: Int by lazy { numbers.count { it in setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25) } }
    val portrait: Int by lazy { numbers.count { it in setOf(7, 8, 9, 12, 13, 14, 17, 18, 19) } }
    val multiplesOf3: Int by lazy { numbers.count { it in setOf(3, 6, 9, 12, 15, 18, 21, 24) } }

    companion object
}

@Immutable
@Serializable
data class PrizeTier(
    val description: String,
    val winners: Int,
    val prizeValue: Double
)

@Immutable
@Serializable
data class WinnerLocation(
    val winnersCount: Int,
    val city: String,
    val state: String
)

@Immutable
data class CheckResult(
    val scoreCounts: ImmutableMap<Int, Int> = persistentMapOf(),
    val lastHitContest: Int?,
    val lastHitScore: Int?,
    val lastCheckedContest: Int,
    val recentHits: ImmutableList<Pair<Int, Int>> = persistentListOf()
)
