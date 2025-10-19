package com.itb.notesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.notesapp.model.Task
import com.itb.notesapp.model.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            val t = repository.getTaskById(taskId)
            _task.value = t
        }
    }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            repository.updateTask(updatedTask)
            _task.value = updatedTask
        }
    }
}