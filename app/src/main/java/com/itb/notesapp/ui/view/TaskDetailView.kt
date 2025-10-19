package com.itb.notesapp.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.itb.notesapp.model.Priority
import com.itb.notesapp.ui.viewmodel.TaskDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailView(
    taskId: Int,
    navController: NavController,
    viewModel: TaskDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(key1 = taskId) {
        viewModel.loadTask(taskId)
    }

    val task by viewModel.task.collectAsState()

    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var category by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }

    // Update the local state when the task is loaded from the ViewModel
    LaunchedEffect(task) {
        task?.let {
            title = it.title
            dueDate = it.dueDate
            priority = it.priority
            category = it.category
            notes = it.notes
            isCompleted = it.isCompleted
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Task info") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })
        }) { paddingValues ->
        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    modifier = Modifier.fillMaxWidth()
                )

                PrioritySelector(currentPriority = priority, onPriorityChange = { priority = it })

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completed")
                }


                val isModified = task?.let {
                    it.title != title ||
                            it.dueDate != dueDate ||
                            it.priority != priority ||
                            it.category != category ||
                            it.notes != notes ||
                            it.isCompleted != isCompleted
                } ?: false

                Button(
                    onClick = {
                        task?.let {
                            val updatedTask = it.copy(
                                title = title,
                                dueDate = dueDate,
                                priority = priority,
                                category = category,
                                notes = notes,
                                isCompleted = isCompleted
                            )
                            viewModel.updateTask(updatedTask)
                        }
                    }, modifier = Modifier.fillMaxWidth(), enabled = isModified
                ) {
                    Text("Save changes")
                }
            }
        }
    }
}
