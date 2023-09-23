package com.example.LockPro

import android.app.Application
import android.provider.Settings
import com.example.LockPro.local.Preferences

class MainApp : Application() {

    var preference: Preferences? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        preference = Preferences.getInstance(this)
        if (preference?.firstInstall == false) {
            preference?.firstInstall = true
            preference?.setValueCoin(30)
        }

    }

    companion object {
        var instance: MainApp? = null
        fun newInstance(): MainApp {
            if (instance == null) {
                instance = MainApp()
            }
            return instance!!
        }
    }
    val deviceId: String
        get() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}