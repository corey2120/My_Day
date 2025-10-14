package com.example.myday

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Minimal top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (title.isNotBlank() || content.isNotBlank()) {
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
                    }
                    onBack()
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
            }

            // Content area - seamless
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Title", color = textColor.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(color = textColor),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor,
                        selectionColors = TextSelectionColors(
                            handleColor = textColor,
                            backgroundColor = textColor.copy(alpha = 0.4f)
                        )
                    )
                )
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("Note", color = textColor.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        cursorColor = textColor,
                        selectionColors = TextSelectionColors(
                            handleColor = textColor,
                            backgroundColor = textColor.copy(alpha = 0.4f)
                        )
                    )
                )
            }
        }
    }
}