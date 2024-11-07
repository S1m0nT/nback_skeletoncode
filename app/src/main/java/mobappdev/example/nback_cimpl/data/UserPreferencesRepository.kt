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

/**
 * This repository provides a way to interact with the DataStore api,
 * with this API you can save key:value pairs
 *
 * Currently this file contains only one thing: getting the highscore as a flow
 * and writing to the highscore preference.
 * (a flow is like a waterpipe; if you put something different in the start,
 * the end automatically updates as long as the pipe is open)
 *
 * Date: 25-08-2023
 * Version: Skeleton code version 1.0
 * Author: Yeetivity
 *
 */

class UserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val HIGHSCORE = intPreferencesKey("highscore")
        val N_BACK_LEVEL = intPreferencesKey("n_back_level")
        val EVENT_INTERVAL = longPreferencesKey("event_interval")
        val TOTAL_EVENTS = intPreferencesKey("total_events")
        val GRID_SIZE = intPreferencesKey("grid_size")
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

    suspend fun saveHighScore(score: Int) {
        dataStore.edit { preferences ->
            preferences[HIGHSCORE] = score
        }
    }

    suspend fun saveSettings(nBackLevel: Int, eventInterval: Long, totalEvents: Int, gridSize: Int) {
        dataStore.edit { preferences ->
            preferences[N_BACK_LEVEL] = nBackLevel
            preferences[EVENT_INTERVAL] = eventInterval
            preferences[TOTAL_EVENTS] = totalEvents
            preferences[GRID_SIZE] = gridSize
        }
    }
}