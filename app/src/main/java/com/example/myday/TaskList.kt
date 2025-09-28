package com.example.myday

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TaskList(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)