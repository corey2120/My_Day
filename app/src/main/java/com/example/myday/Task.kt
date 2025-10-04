package com.example.myday

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class Priority {
    NONE, LOW, MEDIUM, HIGH
}

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = TaskList::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["listId"])]
)
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val description: String,
    val dateTime: String,
    val listId: String,
    var isCompleted: Boolean = false,
    val priority: Priority = Priority.NONE
)