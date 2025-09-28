package com.example.myday

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("theme_preference")
        const val DEFAULT_THEME = "Default Blue"
    }

    val theme: Flow<String> = dataStore.data.map {
        it[THEME_KEY] ?: DEFAULT_THEME
    }

    suspend fun setTheme(themeName: String) {
        dataStore.edit {
            it[THEME_KEY] = themeName
        }
    }
}