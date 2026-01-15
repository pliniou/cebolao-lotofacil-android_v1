package com.cebolao.lotofacil.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableSet

/**
 * Represents summary statistics for a single lottery draw.
 * Uses @Immutable for Compose optimization.
 * The use of [ImmutableSet] ensures immutability for the collection of drawn numbers.
 */
@Immutable
data class LastDrawStats(
    val contest: Int,
    val numbers: ImmutableSet<Int>,
    val sum: Int,
    val evens: Int,
    val odds: Int,
    val primes: Int,
    val frame: Int,
    val portrait: Int,
    val fibonacci: Int,
    val multiplesOf3: Int
)
