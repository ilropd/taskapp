package com.itb.notesapp.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.itb.notesapp.model.Task
import com.itb.notesapp.ui.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(viewModel: TaskViewModel = koinViewModel()) {

    val tasks by viewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task",
                )
            }
        }

    ) { innerPadding ->
        ContentTasksView(viewModel, tasks, innerPadding)

        if (showDialog) {
            AddTaskDialog(onDismiss = { showDialog = false }, onConfirm = { description, dueDate ->
                showDialog = false
                viewModel.addTask(description, dueDate)
            })
        }
    }
}

@Composable
fun ContentTasksView(viewModel: TaskViewModel, tasks: List<Task>, innerPadding: PaddingValues) {

    LazyColumn(contentPadding = innerPadding) {
        if (tasks.isEmpty()) {
            item {
                EmptyTaskMessage()
            }
        } else {
            items(tasks) { task ->
                TaskItem(task = task, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EmptyTaskMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    )
    {
        Text(
            text = "No tasks",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }

}

@Composable
fun TaskItem(task: Task, viewModel: TaskViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteTaskDialog(onDismiss = { showDeleteDialog = false }, onConfirm = {
            showDeleteDialog = false
            viewModel.deleteTask(task)
        })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { viewModel.updateTask(task.copy(isCompleted = !task.isCompleted)) },
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.dueDate,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete this task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var isDateInvalid by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { newText ->
                        if (newText.all { it.isDigit() } && newText.length <= 8) {
                            dueDate = newText
                            isDateInvalid = if (newText.length == 8) {
                                !checkDate(newText)
                            } else {
                                false
                            }
                        }
                    },
                    label = { Text("Due Date") },
                    placeholder = { Text("DDMMYYYY") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = DateVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = isDateInvalid,
                    supportingText = {
                        if (isDateInvalid) {
                            Text(
                                "Invalid date",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (description.isNotBlank()) {
                        val formattedDate = "${dueDate.substring(0, 2)}.${dueDate.substring(2, 4)}.${dueDate.substring(4, 8)}"
                        onConfirm(description, formattedDate)
                    }
                },
                enabled = description.isNotBlank() && dueDate.length == 8 && !isDateInvalid
            ) {
                Text("ADD", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun DeleteTaskDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Task") },
        text = { Text(text = "Are you sure you want to delete this task?") },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("DELETE")
            }
        }
    )
}


class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1 || i == 3)
                out += "."
        }

        val dateOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 3) return offset + 1
                if (offset <= 8) return offset + 2
                return 10
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 10) return offset - 2
                return 8
            }
        }

        return TransformedText(
            AnnotatedString(out),
            dateOffsetTranslator
        )
    }
}

fun checkDate(date: String): Boolean {
    if (date.length != 8) return false

    return try {
        val day = date.substring(0, 2).toInt()
        val month = date.substring(2, 4).toInt()
        val year = date.substring(4, 8).toInt()

        if (year < 1900 || year > 2100) return false
        if (month < 1 || month > 12) return false

        val monthLength = when (month) {
            2 -> if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }

        day in 1..monthLength
    } catch (e: NumberFormatException) {
        false
    }
}
