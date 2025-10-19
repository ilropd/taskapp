@file:OptIn(ExperimentalMaterial3Api::class)

package com.itb.notesapp.ui.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itb.notesapp.model.Task
import com.itb.notesapp.ui.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel
import com.itb.notesapp.R
import com.itb.notesapp.model.Priority
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListView(
    viewModel: TaskViewModel = koinViewModel(),
    onTaskClick: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    var showCompleted by remember { mutableStateOf<Boolean?>(null) }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }
    var showOverdue by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredTasks by remember(showCompleted, selectedPriority, searchQuery) {
        viewModel.getFilteredTasks(
            priority = selectedPriority?.name,
            completed = showCompleted,
            searchQuery = searchQuery.takeIf { it.isNotBlank() }
        )
    }.collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.FilterAlt,
                            tint = Color.White,
                            contentDescription = "Filters"
                        )
                    }
                },
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

        Column {

            val activeFilters = mutableListOf<String>()
            if (showCompleted == true) activeFilters += "Completed"
            if (showCompleted == false) activeFilters += "Pending"
            selectedPriority?.let { activeFilters += "Priority: ${it.name}" }
            if (searchQuery.isNotBlank()) activeFilters += "Search: \"$searchQuery\""
            if (showOverdue) activeFilters += "Overdue"

            ContentTasksView(
                viewModel,
                filteredTasks,
                innerPadding,
                onTaskClick,
                activeFilters
            )

            if (showDialog) {
                AddTaskDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { description, dueDate ->
                        showDialog = false
                        viewModel.addTask(description, dueDate)
                    })
            }

            if (showFilterDialog) {
                FilterTaskDialog(
                    onDismiss = { showFilterDialog = false },
                    showCompleted = showCompleted,
                    onCompletedChange = { showCompleted = it },
                    selectedPriority = selectedPriority,
                    onPriorityChange = { selectedPriority = it },
                    showOverdue = showOverdue,
                    onOverdueChange = { showOverdue = it },
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it }, onConfirm = { showFilterDialog = false }
                )
            }
        }
    }
}

@Composable
fun ContentTasksView(
    viewModel: TaskViewModel,
    tasks: List<Task>,
    innerPadding: PaddingValues,
    onTaskClick: (Int) -> Unit,
    activeFilters: List<String>
) {

    LazyColumn(contentPadding = innerPadding) {

        if (tasks.isNotEmpty() && activeFilters.isNotEmpty()) {
            item {
                Text(
                    text = "Active filters: ${activeFilters.joinToString(", ")}",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(8.dp)
                )
            }
        }

        if (tasks.isEmpty()) {
            item {
                EmptyTaskMessage()
            }
        } else {
            items(tasks) { task ->
                TaskItem(task = task, viewModel = viewModel, onTaskClick = onTaskClick)
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
            text = stringResource(id = R.string.no_task),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }

}

@Composable
fun TaskItem(
    task: Task,
    viewModel: TaskViewModel,
    onTaskClick: (Int) -> Unit
) {
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
            val lineColor = when (task.priority) {
                Priority.LOW -> Color.DarkGray
                Priority.MEDIUM -> Color(0xFFFFC107)
                Priority.HIGH -> Color.Red
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .background(lineColor)
            )

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
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.dueDate,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "More info",
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onTaskClick(task.id) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.LOW) }
    var isDateInvalid by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
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

                PrioritySelector(
                    currentPriority = priority,
                    onPriorityChange = { newPriority ->
                        priority = newPriority
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
                        val formattedDate = "${dueDate.substring(0, 2)}.${
                            dueDate.substring(
                                2,
                                4
                            )
                        }.${dueDate.substring(4, 8)}"
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
fun FilterTaskDialog(
    onDismiss: () -> Unit,
    showCompleted: Boolean?,
    onCompletedChange: (Boolean?) -> Unit,
    selectedPriority: Priority?,
    onPriorityChange: (Priority?) -> Unit,
    showOverdue: Boolean,
    onOverdueChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filters") },
        text = {
            TaskFilters(
                showCompleted = showCompleted,
                onCompletedChange = onCompletedChange,
                selectedPriority = selectedPriority,
                onPriorityChange = onPriorityChange,
                showOverdue = showOverdue,
                onOverdueChange = onOverdueChange,
                searchQuery = searchQuery,
                onSearchChange = onSearchChange
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled =
                    showCompleted != null
                            || selectedPriority != null
                            || showOverdue
                            || searchQuery.isNotBlank()
            ) {
                Text("FILTER")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
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

@Composable
fun PrioritySelector(
    currentPriority: Priority,
    onPriorityChange: (Priority) -> Unit
) {

    val priority = listOf(
        Priority.LOW to "Low",
        Priority.MEDIUM to "Medium",
        Priority.HIGH to "High"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 12.dp, bottom = 8.dp, start = 12.dp, end = 12.dp)
    ) {
        Column {
            Text(
                text = "Choose priority",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                priority.forEach { (p, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = currentPriority == p,
                            onClick = { onPriorityChange(p) })
                        Text(label)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskFilters(
    showCompleted: Boolean?,
    onCompletedChange: (Boolean?) -> Unit,
    selectedPriority: Priority?,
    onPriorityChange: (Priority?) -> Unit,
    showOverdue: Boolean,
    onOverdueChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "Search by name",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Enter task name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Divider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Status",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterButton(
                text = "Completed",
                selected = showCompleted == true,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onCompletedChange(if (showCompleted == true) null else true) }
            )
            FilterButton(
                text = "Pending",
                selected = showCompleted == false,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onCompletedChange(if (showCompleted == false) null else false) }
            )
            FilterButton(
                text = "Overdue",
                selected = showOverdue,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onOverdueChange(!showOverdue) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Priority",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH).forEach { priority ->
                val color = MaterialTheme.colorScheme.primary
                FilterButton(
                    text = when (priority.name) {
                        "LOW" -> "Low"
                        "MEDIUM" -> "Medium"
                        "HIGH" -> "High"
                        else -> "All"
                    },
                    selected = selectedPriority == priority,
                    color = color,
                    onClick = { onPriorityChange(if (selectedPriority == priority) null else priority) }
                )
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) BorderStroke(1.dp, color) else null,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected) color else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.isOverdue(): Boolean {
    val parts = this.split(".")
    if (parts.size != 3) return false
    return try {
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        val taskDate = java.time.LocalDate.of(year, month, day)
        taskDate.isBefore(java.time.LocalDate.now()) && !this.contains(
            "completed",
            ignoreCase = true
        )
    } catch (e: Exception) {
        false
    }
}