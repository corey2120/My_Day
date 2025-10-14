package com.example.myday

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * A state holder class for the TaskListsScreen.
 *
 * @param onAddTaskList A callback to be invoked when a new task list is added.
 * @param onRenameTaskList A callback to be invoked when a task list is renamed.
 * @param onDeleteTaskList A callback to be invoked when a task list is deleted.
 */
class TaskListScreenState(
    private val onAddTaskList: (String) -> Unit,
    private val onRenameTaskList: (String, String) -> Unit,
    private val onDeleteTaskList: (String) -> Unit,
    private val onAddTask: (String, String) -> Unit
) {
    var showAddTaskListDialog by mutableStateOf(false)
    var showEditOptionsDialog by mutableStateOf(false)
    var showRenameTaskListDialog by mutableStateOf(false)
    var showQuickAddTaskDialog by mutableStateOf(false)

    var selectedTaskList by mutableStateOf<TaskList?>(null)
    var quickAddTaskToListId by mutableStateOf<String?>(null)

    fun onAddTaskListClicked() {
        showAddTaskListDialog = true
    }

    fun onTaskListLongClicked(taskList: TaskList) {
        selectedTaskList = taskList
        showEditOptionsDialog = true
    }

    fun onQuickAddTaskClicked(listId: String) {
        quickAddTaskToListId = listId
        showQuickAddTaskDialog = true
    }

    fun addTaskList(name: String) {
        onAddTaskList(name)
        showAddTaskListDialog = false
    }

    fun addQuickTask(description: String) {
        quickAddTaskToListId?.let {
            onAddTask(description, it)
        }
        showQuickAddTaskDialog = false
    }

    fun onRenameTaskList(newName: String) {
        selectedTaskList?.let {
            onRenameTaskList(it.id, newName)
        }
        showRenameTaskListDialog = false
    }

    fun onDeleteTaskList() {
        selectedTaskList?.let {
            onDeleteTaskList(it.id)
        }
        showEditOptionsDialog = false
    }

    fun onDismissDialog() {
        showAddTaskListDialog = false
        showEditOptionsDialog = false
        showRenameTaskListDialog = false
        showQuickAddTaskDialog = false
    }

    fun onRenameClicked() {
        showEditOptionsDialog = false
        showRenameTaskListDialog = true
    }
}