package com.cebolao.lotofacil.domain.service

import com.cebolao.lotofacil.domain.model.FilterState
import com.cebolao.lotofacil.domain.model.FilterType
import com.cebolao.lotofacil.domain.model.LotofacilGame
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
        val uniqueGames = mutableSetOf<Set<Int>>()
        val resultList = mutableListOf<LotofacilGame>()
        var attempts = 0

        val enabledFilters = activeFilters.filter { it.isEnabled }

        while (resultList.size < count && attempts < maxAttempts) {
            val numbers = generateRandomNumbers()
            if (uniqueGames.add(numbers)) {
                val game = LotofacilGame(numbers)
                if (isGameValid(game, enabledFilters, lastDraw)) {
                    resultList.add(game)
                }
            }
            attempts++
        }

        if (resultList.size < count) {
            throw GameGenerationException("Não foi possível gerar a quantidade de jogos desejada com os filtros atuais. Tente configurações menos restritivas.")
        }

        return@withContext resultList
    }

    private fun generateRandomNumbers(): Set<Int> {
        // Optimized selection: Fisher-Yates shuffle variant for small pool
        val pool = allNumbers.toMutableList()
        val selection = mutableSetOf<Int>()
        repeat(15) {
            val index = random.nextInt(pool.size)
            selection.add(pool.removeAt(index))
        }
        return selection
    }

    private fun isGameValid(
        game: LotofacilGame,
        enabledFilters: List<FilterState>,
        lastDraw: Set<Int>?
    ): Boolean {
        return enabledFilters.all { filter ->
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
            filter.containsValue(value)
        }
    }
}

