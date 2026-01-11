package com.cebolao.lotofacil.domain.model

import kotlinx.collections.immutable.ImmutableSet

/**
 * Represents summary statistics for a single lottery draw.  Unlike the UI layer,
 * the domain model does not depend on Compose-specific annotations so that it
 * remains framework-agnostic.  The use of [ImmutableSet] ensures immutability
 * for the collection of drawn numbers.
 */
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
