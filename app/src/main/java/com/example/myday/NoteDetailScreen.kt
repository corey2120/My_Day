package com.example.myday

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(viewModel: MainViewModel, noteId: String?, onBack: () -> Unit) {
    val existingNote by remember(noteId) {
        if (!noteId.isNullOrBlank()) {
            viewModel.getNoteFlowById(noteId)
        } else flowOf(null)
    }.collectAsState(initial = null)

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(existingNote) {
        title = existingNote?.title ?: ""
        content = existingNote?.content ?: ""
    }

    val backgroundColor =
        existingNote?.color?.let { Color(it) } ?: MaterialTheme.colorScheme.surface
    val textColor = getTextColorForBackground(backgroundColor)

    Log.d("NoteDetailScreen", "backgroundColor: $backgroundColor, textColor: $textColor")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (noteId.isNullOrBlank()) "New Note" else "Edit Note",
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (noteId.isNullOrBlank()) {
                            viewModel.addNote(title, content)
                        } else {
                            existingNote?.let { noteToUpdate ->
                                viewModel.updateNote(
                                    noteToUpdate.copy(
                                        title = title,
                                        content = content
                                    )
                                )
                            }
                        }
                        onBack()
                    }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save Note",
                            tint = getTextColorForBackground(backgroundColor)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
                            TextField(
                                value = title,
                                onValueChange = { title = it },
                                placeholder = { Text("Title", color = textColor.copy(alpha = 0.7f)) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.headlineMedium,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    disabledTextColor = textColor,
                                    errorTextColor = textColor,
                                    cursorColor = textColor,
                                    selectionColors = TextSelectionColors(
                                        handleColor = textColor,
                                        backgroundColor = textColor.copy(alpha = 0.4f)
                                    )
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextField(
                                value = content,
                                onValueChange = { content = it },
                                placeholder = { Text("Content", color = textColor.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = textColor,
                                    unfocusedTextColor = textColor,
                                    disabledTextColor = textColor,
                                    errorTextColor = textColor,
                                    cursorColor = textColor,
                                    selectionColors = TextSelectionColors(handleColor = textColor, backgroundColor = textColor.copy(alpha = 0.4f))
                                )
                            )            }
        }
    }


