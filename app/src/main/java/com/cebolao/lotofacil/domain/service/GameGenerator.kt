package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.data.FilterState
import com.cebolao.lotofacil.data.FilterType
import com.cebolao.lotofacil.data.LotofacilGame
import com.cebolao.lotofacil.di.DefaultDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

class GameGenerationException(message: String) : Exception(message)

@Singleton
class GameGenerator @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val random: Random = Random()
) {
    private val allNumbers = (1..25).toList()

    suspend fun generateGames(
        activeFilters: List<FilterState>,
        count: Int,
        lastDraw: Set<Int>? = null,
        maxAttempts: Int = 250_000
    ): List<LotofacilGame> = withContext(defaultDispatcher) {
        val uniqueGames = linkedMapOf<Set<Int>, LotofacilGame>()
        var attempts = 0

        while (uniqueGames.size < count && attempts < maxAttempts) {
            val game = generateRandomGame()
            if (isGameValid(game, activeFilters, lastDraw)) {
                uniqueGames.putIfAbsent(game.numbers.toSet(), game)
            }
            attempts++
        }

        if (uniqueGames.size < count) {
            throw GameGenerationException("Não foi possível gerar a quantidade de jogos desejada com os filtros atuais. Tente configurações menos restritivas.")
        }

        return@withContext uniqueGames.values.toList()
    }

    private fun generateRandomGame(): LotofacilGame {
        val selectedNumbers = allNumbers.shuffled(random).take(15).toSet()
        return LotofacilGame(selectedNumbers)
    }

    private fun isGameValid(
        game: LotofacilGame,
        activeFilters: List<FilterState>,
        lastDraw: Set<Int>?
    ): Boolean {
        for (filter in activeFilters.filter { it.isEnabled }) {
            val value = when (filter.type) {
                FilterType.SOMA_DEZENAS -> game.sum
                FilterType.PARES -> game.evens
                FilterType.PRIMOS -> game.primes
                FilterType.MOLDURA -> game.frame
                FilterType.RETRATO -> game.portrait
                FilterType.FIBONACCI -> game.fibonacci
                FilterType.MULTIPLOS_DE_3 -> game.multiplesOf3
                FilterType.REPETIDAS_CONCURSO_ANTERIOR -> game.repeatedFrom(lastDraw)
            }
            if (!filter.containsValue(value)) {
                return false
            }
        }
        return true
    }
}
