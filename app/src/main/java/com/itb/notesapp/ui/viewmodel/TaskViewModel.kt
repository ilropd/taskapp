package com.itb.notesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.notesapp.model.Task
import com.itb.notesapp.model.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            if (repository.allTasks.first().isEmpty()) {

                repository.addTask(
                    Task(
                        id = 1,
                        title = "Goooooooooo",
                        dueDate = "03.12.2025",
                        isCompleted = false
                    )
                )
                repository.addTask(
                    Task(
                        id = 2,
                        title = "Drink coffee",
                        dueDate = "28.01.2026",
                        isCompleted = true
                    )
                )
                repository.addTask(
                    Task(
                        id = 3,
                        title = "Another task a lot of text to check if it works",
                        dueDate = "11.11.2028",
                        isCompleted = false
                    )
                )
            }

            repository.allTasks.collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun addTask(title: String, dueDate: String) {
        viewModelScope.launch {
            val newTask = Task(title = title, dueDate = dueDate)
            repository.addTask(newTask)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun getFilteredTasks(
        priority: String? = null,
        completed: Boolean? = null,
        searchQuery: String? = null
    ): Flow<List<Task>> {
        return when {
            searchQuery != null -> repository.searchTasks(searchQuery)
            completed == true -> repository.getCompletedTasks()
            completed == false -> repository.getPendingTasks()
            priority != null -> repository.getTasksByPriority(priority)
            else -> tasks
        }
    }

}
