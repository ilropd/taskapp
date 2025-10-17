package com.itb.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.itb.notesapp.ui.theme.NotesAppTheme
import com.itb.notesapp.ui.view.TaskList
import com.itb.notesapp.ui.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                val viewModel: TaskViewModel = koinViewModel()
                TaskList(viewModel)
            }
        }
    }
}
