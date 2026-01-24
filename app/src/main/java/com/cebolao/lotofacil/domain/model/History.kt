package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Represents a single Lotofácil draw.  Derived statistics (sum, evens,
 * primes, etc.) are computed eagerly in the constructor to avoid
 * recomputation and potential memory leaks.
 */
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
    val sum: Int = numbers.sum()
    val evens: Int = numbers.count { it % 2 == 0 }
    val odds: Int = 15 - evens
    val primes: Int = numbers.count { it in setOf(2, 3, 5, 7, 11, 13, 17, 19, 23) }
    val fibonacci: Int = numbers.count { it in setOf(1, 2, 3, 5, 8, 13, 21) }
    val frame: Int = numbers.count { it in setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25) }
    val portrait: Int = numbers.count { it in setOf(7, 8, 9, 12, 13, 14, 17, 18, 19) }
    val multiplesOf3: Int = numbers.count { it in setOf(3, 6, 9, 12, 15, 18, 21, 24) }
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
