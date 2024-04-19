package com.example.LockPro.ui.app_list

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import com.example.LockPro.base.BaseViewModel
import com.example.LockPro.model.AppLock
import com.example.LockPro.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AppListViewModel() : BaseViewModel() {
    private val repository = Repository()

    private fun getAllInstalledApps(
        context: Context,
        packageManager: PackageManager,
        packageName: String?
    ): ArrayList<ApplicationInfo> {
        val installedApps: ArrayList<ApplicationInfo> = ArrayList()
        try {
            val appList = packageManager.getInstalledApplications(0)
            for (appInfo in appList) {
                if (packageManager.getLaunchIntentForPackage(appInfo.packageName) != null && appInfo.packageName != packageName) {
                    installedApps.add(appInfo)
                }
            }
            return installedApps
        } catch (e: Throwable) {
            return arrayListOf()
        }
    }

    fun getAll(): LiveData<List<AppLock>> = repository.getAll()

    fun addList(context: Context, packageManager: PackageManager, packageName: String?) {
        scope.launch(Dispatchers.IO) {
            try {
                val getAllApp = async {
                    getAllInstalledApps(context, packageManager, packageName)
                }
                val allAppLocal = async {
                    repository.getAllService()
                }
                var getAllAppLock = allAppLocal.await()
                repository.getAllService()
                var listadd = getAllApp.await()
                val listApp = ArrayList<AppLock>()
                listadd.forEach { appInfo ->
                    val packageName = appInfo.packageName
                    val appName = appInfo.loadLabel(packageManager).toString()
                    if (getAllAppLock?.filter { appLock -> appLock.packetName == appInfo.packageName }
                            ?.isEmpty() == true || getAllAppLock.isEmpty())
                        listApp.add(
                            AppLock(
                                packetName = packageName,
                                isLock = false,
                                appName = appName,
                                pass = null
                            )
                        )
                }
                repository.addList(listApp)
            } catch (e: Exception) {

            }
        }
    }

    fun addAppLock(appLock: AppLock , callback:()-> Unit) {
        scope.launch(Dispatchers.IO) {
            async {
                repository.update(appLock)
                callback.invoke()
            }.await()

        }
    }
    fun addAllAppLock(callback: () -> Unit){
        scope.launch(Dispatchers.IO) {
            async {
                repository.getAllService().forEach {
                    repository.update(it.apply { isLock = true })
                }
callback.invoke()
            }.await()

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