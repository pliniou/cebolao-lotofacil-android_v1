package com.cebolao.lotofacil.domain.model

import android.annotation.SuppressLint
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Representa um único jogo da Lotofácil.
 * Otimizada para performance no Jetpack Compose com @Immutable.
 */
@SuppressLint("UnsafeOptInUsageError")
@Immutable
@Serializable
data class LotofacilGame(
    val numbers: Set<Int>,
    val isPinned: Boolean = false,
    val creationTimestamp: Long = System.currentTimeMillis(),
    val id: String = "${creationTimestamp}_${numbers.hashCode()}"
) {
    init {
        require(numbers.size == 15) { "Um jogo deve ter 15 números." }
        require(numbers.all { it in 1..25 }) { "Números inválidos encontrados." }
    }

    // Propriedades computadas para análise estatística
    val sum: Int by lazy { numbers.sum() }
    val evens: Int by lazy { numbers.count { it % 2 == 0 } }
    val odds: Int by lazy { 15 - evens }
    val primes: Int by lazy { numbers.count { it in setOf(2, 3, 5, 7, 11, 13, 17, 19, 23) } }
    val frame: Int by lazy { numbers.count { it in setOf(1, 2, 3, 4, 5, 6, 10, 11, 15, 16, 20, 21, 22, 23, 24, 25) } }
    val portrait: Int by lazy { numbers.count { it in setOf(7, 8, 9, 12, 13, 14, 17, 18, 19) } }
    val fibonacci: Int by lazy { numbers.count { it in setOf(1, 2, 3, 5, 8, 13, 21) } }
    val multiplesOf3: Int by lazy { numbers.count { it in setOf(3, 6, 9, 12, 15, 18, 21, 24) } }

    /** Calcula quantos números deste jogo se repetiram do sorteio anterior. */
    fun repeatedFrom(lastDraw: Set<Int>?): Int {
        return lastDraw?.let { numbers.intersect(it).size } ?: 0
    }

    /** Converte o jogo para uma string compacta para armazenamento eficiente. */
    fun toCompactString(): String {
        return "${numbers.sorted().joinToString(",")}|$isPinned|$creationTimestamp|$id"
    }

    companion object {
        /** Cria um LotofacilGame a partir de uma string compacta. */
        fun fromCompactString(compactString: String): LotofacilGame? {
            return try {
                val parts = compactString.split("|")
                val numbers = parts[0].split(",").map { it.toInt() }.toSet()
                val isPinned = parts.getOrNull(1)?.toBoolean() ?: false
                val timestamp = parts.getOrNull(2)?.toLong() ?: System.currentTimeMillis()
                val id = parts.getOrNull(3) ?: "${timestamp}_${numbers.hashCode()}"
                if (numbers.size == 15) {
                    LotofacilGame(numbers, isPinned, timestamp, id)
                } else null
            } catch (_: Exception) {
                null
            }
        }
    }
}
