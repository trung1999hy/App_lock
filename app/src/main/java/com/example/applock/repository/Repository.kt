package com.example.applock.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.applock.MainApp
import com.example.applock.local.AppDatabase
import com.example.applock.model.AppLock

class Repository(){
    private val appDatabase = AppDatabase.getInstance(MainApp.newInstance().applicationContext)

     fun getAll(): LiveData<List<AppLock>> = appDatabase.getAppLockDao().getAll()
    suspend fun add(appLock : AppLock) = appDatabase.getAppLockDao().insertAll(appLock)
    suspend fun addList(list: ArrayList<AppLock>) = appDatabase.getAppLockDao().insetList(list)
    suspend fun remove(appLock: AppLock) = appDatabase.getAppLockDao().delete(appLock)
    suspend fun update(appLock: AppLock) = appDatabase.getAppLockDao().update(appLock)
    suspend fun getAllService() = appDatabase.getAppLockDao().getAllSever()

}