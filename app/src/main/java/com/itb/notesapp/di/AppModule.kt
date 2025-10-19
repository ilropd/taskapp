package com.itb.notesapp.di

import androidx.room.Room
import com.itb.notesapp.data.TaskDataBase
import com.itb.notesapp.model.TaskRepository
import com.itb.notesapp.ui.viewmodel.TaskDetailViewModel
import com.itb.notesapp.ui.viewmodel.TaskViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { TaskDataBase.getInstance(androidApplication()).taskDao() }

    single { TaskRepository(get()) }

    viewModel { TaskViewModel(get()) }

    viewModel { TaskDetailViewModel(get()) }

}