package com.example.applock.service

import android.R
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.applock.BuildConfig
import com.example.applock.local.AppDatabase
import com.example.applock.local.Preferences
import com.example.applock.model.AppLock
import com.example.applock.ui.LoginActivity
import com.example.applock.ui.MainActivity
import com.example.applock.ui.app_list.AppListFragment.Companion.ENABLE_APP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer


open class AppLockService : Service() {


    private var wakeLock: WakeLock? = null
    private var appLockTask: AppLockTask? = null
    private var lastForegroundAppPackage: String? = null
    private val lockedAppPackageSet: MutableList<AppLock> = ArrayList()
    private var preferences: Preferences? = null
    private val getDataBroadcastReceiver = GetDataBroadcastReceiver()
    private var isShowActivity: Boolean = false
    private val appLockBinder: AppLockBinder = AppLockBinder()
    private var screenOnOffReceiver: ScreenOnOffReceiver? = null

    inner class AppLockBinder : Binder() {
        fun getAppLockService(): AppLockService = this@AppLockService
    }

    fun setShowActivity(isShowActivity: Boolean) {
        this.isShowActivity = isShowActivity
    }

    override fun onBind(intent: Intent): IBinder? {
        return appLockBinder
    }

    inner class GetDataBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent?.action == "getData") {
                getDataAppLock()
            } else if (intent != null && intent?.action == "set") {
                setShowActivity(intent.getBooleanExtra("isShowActivity", false))
            }

        }
    }


    override fun onCreate() {
        super.onCreate()
        startInForeground()
        acquireWakeLock()
        startAppLockTask()
        val filter = IntentFilter("getData")
        LocalBroadcastManager.getInstance(this).registerReceiver(getDataBroadcastReceiver, filter)
        registerReceiver()
    }

    override fun onDestroy() {
        releaseWakelock()
        unregisterReceiver(getDataBroadcastReceiver)
        super.onDestroy()
    }

    inner class ScreenOnOffReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = if (null != intent) intent.action else null
            if (Intent.ACTION_SCREEN_ON == action) {
            } else if (Intent.ACTION_SCREEN_OFF == action) {

            } else if (Intent.ACTION_USER_PRESENT == action) {
                if (lastForegroundAppPackage?.let { checkLockedApp(it) } == true) {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.putExtra("pkg", lastForegroundAppPackage)

                    intent.putExtra("pass",
                        lockedAppPackageSet.filter { it.packetName == lastForegroundAppPackage }
                            .getOrNull(0)?.pass
                    )

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }

    }


    private fun registerReceiver() {
        if (null == screenOnOffReceiver) {
            try {
                screenOnOffReceiver = ScreenOnOffReceiver()
                val intentFilter = IntentFilter()
                intentFilter.addAction(Intent.ACTION_USER_PRESENT)
                intentFilter.addAction(Intent.ACTION_SCREEN_ON)
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
                registerReceiver(screenOnOffReceiver, intentFilter)
            } catch (e: Throwable) {
                //
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        preferences = Preferences.getInstance(this)
        getDataAppLock()
        return START_STICKY
    }

    private fun acquireWakeLock() {
        try {
            // we need this lock so our service gets not affected by Doze Mode
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock =
                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock")
            wakeLock?.acquire(1000L)
        } catch (e: Throwable) {
            wakeLock = null
        }
    }

    private fun releaseWakelock() {
        if (null != wakeLock && wakeLock!!.isHeld) {
            try {
                wakeLock!!.release()
            } catch (e: Throwable) {
                //
            }
            wakeLock = null
        }
    }

    private fun getDataAppLock() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                lockedAppPackageSet.clear()
                AppDatabase.getInstance(this@AppLockService.applicationContext).getAppLockDao()
                    .getAllSever().forEach {
                        if (it.isLock) {
                            lockedAppPackageSet.add(it)
                        }
                    }

            } catch (e: Throwable) {
                Log.e("Lỗi ", e.message.toString())
            }
        }
    }


    private fun startInForeground() {
        try {
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.sym_def_app_icon)
                    .setContentTitle("app")
                    .setContentText("app")
                    .setContentIntent(pendingIntent)
            if (Build.VERSION.SDK_INT >= 26) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.description = NOTIFICATION_CHANNEL_DESC
                channel.enableVibration(false)
                channel.setShowBadge(false)
                //
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            startForeground(NOTIFICATION_ID, builder.build())
        } catch (e: Throwable) {
            //
        }
    }


    private fun checkLockedApp(pkg: String): Boolean {
        synchronized(lockedAppPackageSet) {
            try {
                return lockedAppPackageSet.any { it.packetName == pkg }
            } catch (e: Throwable) {
                //
            }
        }
        return false
    }


    private fun startAppLockTask() {
        if (null != appLockTask) {
            appLockTask?.cancel()
        }
        //
        appLockTask = AppLockTask(this) { pkg ->
            if (preferences?.getBoolean(ENABLE_APP) == true) {
                if (lastForegroundAppPackage == pkg) {
                    return@AppLockTask
                }
                if (isLaunchIntent(this.applicationContext, packageName) && checkLockedApp(pkg)) {
                    if (checkLockedApp(pkg) && !isShowActivity && BuildConfig.APPLICATION_ID != pkg) {
                        isShowActivity = true
                        lastForegroundAppPackage = pkg
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("pkg", pkg)
                        intent.putExtra("pass",
                            lockedAppPackageSet.filter { it.packetName == lastForegroundAppPackage }
                                .getOrNull(0)?.pass
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } else {
                    if (BuildConfig.APPLICATION_ID != pkg) {
                        lastForegroundAppPackage = null
                        isShowActivity = false
                    }
                }
            }
        }
        val timer = Timer()
        timer.scheduleAtFixedRate(appLockTask, 0, 100)
    }

    fun setLastForegroundAppPackage(lastForegroundAppPackage: String?) {
        this.lastForegroundAppPackage = lastForegroundAppPackage
    }

    fun isLaunchIntent(context: Context?, packageName: String): Boolean {
        if (null != context) {
            try {
                return context.packageManager.getLaunchIntentForPackage(packageName) != null
            } catch (e: Throwable) {
                //
            }
        }
        return false
    }


    companion object {
        fun startService(context: Context?) {
            if (!isMyServiceRunning(context, AppLockService::class.java)) {
                try {
                    context?.let {
                        ContextCompat.startForegroundService(
                            it,
                            Intent(context, AppLockService::class.java)
                        )
                    }

                } catch (e: Throwable) {
                    //
                }
            }
        }

        private fun isMyServiceRunning(context: Context?, serviceClass: Class<*>): Boolean {
            val manager = context?.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)!!) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        private const val NOTIFICATION_ID = 11
        private const val CHANNEL_ID = "default_app_lock_channel_id"
        private const val NOTIFICATION_CHANNEL_NAME = "AppLockService"
        private const val NOTIFICATION_CHANNEL_DESC =
            "Your phone  by Binary App Lock"
    }

}