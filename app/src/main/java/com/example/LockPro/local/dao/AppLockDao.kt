package com.example.LockPro.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.LockPro.model.AppLock

@Dao
interface AppLockDao {
    @Query("SELECT * FROM AppLock")
    fun getAll(): LiveData<List<AppLock>>
    @Query("SELECT * FROM AppLock")
    fun getAllSever(): List<AppLock>
    @Insert
    suspend fun insertAll(vararg users: AppLock)

    @Insert
    suspend fun insetList(list: ArrayList<AppLock>)

    @Update
    suspend fun update(appLock: AppLock)

    @Delete
    suspend fun delete(user: AppLock)
}