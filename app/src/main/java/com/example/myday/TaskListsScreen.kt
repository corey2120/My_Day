package com.example.myday

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * This screen displays a list of task lists. The user can add, rename, and delete task lists.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListsScreen(
    viewModel: MainViewModel,
    onTaskListClicked: (String) -> Unit
) {
    val taskLists: List<TaskList> by viewModel.taskLists.collectAsState()
    val tasks: List<Task> by viewModel.tasks.collectAsState()
    val scope = rememberCoroutineScope()

    val state = remember {
        TaskListScreenState(
            onAddTaskList = { name ->
                scope.launch {
                    viewModel.addTaskList(name)
                }
            },
            onRenameTaskList = { id, newName ->
                scope.launch {
                    viewModel.renameTaskList(id, newName)
                }
            },
            onDeleteTaskList = { id ->
                scope.launch {
                    viewModel.deleteTaskList(id)
                }
            },
            onAddTask = { description, listId ->
                scope.launch {
                    viewModel.addTask(description, listId)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = taskLists, key = { it.id }) { taskList ->
                val tasksInList = tasks.filter { it.listId == taskList.id }
                val completedTasks = tasksInList.count { it.isCompleted }
                val progress = if (tasksInList.isNotEmpty()) {
                    completedTasks.toFloat() / tasksInList.size.toFloat()
                } else {
                    0f
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onTaskListClicked(taskList.id) },
                            onLongClick = { state.onTaskListLongClicked(taskList) }
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = taskList.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${completedTasks} / ${tasksInList.size} completed", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        IconButton(onClick = { state.onQuickAddTaskClicked(taskList.id) }) {
                            Icon(Icons.Default.Add, contentDescription = "Quick Add Task")
                        }
                    }
                }
            }
        }
    }

    if (state.showAddTaskListDialog) {
        AddTaskListDialog(
            onDismiss = { state.onDismissDialog() },
            onAddTaskList = { name ->
                state.addTaskList(name)
            }
        )
    }

    if (state.showQuickAddTaskDialog) {
        QuickAddTaskDialog(
            onDismiss = { state.onDismissDialog() },
            onAddTask = { description ->
                state.addQuickTask(description)
            }
        )
    }

    if (state.showEditOptionsDialog) {
        EditOptionsBottomSheet(
            onDismiss = { state.onDismissDialog() },
            onRename = { state.onRenameClicked() },
            onDelete = { state.onDeleteTaskList() }
        )
    }

    if (state.showRenameTaskListDialog) {
        state.selectedTaskList?.let { taskList ->
            RenameTaskListDialog(
                taskList = taskList,
                onDismiss = { state.onDismissDialog() },
                onRename = { newName ->
                    state.onRenameTaskList(newName)
                }
            )
        }
    }
}

@Composable
private fun QuickAddTaskDialog(onDismiss: () -> Unit, onAddTask: (String) -> Unit) {
    var newTaskDescription by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = { 
            TextField(
                value = newTaskDescription,
                onValueChange = { newTaskDescription = it },
                label = { Text("Task Description") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newTaskDescription.isNotBlank()) {
                        onAddTask(newTaskDescription)
                        onDismiss()
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}