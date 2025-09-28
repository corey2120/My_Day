package com.example.myday

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshotFlow

sealed class Screen {
    object Home : Screen()
    object TaskLists : Screen()
    data class Tasks(val listId: String) : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = SettingsManager(application)

    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

    val themeName: StateFlow<String> = settingsManager.theme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsManager.DEFAULT_THEME
    )

    val taskLists = mutableStateOf<List<TaskList>>(emptyList())
    val tasks = mutableStateOf<List<Task>>(emptyList())

    val tasksWithDates by derivedStateOf {
        tasks.value.mapNotNull { task ->
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.dateTime)
            } catch (e: Exception) {
                null
            }
        }
    }

    private val geminiService = GeminiService()

    fun setTheme(themeName: String) {
        viewModelScope.launch {
            settingsManager.setTheme(themeName)
        }
    }
    fun addTaskList(name: String) {
        val newList = TaskList(name = name)
        taskLists.value = taskLists.value + newList
    }

    fun onTaskListClicked(listId: String) {
        currentScreen = Screen.Tasks(listId)
    }

    fun onBackToTaskLists() {
        currentScreen = Screen.TaskLists
    }

    fun onNavigateToTaskLists() {
        currentScreen = Screen.TaskLists
    }

    fun onBackToHome() {
        currentScreen = Screen.Home
    }

    fun toggleTaskCompleted(taskId: String) {
        val currentTasks = tasks.value.toMutableList()
        val taskIndex = currentTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = currentTasks[taskIndex]
            currentTasks[taskIndex] = task.copy(isCompleted = !task.isCompleted)
            tasks.value = currentTasks
        }
    }

    fun cycleTaskPriority(taskId: String) {
        val currentTasks = tasks.value.toMutableList()
        val taskIndex = currentTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = currentTasks[taskIndex]
            val nextPriority = when (task.priority) {
                Priority.NONE -> Priority.LOW
                Priority.LOW -> Priority.MEDIUM
                Priority.MEDIUM -> Priority.HIGH
                Priority.HIGH -> Priority.NONE
            }
            currentTasks[taskIndex] = task.copy(priority = nextPriority)
            tasks.value = currentTasks
        }
    }

    fun deleteTask(taskId: String) {
        tasks.value = tasks.value.filter { it.id != taskId }
    }

    fun addTask(description: String, listId: String, date: Date? = null) {
        val dateTime = date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "Someday"
        val newTask = Task(description = description, dateTime = dateTime, listId = listId)
        tasks.value = tasks.value + newTask
    }

    fun processVoiceInput(text: String, listId: String) {
        viewModelScope.launch {
            geminiService.getTaskFromPrompt(text)?.let { geminiTask ->
                val newTask = Task(
                    description = geminiTask.description,
                    dateTime = geminiTask.dateTime,
                    listId = listId
                )
                tasks.value = tasks.value + newTask
            }
        }
    }
}