package com.example.myday

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskListDialog(onDismiss: () -> Unit, onAddTaskList: (String) -> Unit) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOptionsBottomSheet(
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Task List Options", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRename, modifier = Modifier.fillMaxWidth()) {
                Text("Rename")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun RenameTaskListDialog(
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