package com.example.myday

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.serialization.json.Json

class GeminiService {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getTaskFromPrompt(voiceInput: String): Task? {
        val prompt = """You are a task parsing assistant. Your job is to take a natural language string and extract the task description and a user-friendly date and time.

        Respond with ONLY a valid JSON object in the following format:
        {"description": "The task description", "dateTime": "The extracted date and time"}

        If the user doesn't specify a date or time, use "Someday".

        Here is the user's input:
        "$voiceInput"""

        return try {
            val response = generativeModel.generateContent(prompt)
            response.text?.let {
                json.decodeFromString<Task>(it)
            }
        } catch (e: Exception) {
            // Handle API errors (e.g., network issues, invalid key)
            null
        }
    }
}