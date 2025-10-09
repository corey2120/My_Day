package com.example.myday

import android.Manifest
import android.R.attr.text
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun PriorityIndicator(priority: Priority, onClick: () -> Unit) {
    val color = when (priority) {
        Priority.HIGH -> Color.Red
        Priority.MEDIUM -> Color(0xFFFFA500) // Orange
        Priority.LOW -> Color.Yellow
        Priority.NONE -> Color.Transparent
    }

    Box(modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(color)
        .clickable(onClick = onClick)
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    listId: String,
    onBack: () -> Unit
) {
    val tasks: List<Task> by viewModel.tasks.collectAsState()
    val taskLists: List<TaskList> by viewModel.taskLists.collectAsState()

    val tasksForList = tasks.filter { it.listId == listId }
    val listName = taskLists.find { it.id == listId }?.name ?: "Tasks"

    var showTaskDialog by remember { mutableStateOf(false) }

    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showEditOptionsDialog by remember { mutableStateOf(false) }
    var showRenameTaskDialog by remember { mutableStateOf(false) }
    var showEditTaskDialog by remember { mutableStateOf(false) }
    var showMoveTaskDialog by remember { mutableStateOf(false) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.get(0)?.let {
                viewModel.processVoiceInput(it, listId)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your task...")
            }
            speechRecognizerLauncher.launch(intent)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(listName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = { showTaskDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
                Spacer(modifier = Modifier.height(16.dp))
                FloatingActionButton(
                    onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Speak Task")
                }
            }
        }
    ) { innerPadding ->

        val topPadding = innerPadding.calculateTopPadding()
        val newPadding = PaddingValues(
            top = topPadding - 20.dp,
            bottom = innerPadding.calculateBottomPadding(),
            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
        )
        LazyColumn(
            contentPadding = newPadding
        ) {
            items(tasksForList) { task ->
                Box(modifier = Modifier.combinedClickable(
                    onClick = { viewModel.toggleTaskCompleted(task.id) },
                    onLongClick = {
                        selectedTask = task
                        showEditOptionsDialog = true
                    }
                )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { viewModel.toggleTaskCompleted(task.id) }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        PriorityIndicator(priority = task.priority) {
                            viewModel.cycleTaskPriority(task.id)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.description,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                            if (task.dateTime.isNotEmpty()) {
                                Text(
                                    text = task.dateTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
                        }
                        IconButton(onClick = {
                            selectedTask = task
                            showEditOptionsDialog = true
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Task Options")
                        }
                    }
                }
            }
        }
    }


    if (showTaskDialog) {
        AddTaskDialog(
            onDismiss = { showTaskDialog = false },
            onAddTask = { description ->
                viewModel.addTask(description, listId)
            }
        )
    }

    if (showEditOptionsDialog) {
        EditOptionsDialog(
            onDismiss = { showEditOptionsDialog = false },
            onRename = {
                showEditOptionsDialog = false
                showRenameTaskDialog = true
            },
            onEdit = {
                showEditOptionsDialog = false
                showEditTaskDialog = true
            },
            onDelete = {
                selectedTask?.let { viewModel.deleteTask(it.id) }
                showEditOptionsDialog = false
            },
            onMove = {
                showEditOptionsDialog = false
                showMoveTaskDialog = true
            }
        )
    }

    if (showMoveTaskDialog) {
        selectedTask?.let {
            MoveTaskDialog(
                viewModel = viewModel,
                task = it,
                onDismiss = { showMoveTaskDialog = false }
            )
        }
    }

    if (showRenameTaskDialog) {
        selectedTask?.let { task ->
            RenameTaskDialog(
                task = task,
                onDismiss = { showRenameTaskDialog = false },
                onRename = { newDescription ->
                    viewModel.renameTask(task.id, newDescription)
                    showRenameTaskDialog = false
                }
            )
        }
    }

    if (showEditTaskDialog) {
        selectedTask?.let { task ->
            EditTaskDialog(
                task = task,
                onDismiss = { showEditTaskDialog = false },
                onSave = { newDescription, newDateTime ->
                    viewModel.editTask(task.id, newDescription, newDateTime)
                    showEditTaskDialog = false
                }
            )
        }
    }
}



@Composable
private fun AddTaskDialog(onDismiss: () -> Unit, onAddTask: (String) -> Unit) {
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

@Composable
private fun EditOptionsDialog(
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMove: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Task Options") },
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
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onMove, modifier = Modifier.fillMaxWidth()) {
                    Text("Move")
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
private fun RenameTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var newDescription by remember { mutableStateOf(task.description) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Task") },
        text = {
            TextField(
                value = newDescription,
                onValueChange = { newDescription = it },
                label = { Text("New Description") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onRename(newDescription)
                    onDismiss() // Dismiss after renaming
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
private fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var newDescription by remember { mutableStateOf(task.description) }
    var newDateTime by remember { mutableStateOf(task.dateTime) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column {
                TextField(
                    value = newDescription,
                    onValueChange = { newDescription = it },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newDateTime,
                    onValueChange = { newDateTime = it },
                    label = { Text("Date and Time") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(newDescription, newDateTime)
                    onDismiss()
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
@Composable
private fun MoveTaskDialog(
    viewModel: MainViewModel,
    task: Task,
    onDismiss: () -> Unit
) {
    val taskLists by viewModel.taskLists.collectAsState()
    val otherLists = taskLists.filter { it.id != task.listId }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move Task") },
        text = {

            LazyColumn {
                items(otherLists) { list ->
                    Text(
                        text = list.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.moveTask(task.id, list.id)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp)
                    )
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
