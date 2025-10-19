package com.itb.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.LOW,
    val dueDate: String,
    val category: String = "",
    val notes: String = ""
)