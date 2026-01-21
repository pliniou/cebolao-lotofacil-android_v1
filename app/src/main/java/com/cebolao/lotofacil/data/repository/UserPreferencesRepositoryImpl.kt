package com.cebolao.lotofacil.data.repository

import android.content.Context
import android.util.Log
import com.cebolao.lotofacil.BuildConfig
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cebolao.lotofacil.core.coroutine.DispatchersProvider
import com.cebolao.lotofacil.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchersProvider: DispatchersProvider
) : UserPreferencesRepository {

    companion object {
        private const val TAG = "UserPreferencesRepo"
        private val PINNED_GAMES_KEY = stringSetPreferencesKey("pinned_games")
        private val DYNAMIC_HISTORY_KEY = stringSetPreferencesKey("dynamic_history")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    override val pinnedGames: Flow<Set<String>> = context.dataStore.data
        .catch { exception ->
            handleError(exception, "reading pinned games")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[PINNED_GAMES_KEY] ?: emptySet()
        }

    override suspend fun savePinnedGames(games: Set<String>) {
        withContext(dispatchersProvider.io) {
            try {
                context.dataStore.edit { preferences ->
                    preferences[PINNED_GAMES_KEY] = games
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Saved ${games.size} pinned games")
                }
            } catch (e: IOException) {
                handleError(e, "saving pinned games")
            }
        }
    }

    override suspend fun getHistory(): Set<String> = withContext(dispatchersProvider.io) {
        try {
            context.dataStore.data
                .map { preferences ->
                    preferences[DYNAMIC_HISTORY_KEY] ?: emptySet()
                }
                .first()
        } catch (e: Exception) {
            handleError(e, "getting history")
            emptySet()
        }
    }

    override suspend fun addDynamicHistoryEntries(newHistoryEntries: Set<String>) {
        withContext(dispatchersProvider.io) {
            if (newHistoryEntries.isEmpty()) return@withContext
            try {
                context.dataStore.edit { preferences ->
                    val currentHistory = preferences[DYNAMIC_HISTORY_KEY] ?: emptySet()
                    val validEntries = newHistoryEntries.filter { entry ->
                        entry.matches(Regex("\\d+ - [\\d,]+"))
                    }.toSet()

                    if (validEntries.isNotEmpty()) {
                        preferences[DYNAMIC_HISTORY_KEY] = currentHistory + validEntries
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Added ${validEntries.size} valid history entries")
                        }
                    }
                }
            } catch (e: IOException) {
                handleError(e, "adding dynamic history entries")
            }
        }
    }

    override val themeMode: Flow<String> = context.dataStore.data
        .catch { exception ->
            handleError(exception, "reading theme mode")
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: "auto"
        }

    override suspend fun setThemeMode(mode: String) {
        withContext(dispatchersProvider.io) {
            try {
                context.dataStore.edit { preferences ->
                    preferences[THEME_MODE_KEY] = mode
                }
            } catch (e: IOException) {
                handleError(e, "setting theme mode")
            }
        }
    }

    private fun handleError(exception: Throwable, contextMessage: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Error $contextMessage", exception)
        }
    }
}
