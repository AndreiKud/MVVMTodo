package ru.andreikud.mvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private const val TAG = "PreferencesManager"

class TaskFilterPreferences(
    val sortOrder: SortOrder,
    val hideCompleted: Boolean
)

class TaskFilterPreference<T>(
    val value: T,
    private val defaultValue: T,
    private val dataStoreKey: String
)

enum class SortOrder {
    BY_DATE,
    BY_NAME;
}

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val preferencesFlow: Flow<TaskFilterPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Failed to read from DataStore", exception)
                emit(emptyPreferences())
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[KEY_SORT_ORDER] ?: DEFAULT_SORT_ORDER.name
            )
            val hideCompleted = preferences[KEY_HIDE_COMPLETED] ?: DEFAULT_HIDE_COMPLETED
            Log.d(TAG, "${preferences.asMap()}")
            TaskFilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        updatePreference(sortOrder.name, KEY_SORT_ORDER)
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        updatePreference(hideCompleted, KEY_HIDE_COMPLETED)
    }

    private suspend fun <T> updatePreference(value: T, key: Preferences.Key<T>) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    companion object {
        val KEY_SORT_ORDER = stringPreferencesKey("sort_order")
        val KEY_HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
        val DEFAULT_SORT_ORDER = SortOrder.BY_DATE
        const val DEFAULT_HIDE_COMPLETED = false
    }
}