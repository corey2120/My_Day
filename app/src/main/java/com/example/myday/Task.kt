package com.example.myday

import kotlinx.serialization.Serializable
import java.util.UUID

enum class Priority {
    NONE, LOW, MEDIUM, HIGH
}

@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val description: String,
    val dateTime: String,
    val listId: String,
    var isCompleted: Boolean = false,
    val priority: Priority = Priority.NONE
)