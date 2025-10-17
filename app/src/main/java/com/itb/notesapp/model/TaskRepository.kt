package com.itb.notesapp.model

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.itb.notesapp.data.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository (private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}