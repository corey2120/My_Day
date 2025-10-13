package com.example.myday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable.isCompleted
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val taskDao: TaskDao,
    private val NoteDao: NoteDao,
) : ViewModel() {

    val themeName: StateFlow<String> = settingsManager.theme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsManager.DEFAULT_THEME
    )

    val taskLists: StateFlow<List<TaskList>> = taskDao.getTaskLists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<Task>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasksWithDates by derivedStateOf {
        tasks.value.mapNotNull { task ->
            try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(task.dateTime)
            } catch (e: Exception) {
                null
            }
        }
    }

    val notes: StateFlow<List<Note>> = NoteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        ensureDefaultTaskListExists()
    }

    private fun ensureDefaultTaskListExists() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val taskLists = taskDao.getTaskLists().first()
                val generalList = taskLists.find { it.name == "General" }
                if (generalList == null) {
                    taskDao.insertTaskList(TaskList(name = "General"))
                }
            }
        }
    }

    private val geminiService = GeminiService()

    fun setTheme(themeName: String) {
        viewModelScope.launch {
            settingsManager.setTheme(themeName)
        }
    }

    suspend fun addTaskList(name: String): TaskList = withContext(Dispatchers.IO) {
        val newTaskList = TaskList(name = name)
        taskDao.insertTaskList(newTaskList)
        newTaskList
    }



    fun renameTaskList(listId: String, newName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val taskList = taskLists.value.find { it.id == listId }?.copy(name = newName)
                taskList?.let { taskDao.updateTaskList(it) }
            }
        }
    }

    fun editTaskList(listId: String, newName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val taskList = taskLists.value.find { it.id == listId }?.copy(name = newName)
                taskList?.let { taskDao.updateTaskList(it) }
            }
        }
    }

    fun deleteTaskList(listId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val taskList = taskLists.value.find { it.id == listId }
                taskList?.let { taskDao.deleteTaskList(it) }
            }
        }
    }



    fun toggleTaskCompleted(taskId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = tasks.value.find { it.id == taskId }
                task?.let { taskDao.updateTask(it.copy(isCompleted = !it.isCompleted)) }
            }
        }
    }

    fun cycleTaskPriority(taskId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = tasks.value.find { it.id == taskId }
                task?.let {
                    val nextPriority = when (it.priority) {
                        Priority.NONE -> Priority.LOW
                        Priority.LOW -> Priority.MEDIUM
                        Priority.MEDIUM -> Priority.HIGH
                        Priority.HIGH -> Priority.NONE
                    }
                    taskDao.updateTask(it.copy(priority = nextPriority))
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = tasks.value.find { it.id == taskId }
                task?.let { taskDao.deleteTask(it) }
            }
        }
    }

    fun renameTask(taskId: String, newDescription: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = tasks.value.find { it.id == taskId }
                task?.let { taskDao.updateTask(it.copy(description = newDescription)) }
            }
        }
    }

    fun editTask(taskId: String, newDescription: String, newDateTime: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = tasks.value.find { it.id == taskId }
                task?.let { taskDao.updateTask(it.copy(description = newDescription, dateTime = newDateTime)) }
            }
        }
    }

    fun addTask(description: String, listId: String, date: Date? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dateTime = date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "Someday"
                val newTask = Task(description = description, dateTime = dateTime, listId = listId)
                taskDao.insertTask(newTask)
            }
        }
    }

    fun processVoiceInput(text: String, listId: String) {
        viewModelScope.launch {
            geminiService.getTaskFromPrompt(text)?.let { geminiTask ->
                val newTask = Task(
                    description = geminiTask.description,
                    dateTime = geminiTask.dateTime,
                    listId = listId
                )
                taskDao.insertTask(newTask)
            }
        }
    }

    fun moveTask(taskId: String, newListId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = tasks.value.find { it.id == taskId }
                task?.let { 
                    taskDao.updateTask(it.copy(listId = newListId))
                }
            }
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            val colors = listOf(
                0xFFFFFFFF,
                0xFFF28B82,
                0xFFFBBC04,
                0xFFFFF475,
                0xFFCCFF90,
                0xFFA7FFEB,
                0xFFCBF0F8,
                0xFFAECBFA,
                0xFFD7AEFB,
                0xFFFDCFE8,
                0xFFE6C9A8,
                0xFFE8EAED
            )
            val randomColor = colors.random()
            NoteDao.insertNote(Note(title = title, content = content, color = randomColor.toInt()))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            NoteDao.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            NoteDao.deleteNote(note)
        }
    }

    fun getNoteById(noteId: String): Note? {
        return notes.value.find { it.id == noteId }
    }

    fun getNoteFlowById(noteId: String): Flow<Note?> {
        return NoteDao.getNoteByIdFlow(noteId)
    }


}