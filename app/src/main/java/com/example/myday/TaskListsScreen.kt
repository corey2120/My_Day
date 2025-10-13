package com.example.myday

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * This screen displays a list of task lists. The user can add, rename, and delete task lists.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListsScreen(
    viewModel: MainViewModel,
    onTaskListClicked: (String) -> Unit
) {
    val taskLists: List<TaskList> by viewModel.taskLists.collectAsState()
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
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Lists") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { state.onAddTaskListClicked() }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task List")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(taskLists) { taskList ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onTaskListClicked(taskList.id) },
                            onLongClick = { state.onTaskListLongClicked(taskList) }
                        )
                        .padding(16.dp)
                ) {
                    Text(text = taskList.name, style = MaterialTheme.typography.titleMedium)
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