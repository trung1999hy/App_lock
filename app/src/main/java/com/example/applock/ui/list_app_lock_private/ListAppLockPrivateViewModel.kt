package com.example.applock.ui.list_app_lock_private

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.applock.base.BaseViewModel
import com.example.applock.model.AppLock
import com.example.applock.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ListAppLockPrivateViewModel : BaseViewModel() {
    private val repository = Repository()

    fun getAll(): LiveData<List<AppLock>> = repository.getAll()
    fun updateAppLock(appLock: AppLock) {
        scope.launch(Dispatchers.IO) {
            async {
                repository.update(appLock)
            }.await()

        }
    }
    fun update(appLock: AppLock, onSuccessors: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                async {
                    repository.update(appLock)
                }.await()
                onSuccessors.invoke()
            } catch (e: Throwable) {

            }
        }
    }

    fun getAppIconByPackageName(context: Context, packageName: String?): Drawable? {
        try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName!!, 0)
            return appInfo.loadIcon(packageManager)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null

    }


}