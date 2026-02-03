package com.cebolao.lotofacil.domain.model

import java.math.BigDecimal

/**
 * Centraliza todas as constantes relacionadas à lógica do jogo Lotofácil.
 * Isso evita a duplicação de "números mágicos" e facilita a manutenção.
 */
object LotofacilConstants {
    const val GAME_SIZE = 15
    val GAME_COST: BigDecimal = BigDecimal("3.50")
    val VALID_NUMBER_RANGE = 1..25
    val ALL_NUMBERS: Set<Int> = VALID_NUMBER_RANGE.toSet()

    // Constantes para análise estatística

}
