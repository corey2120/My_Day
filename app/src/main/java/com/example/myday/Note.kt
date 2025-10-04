package com.example.myday

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val lastModified: Date = Date(),
    val color: Int = 0
)