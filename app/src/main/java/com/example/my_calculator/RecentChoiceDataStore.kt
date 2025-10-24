package com.example.my_calculator

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("recent_choice_prefs")

class RecentChoiceDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val RECENT_CHOICE_KEY = stringPreferencesKey("recent_calculator_choice")
    }

    val recentChoiceFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[RECENT_CHOICE_KEY]
        }

    suspend fun saveRecentChoice(choice: String) {
        dataStore.edit {
            it[RECENT_CHOICE_KEY] = choice
        }
    }
}
