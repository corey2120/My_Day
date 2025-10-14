package com.example.myday

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*

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
    var isSecured by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(0xFFFFFFFF) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showToolbar by remember { mutableStateOf(true) }

    LaunchedEffect(existingNote) {
        title = existingNote?.title ?: ""
        content = existingNote?.content ?: ""
        isSecured = existingNote?.isSecured ?: false
        selectedColor = existingNote?.color?.toLong() ?: 0xFFFFFFFF
    }

    val backgroundColor = Color(selectedColor)
    val textColor = getTextColorForBackground(backgroundColor)
    
    // Auto-save function
    val saveNote = {
        if (title.isNotBlank() || content.isNotBlank()) {
            if (noteId.isNullOrBlank()) {
                viewModel.addNote(title, content, isSecured)
            } else {
                existingNote?.let { noteToUpdate ->
                    viewModel.updateNote(
                        noteToUpdate.copy(
                            title = title,
                            content = content,
                            isSecured = isSecured,
                            color = selectedColor.toInt()
                        )
                    )
                }
            }
        }
    }

    // Delete dialog
    if (showDeleteDialog && existingNote != null) {
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
                Text("Are you sure you want to delete this note? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        existingNote?.let { viewModel.deleteNote(it) }
                        showDeleteDialog = false
                        onBack()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Collapsible toolbar
            AnimatedVisibility(
                visible = showToolbar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    // Top bar with actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button
                        IconButton(onClick = {
                            saveNote()
                            onBack()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                        
                        // Right side actions
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Security toggle
                            IconButton(
                                onClick = { isSecured = !isSecured }
                            ) {
                                Icon(
                                    if (isSecured) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = if (isSecured) "Secured" else "Not Secured",
                                    tint = if (isSecured) textColor else textColor.copy(alpha = 0.5f)
                                )
                            }
                            
                            // Color picker toggle
                            IconButton(
                                onClick = { showColorPicker = !showColorPicker }
                            ) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = "Change Color",
                                    tint = textColor.copy(alpha = 0.7f)
                                )
                            }
                            
                            // More menu
                            Box {
                                IconButton(onClick = { showMoreMenu = true }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More Options",
                                        tint = textColor.copy(alpha = 0.7f)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMoreMenu,
                                    onDismissRequest = { showMoreMenu = false }
                                ) {
                                    if (existingNote != null) {
                                        DropdownMenuItem(
                                            text = { Text("Delete Note", color = MaterialTheme.colorScheme.error) },
                                            onClick = {
                                                showMoreMenu = false
                                                showDeleteDialog = true
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        )
                                    }
                                    DropdownMenuItem(
                                        text = { Text("Hide Toolbar") },
                                        onClick = {
                                            showMoreMenu = false
                                            showToolbar = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.MoreVert, contentDescription = "Hide")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Save & Close") },
                                        onClick = {
                                            showMoreMenu = false
                                            saveNote()
                                            onBack()
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Check, contentDescription = "Save")
                                        }
                                    )
                                }
                            }
                            
                            // Save button
                            FilledTonalButton(
                                onClick = {
                                    saveNote()
                                    onBack()
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = textColor.copy(alpha = 0.15f),
                                    contentColor = textColor
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Save",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save")
                            }
                        }
                    }

                    // Color picker bar
                    if (showColorPicker) {
                        ColorPickerBar(
                            selectedColor = selectedColor,
                            onColorSelected = { 
                                selectedColor = it
                            },
                            textColor = textColor
                        )
                    }

                    // Last modified indicator (only show if note exists and toolbar is visible)
                    existingNote?.let {
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' h:mm a", Locale.getDefault())
                        Text(
                            text = "Last modified: ${dateFormat.format(it.lastModified)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.5f),
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Full screen content area
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Title field - edge to edge
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { 
                        Text(
                            "Title", 
                            color = textColor.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.headlineLarge
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    ),
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
                    ),
                    singleLine = false,
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Content field - takes all remaining space
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { 
                        Text(
                            "Start writing...", 
                            color = textColor.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodyLarge
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = textColor,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                    ),
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
        
        // Floating toggle button for toolbar (only visible when toolbar is hidden)
        if (!showToolbar) {
            FloatingActionButton(
                onClick = { showToolbar = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = textColor.copy(alpha = 0.15f),
                contentColor = textColor
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Show Options"
                )
            }
        }
    }
}

@Composable
fun ColorPickerBar(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    textColor: Color
) {
    val colors = listOf(
        0xFFFFFFFF to "White",
        0xFFF28B82 to "Red",
        0xFFFBBC04 to "Orange",
        0xFFFFF475 to "Yellow",
        0xFFCCFF90 to "Green",
        0xFFA7FFEB to "Teal",
        0xFFCBF0F8 to "Blue",
        0xFFAECBFA to "Sky",
        0xFFD7AEFB to "Purple",
        0xFFFDCFE8 to "Pink",
        0xFFE6C9A8 to "Brown",
        0xFFE8EAED to "Gray"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = textColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Choose Color",
                style = MaterialTheme.typography.labelMedium,
                color = textColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(colors) { (color, name) ->
                    ColorOption(
                        color = color,
                        name = name,
                        isSelected = color == selectedColor,
                        onClick = { onColorSelected(color) },
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ColorOption(
    color: Long,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(color))
                .then(
                    if (isSelected) {
                        Modifier.border(3.dp, textColor, CircleShape)
                    } else {
                        Modifier.border(1.dp, textColor.copy(alpha = 0.3f), CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = getTextColorForBackground(Color(color)),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.7f)
        )
    }
}