package com.itb.notesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.notesapp.model.Task
import com.itb.notesapp.model.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository): ViewModel()
{
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            if (repository.allTasks.first().isEmpty()) {

                repository.insertTask(Task(id = 1, title = "Goooooooooo", dueDate = "03 october", isCompleted = false))
                repository.insertTask(Task(id = 2, title = "Drink coffee", dueDate = "03 october", isCompleted = true))
                repository.insertTask(Task(id = 3, title = "Another task a lot of text to check if it works", dueDate = "03 october 1958 a lot of text to check if it works", isCompleted = false))
            }
            
            repository.allTasks.collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun addTask(title: String, dueDate: String) {
        viewModelScope.launch {
            val newTask = Task(title = title, dueDate = dueDate)
            repository.insertTask(newTask)
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

}
