package com.itb.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.itb.notesapp.model.Task

@Database(entities = [Task::class], version = 3, exportSchema = false)
abstract class TaskDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "tasks_db"

        @Volatile
        private var INSTANCE: TaskDataBase? = null

        fun getInstance(context: Context): TaskDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDataBase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

    }
}