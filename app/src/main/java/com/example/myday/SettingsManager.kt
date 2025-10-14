package com.example.myday

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        
        // Home screen widget preferences
        val SHOW_GREETING_KEY = booleanPreferencesKey("show_greeting")
        val SHOW_QUOTE_KEY = booleanPreferencesKey("show_quote")
        val SHOW_NEWS_KEY = booleanPreferencesKey("show_news")
        val SHOW_WEATHER_KEY = booleanPreferencesKey("show_weather")
        val NEWS_CATEGORY_KEY = stringPreferencesKey("news_category")
    }

    val theme: Flow<String> = dataStore.data.map {
        it[THEME_KEY] ?: DEFAULT_THEME
    }
    
    val showGreeting: Flow<Boolean> = dataStore.data.map {
        it[SHOW_GREETING_KEY] ?: true
    }
    
    val showQuote: Flow<Boolean> = dataStore.data.map {
        it[SHOW_QUOTE_KEY] ?: true
    }
    
    val showNews: Flow<Boolean> = dataStore.data.map {
        it[SHOW_NEWS_KEY] ?: true
    }
    
    val showWeather: Flow<Boolean> = dataStore.data.map {
        it[SHOW_WEATHER_KEY] ?: false
    }
    
    val newsCategory: Flow<String> = dataStore.data.map {
        it[NEWS_CATEGORY_KEY] ?: "general"
    }

    suspend fun setTheme(themeName: String) {
        dataStore.edit {
            it[THEME_KEY] = themeName
        }
    }
    
    suspend fun setShowGreeting(show: Boolean) {
        dataStore.edit {
            it[SHOW_GREETING_KEY] = show
        }
    }
    
    suspend fun setShowQuote(show: Boolean) {
        dataStore.edit {
            it[SHOW_QUOTE_KEY] = show
        }
    }
    
    suspend fun setShowNews(show: Boolean) {
        dataStore.edit {
            it[SHOW_NEWS_KEY] = show
        }
    }
    
    suspend fun setShowWeather(show: Boolean) {
        dataStore.edit {
            it[SHOW_WEATHER_KEY] = show
        }
    }
    
    suspend fun setNewsCategory(category: String) {
        dataStore.edit {
            it[NEWS_CATEGORY_KEY] = category
        }
    }
}