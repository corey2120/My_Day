package com.example.myday

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(
    viewModel: MainViewModel,
    onNoteClicked: (String) -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    val secureNotesPin by viewModel.secureNotesPin.collectAsState(initial = null)
    val securityQuestion by viewModel.securityQuestion.collectAsState(initial = null)
    val securityAnswer by viewModel.securityAnswer.collectAsState(initial = null)
    var showQuickAddDialog by remember { mutableStateOf(false) }
    var noteToUnlock by remember { mutableStateOf<Note?>(null) }

    if (showQuickAddDialog) {
        QuickAddNoteDialog(
            viewModel = viewModel,
            onDismiss = { showQuickAddDialog = false },
            onViewFullEditor = { 
                showQuickAddDialog = false
                onNoteClicked("")
            }
        )
    }
    
    noteToUnlock?.let { note ->
        PinAuthDialog(
            expectedPin = secureNotesPin,
            securityQuestion = securityQuestion,
            securityAnswer = securityAnswer,
            onSuccess = {
                noteToUnlock = null
                onNoteClicked(note.id)
            },
            onDismiss = { noteToUnlock = null },
            onSetupPin = { newPin ->
                viewModel.setSecureNotesPin(newPin)
                noteToUnlock = null
                onNoteClicked(note.id)
            },
            onPinRecovered = { newPin ->
                viewModel.setSecureNotesPin(newPin)
                noteToUnlock = null
                onNoteClicked(note.id)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onClick = { 
                        if (note.isSecured) {
                            noteToUnlock = note
                        } else {
                            onNoteClicked(note.id)
                        }
                    },
                    onDelete = { viewModel.deleteNote(note) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    val backgroundColor = Color(note.color)
    val textColor = getTextColorForBackground(backgroundColor)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Note?") },
            text = { 
                Text(
                    if (note.title.isNotBlank()) 
                        "Are you sure you want to delete \"${note.title}\"?" 
                    else 
                        "Are you sure you want to delete this note?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { 
                    showDeleteDialog = true
                }
            ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Show lock icon if secured
            if (note.isSecured) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Secured Note",
                        tint = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            
            if (note.title.isNotBlank()) {
                Text(
                    text = if (note.isSecured) "•".repeat(note.title.length.coerceAtMost(20)) else note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (note.content.isNotBlank()) {
                Text(
                    text = if (note.isSecured) "•".repeat(note.content.length.coerceAtMost(50)) else note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.87f),
                    maxLines = 10
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddNoteDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onViewFullEditor: () -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    var noteTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Note",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { 
            Text(
                "Quick Note",
                style = MaterialTheme.typography.titleLarge
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Title (optional)") },
                    placeholder = { Text("Give your note a title...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note") },
                    placeholder = { Text("What's on your mind?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6
                )
                
                Text(
                    "Tip: Use the full editor for more options like colors and security",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onViewFullEditor) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Full Editor")
                }
                Button(
                    onClick = {
                        if (noteText.isNotBlank() || noteTitle.isNotBlank()) {
                            viewModel.addNote(noteTitle, noteText)
                            onDismiss()
                        }
                    },
                    enabled = noteText.isNotBlank() || noteTitle.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinAuthDialog(
    expectedPin: String?,
    securityQuestion: String?,
    securityAnswer: String?,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit,
    onSetupPin: (String) -> Unit,
    onPinRecovered: (String) -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var showRecoveryMode by remember { mutableStateOf(false) }
    var recoveryAnswer by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmNewPin by remember { mutableStateOf("") }
    val isSetupMode = expectedPin.isNullOrBlank()
    val hasRecoveryOption = !securityQuestion.isNullOrBlank() && !securityAnswer.isNullOrBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Secure Note",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { 
            Text(
                when {
                    showRecoveryMode -> "Recover PIN"
                    isSetupMode -> "Setup Security PIN"
                    else -> "Enter PIN"
                }
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (showRecoveryMode) {
                    // Recovery mode
                    Text(
                        "Answer your security question to reset your PIN",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        securityQuestion ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = recoveryAnswer,
                        onValueChange = { 
                            recoveryAnswer = it
                            error = null
                        },
                        label = { Text("Your Answer") },
                        placeholder = { Text("Enter your answer") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = error != null
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { 
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                newPin = it
                                error = null
                            }
                        },
                        label = { Text("New PIN") },
                        placeholder = { Text("Enter new PIN (4-6 digits)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = error != null,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                        ),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    
                    OutlinedTextField(
                        value = confirmNewPin,
                        onValueChange = { 
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                confirmNewPin = it
                                error = null
                            }
                        },
                        label = { Text("Confirm New PIN") },
                        placeholder = { Text("Re-enter new PIN") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = error != null,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                        ),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                } else {
                    // Normal PIN entry or setup mode
                    if (isSetupMode) {
                        Text(
                            "Create a 4-6 digit PIN to secure your notes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { 
                            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                pinInput = it
                                error = null
                            }
                        },
                        label = { Text(if (isSetupMode) "Enter PIN" else "PIN") },
                        placeholder = { Text("Enter 4-6 digits") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = error != null,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                        ),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    
                    if (isSetupMode) {
                        OutlinedTextField(
                            value = confirmPin,
                            onValueChange = { 
                                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                    confirmPin = it
                                    error = null
                                }
                            },
                            label = { Text("Confirm PIN") },
                            placeholder = { Text("Re-enter PIN") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = error != null,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                            ),
                            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                        )
                    }
                    
                    // Forgot PIN link
                    if (!isSetupMode && hasRecoveryOption) {
                        TextButton(
                            onClick = { 
                                showRecoveryMode = true
                                error = null
                            },
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("Forgot PIN?", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (showRecoveryMode) {
                        // Validate recovery
                        when {
                            recoveryAnswer.isBlank() -> {
                                error = "Please enter your answer"
                            }
                            recoveryAnswer.lowercase().trim() != securityAnswer -> {
                                error = "Incorrect answer"
                            }
                            newPin.length !in 4..6 -> {
                                error = "New PIN must be 4-6 digits"
                            }
                            newPin != confirmNewPin -> {
                                error = "PINs don't match"
                            }
                            else -> {
                                onPinRecovered(newPin)
                            }
                        }
                    } else if (isSetupMode) {
                        when {
                            pinInput.length !in 4..6 -> {
                                error = "PIN must be 4-6 digits"
                            }
                            pinInput != confirmPin -> {
                                error = "PINs don't match"
                            }
                            else -> {
                                onSetupPin(pinInput)
                            }
                        }
                    } else {
                        if (pinInput == expectedPin) {
                            onSuccess()
                        } else {
                            error = "Incorrect PIN"
                            pinInput = ""
                        }
                    }
                },
                enabled = if (showRecoveryMode) {
                    recoveryAnswer.isNotBlank() && newPin.length >= 4 && confirmNewPin.length >= 4
                } else {
                    pinInput.length >= 4 && (isSetupMode.not() || confirmPin.length >= 4)
                }
            ) {
                Text(
                    when {
                        showRecoveryMode -> "Reset PIN"
                        isSetupMode -> "Create PIN"
                        else -> "Unlock"
                    }
                )
            }
        },
        dismissButton = {
            if (showRecoveryMode) {
                TextButton(onClick = { 
                    showRecoveryMode = false
                    error = null
                    recoveryAnswer = ""
                    newPin = ""
                    confirmNewPin = ""
                }) {
                    Text("Back")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
