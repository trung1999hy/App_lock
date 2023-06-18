package com.example.applock.model

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity
data class AppLock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "packetName")
    val packetName: String?,
    @ColumnInfo(name = "pass")
    var pass: String?,
    @ColumnInfo(name = "lock")
    var isLock: Boolean = false,
    @ColumnInfo(name = "appName")
    var appName: String?


    ): Serializable {
   @Ignore var drawable: Drawable? = null
}


