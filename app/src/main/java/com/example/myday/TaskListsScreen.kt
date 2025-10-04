package com.example.myday

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListsScreen(viewModel: MainViewModel) {
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
                title = { Text("My Task Lists") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackToHome() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create new list")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(taskLists) { taskList ->
                Box(
                    modifier = Modifier.combinedClickable(
                        onClick = { viewModel.onTaskListClicked(taskList.id) },
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
            onEdit = {
                showEditOptionsDialog = false
                showEditTaskListDialog = true
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

    if (showEditTaskListDialog) {
        selectedTaskList?.let { taskList ->
            EditTaskListDialog(
                taskList = taskList,
                onDismiss = { showEditTaskListDialog = false },
                onSave = { newName ->
                    viewModel.editTaskList(taskList.id, newName)
                    showEditTaskListDialog = false
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
    onEdit: () -> Unit,
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
                Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
                    Text("Edit")
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

@Composable
private fun EditTaskListDialog(
    taskList: TaskList,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(taskList.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task List") },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(newName)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}