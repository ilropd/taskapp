package com.itb.notesapp.model

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.itb.notesapp.data.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository (private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }

    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    fun getTasksByPriority(priority: String): Flow<List<Task>> {
        return taskDao.getTasksByPriority(priority)
    }

    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks()
    }

    fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasks()
    }

    fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query)
    }

}
