package com.example.LockPro.ui.lock

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.LockPro.base.BaseViewModel
import com.example.LockPro.model.AppLock
import com.example.LockPro.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppLockViewModel : BaseViewModel() {
    private val repository = Repository()

    private val _listAppLock: MutableLiveData<List<AppLock>> = MutableLiveData()
    val listAppLock: LiveData<List<AppLock>> = _listAppLock

    fun getAll(): LiveData<List<AppLock>> = repository.getAll()

    fun removeAppLock(appLock: AppLock, onCallBack: () -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                async {
                    repository.update(appLock)
                }.await()
                withContext(Dispatchers.Main) {
                    onCallBack.invoke()
                }

            } catch (e: Exception) {
                //
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