package com.cebolao.lotofacil.domain.model

enum class GameStatisticType {
    SUM,
    EVENS,
    ODDS,
    PRIMES,
    FIBONACCI,
    FRAME,
    PORTRAIT,
    MULTIPLES_OF_3
}

data class GameStatistic(
    val type: GameStatisticType,
    val value: Int
)
