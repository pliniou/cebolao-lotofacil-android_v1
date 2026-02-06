package com.cebolao.lotofacil.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.mock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserPreferencesRepositoryTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<androidx.datastore.preferences.core.Preferences>
    private lateinit var repository: UserPreferencesRepositoryImpl

    @Before
    fun setup() {
        val context = mock<Context>()
        val testDispatcher = StandardTestDispatcher()
        dataStore = PreferenceDataStoreFactory.create(
            corruptionHandler = null,
            migrations = emptyList(),
            scope = kotlinx.coroutines.CoroutineScope(testDispatcher),
            produceFile = {
                tempFolder.newFile("test_preferences")
            }
        )
        repository = UserPreferencesRepositoryImpl(dataStore)
    }

    @Test
    fun `getFilterPreferences should return default values when not set`() = runTest {
        val prefs = repository.getFilterPreferences()

        // Should return default values
        assertEquals(15, prefs.first().filters.size)
    }

    @Test
    fun `saveFilterPreferences should persist filter state`() = runTest {
        val mockFilters = listOf(
            com.cebolao.lotofacil.domain.model.FilterState(
                type = com.cebolao.lotofacil.domain.model.FilterType.WITHOUT_REPETITIVE,
                isEnabled = true,
                selectedRange = 0..0
            )
        )

        // Save preferences
        repository.saveFilterPreferences(mockFilters)

        // Retrieve and verify
        val prefs = repository.getFilterPreferences()
        assert(prefs.first().filters.isNotEmpty())
    }
}
