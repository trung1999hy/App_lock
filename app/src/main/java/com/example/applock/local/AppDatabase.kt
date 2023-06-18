package com.example.applock.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.applock.local.dao.AppLockDao
import com.example.applock.model.AppLock
import java.util.concurrent.Executors

@Database(
    entities = [AppLock::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAppLockDao(): AppLockDao
    val databaseWriteExecutor = Executors.newFixedThreadPool(2)


    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context, AppDatabase::class.java, "database-name"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
            }
            return instance!!
        }
    }
}