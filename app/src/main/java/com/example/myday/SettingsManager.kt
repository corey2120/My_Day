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
        
        // Security preferences
        val SECURE_NOTES_PIN_KEY = stringPreferencesKey("secure_notes_pin")
        val SECURITY_QUESTION_KEY = stringPreferencesKey("security_question")
        val SECURITY_ANSWER_KEY = stringPreferencesKey("security_answer")
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
    
    val secureNotesPin: Flow<String?> = dataStore.data.map {
        it[SECURE_NOTES_PIN_KEY]
    }
    
    val securityQuestion: Flow<String?> = dataStore.data.map {
        it[SECURITY_QUESTION_KEY]
    }
    
    val securityAnswer: Flow<String?> = dataStore.data.map {
        it[SECURITY_ANSWER_KEY]
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
    
    suspend fun setSecureNotesPin(pin: String?) {
        dataStore.edit {
            if (pin.isNullOrBlank()) {
                it.remove(SECURE_NOTES_PIN_KEY)
            } else {
                it[SECURE_NOTES_PIN_KEY] = pin
            }
        }
    }
    
    suspend fun setSecurityQuestion(question: String?) {
        dataStore.edit {
            if (question.isNullOrBlank()) {
                it.remove(SECURITY_QUESTION_KEY)
            } else {
                it[SECURITY_QUESTION_KEY] = question
            }
        }
    }
    
    suspend fun setSecurityAnswer(answer: String?) {
        dataStore.edit {
            if (answer.isNullOrBlank()) {
                it.remove(SECURITY_ANSWER_KEY)
            } else {
                it[SECURITY_ANSWER_KEY] = answer
            }
        }
    }
}