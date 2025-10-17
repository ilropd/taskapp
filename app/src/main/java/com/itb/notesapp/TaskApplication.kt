package com.itb.notesapp

import android.app.Application
import com.itb.notesapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TaskApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TaskApplication)
            modules(appModule)
        }
    }
}