package com.example.myday

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(viewModel: MainViewModel, note: Note?) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    val backgroundColor = note?.color?.let { Color(it) } ?: Color(0xFFFFFFFF)
    val textColor = getTextColorForBackground(MaterialTheme.colorScheme.background)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (note == null) "New Note" else "Edit Note", color = getTextColorForBackground(backgroundColor)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackToNotes() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = getTextColorForBackground(backgroundColor))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (note == null) {
                            viewModel.addNote(title, content)
                        } else {
                            viewModel.updateNote(note.copy(title = title, content = content))
                        }
                        viewModel.onBackToNotes()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note", tint = getTextColorForBackground(backgroundColor))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Title", color = textColor.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(color = textColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Content", color = textColor.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth().weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }
    }
}
