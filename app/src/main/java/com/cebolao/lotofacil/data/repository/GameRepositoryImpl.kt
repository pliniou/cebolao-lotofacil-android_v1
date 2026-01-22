package com.cebolao.lotofacil.data.repository

import com.cebolao.lotofacil.domain.model.LotofacilGame
import com.cebolao.lotofacil.di.ApplicationScope
import com.cebolao.lotofacil.domain.repository.GameRepository
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationScope private val repositoryScope: CoroutineScope
) : GameRepository {

    private val gamesMutex = Mutex()
    private val _games = MutableStateFlow<ImmutableList<LotofacilGame>>(persistentListOf())
    override val games: StateFlow<ImmutableList<LotofacilGame>> = _games.asStateFlow()

    override val pinnedGames: StateFlow<ImmutableList<LotofacilGame>> = games
        .map { gamesList -> gamesList.filter { it.isPinned }.toImmutableList() }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly, // Change to Eagerly for faster first-load UI
            initialValue = persistentListOf()
        )

    init {
        repositoryScope.launch {
            userPreferencesRepository.pinnedGames.first().let { pinnedGameStrings ->
                val loadedGames = pinnedGameStrings.mapNotNull { LotofacilGame.fromCompactString(it) }
                _games.update { (it + loadedGames).distinctBy { g -> g.id }.toImmutableList() }
            }
        }
    }

    override suspend fun addGeneratedGames(newGames: List<LotofacilGame>) {
        gamesMutex.withLock {
            _games.update { currentGames ->
                val currentPinned = currentGames.filter { it.isPinned }
                (currentPinned + newGames)
                    .distinctBy { it.numbers } // Keep uniqueness by numbers for gameplay
                    .sortedWith(
                        compareBy<LotofacilGame> { !it.isPinned }
                            .thenByDescending { it.creationTimestamp }
                    )
                    .toImmutableList()
            }
        }
    }

    override suspend fun clearUnpinnedGames() {
        _games.update { currentGames ->
            currentGames.filter { it.isPinned }.toImmutableList()
        }
    }

    override suspend fun togglePinState(gameToToggle: LotofacilGame) {
        val updatedGame = gameToToggle.copy(isPinned = !gameToToggle.isPinned)
        gamesMutex.withLock {
            _games.update { currentGames ->
                currentGames
                    .map { if (it.id == updatedGame.id) updatedGame else it }
                    .toImmutableList()
            }
        }
        persistPinnedGames()
    }

    override suspend fun deleteGame(gameToDelete: LotofacilGame) {
        gamesMutex.withLock {
            _games.update { currentGames ->
                currentGames.filterNot { it.id == gameToDelete.id }.toImmutableList()
            }
        }
        if (gameToDelete.isPinned) {
            persistPinnedGames()
        }
    }

    private suspend fun persistPinnedGames() {
        val pinned = _games.value.filter { it.isPinned }
        userPreferencesRepository.savePinnedGames(pinned.map { it.toCompactString() }.toSet())
    }
}
