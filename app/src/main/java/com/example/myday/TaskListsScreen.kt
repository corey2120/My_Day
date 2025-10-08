package com.example.myday


import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListsScreen(
    viewModel: MainViewModel,
    onTaskListClicked: (String) -> Unit,
    onBack: () -> Unit,
    paddingValues: PaddingValues
) {
    val taskLists: List<TaskList> by viewModel.taskLists.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    var selectedTaskList by remember { mutableStateOf<TaskList?>(null) }
    var showEditOptionsDialog by remember { mutableStateOf(false) }
    var showRenameTaskListDialog by remember { mutableStateOf(false) }
    var showEditTaskListDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Lists") },
                navigationIcon = {

                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
                LazyColumn(contentPadding = paddingValues) {
            item {
                Spacer(modifier = Modifier.height(56.dp)) // Height of TopAppBar
            }
            items(taskLists) { taskList ->
                Box(
                    modifier = Modifier.combinedClickable(
                        onClick = { onTaskListClicked(taskList.id) },
                        onLongClick = {
                            selectedTaskList = taskList
                            showEditOptionsDialog = true
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(text = taskList.name)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddTaskListDialog(
            onDismiss = { showDialog = false },
            onAddTaskList = { name ->
                viewModel.addTaskList(name)
            }
        )
    }

    if (showEditOptionsDialog) {
        EditOptionsDialog(
            onDismiss = { showEditOptionsDialog = false },
            onRename = {
                showEditOptionsDialog = false
                showRenameTaskListDialog = true
            },
            onDelete = {
                selectedTaskList?.let { viewModel.deleteTaskList(it.id) }
                showEditOptionsDialog = false
            }
        )
    }

    if (showRenameTaskListDialog) {
        selectedTaskList?.let { taskList ->
            RenameTaskListDialog(
                taskList = taskList,
                onDismiss = { showRenameTaskListDialog = false },
                onRename = { newName ->
                    viewModel.renameTaskList(taskList.id, newName)
                    showRenameTaskListDialog = false
                }
            )
        }
    }
}

@Composable
private fun AddTaskListDialog(onDismiss: () -> Unit, onAddTaskList: (String) -> Unit) {
    var newListName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Task List") },
        text = {
            Column {
                TextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("List Name") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newListName.isNotBlank()) {
                        onAddTaskList(newListName)
                        onDismiss()
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditOptionsDialog(
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Task List Options") },
        text = {
            Column {
                Text("What would you like to do?", modifier = Modifier.padding(bottom = 8.dp))
                Button(onClick = onRename, modifier = Modifier.fillMaxWidth()) {
                    Text("Rename")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                    Text("Delete")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun RenameTaskListDialog(
    taskList: TaskList,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newName by remember { mutableStateOf(taskList.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Task List") },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("New Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onRename(newName)
                }
            ) {
                Text("Rename")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
