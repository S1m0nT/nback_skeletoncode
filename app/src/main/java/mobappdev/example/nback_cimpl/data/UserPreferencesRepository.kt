package mobappdev.example.nback_cimpl.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val HIGHSCORE = intPreferencesKey("highscore")
        val N_BACK_LEVEL = intPreferencesKey("n_back_level")
        val EVENT_INTERVAL = longPreferencesKey("event_interval")
        val TOTAL_EVENTS = intPreferencesKey("total_events")
        val GRID_SIZE = intPreferencesKey("grid_size")
        val AUDIO_NUMBERS = intPreferencesKey("audio_numbers")
        const val TAG = "UserPreferencesRepo"
    }

    val highscore: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[HIGHSCORE] ?: 0
        }

    val nBackLevelFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[N_BACK_LEVEL] ?: 1
        }

    val eventIntervalFlow: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[EVENT_INTERVAL] ?: 2000L
        }

    val totalEventsFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[TOTAL_EVENTS] ?: 10
        }

    val gridSizeFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[GRID_SIZE] ?: 3
        }

    val audioNumbersFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[AUDIO_NUMBERS] ?: 2
        }

    suspend fun saveHighScore(score: Int) {
        dataStore.edit { preferences ->
            preferences[HIGHSCORE] = score
        }
    }

    suspend fun saveSettings(nBackLevel: Int, eventInterval: Long, totalEvents: Int, gridSize: Int, audioNumbers: Int) {
        dataStore.edit { preferences ->
            preferences[N_BACK_LEVEL] = nBackLevel
            preferences[EVENT_INTERVAL] = eventInterval
            preferences[TOTAL_EVENTS] = totalEvents
            preferences[GRID_SIZE] = gridSize
            preferences[AUDIO_NUMBERS] = audioNumbers
        }
    }
}
