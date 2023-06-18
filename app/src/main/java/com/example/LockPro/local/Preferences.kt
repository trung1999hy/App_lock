package com.example.LockPro.local

import android.content.Context
import android.content.SharedPreferences


class Preferences(var sharedPreferences: SharedPreferences? = null) {
    private val KEY_TYPE_ONE = "KEY_TYPE_ONE"
    private val KEY_TOTAL_COIN = "KEY_TOTAL_COIN" // coin
    private val KEY_FIRST_INSTALL = "KEY_FIRST_INSTALL" // coin
    private val INT_ZERO = 0 // coin
    fun setString(key: String, string: String) {
        sharedPreferences?.edit()?.putString(key, string)?.apply()
    }

    fun setBoolean(key: String, boolean: Boolean) {
        sharedPreferences?.edit()?.putBoolean(key, boolean)?.apply()
    }

    fun setInt(key: String, int: Int) {
        sharedPreferences?.edit()?.putInt(key, int)?.apply()
    }

    fun setFloat(key: String, float: Float) {
        sharedPreferences?.edit()?.putFloat(key, float)?.apply()
    }

    fun setLong(key: String, long: Long) {
        sharedPreferences?.edit()?.putLong(key, long)?.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences?.getString(key, null)
    }

    fun getBoolean(key: String): Boolean? {
        return sharedPreferences?.getBoolean(key, false)
    }

    fun getInt(key: String): Int? {
        return sharedPreferences?.getInt(key, 0)
    }

    fun getFloat(key: String): Long? {
        return sharedPreferences?.getLong(key, 0)
    }

    fun getLong(key: String): Float? {
        return sharedPreferences?.getFloat(key, 0f)
    }

    fun setValueTypeOne(value: String?) {
        sharedPreferences?.edit()?.putString(KEY_TYPE_ONE, value)?.apply()
    }

    var firstInstall: Boolean?
        get() = sharedPreferences?.getBoolean(KEY_FIRST_INSTALL, false)
        set(value) {
            value?.let { sharedPreferences?.edit()?.putBoolean(KEY_FIRST_INSTALL, it)?.apply() }
        }

    fun setValueCoin(value: Int) {
        sharedPreferences?.edit()?.putInt(KEY_TOTAL_COIN, value)?.apply()
    }

    fun getValueCoin(): Int? {
        return sharedPreferences?.getInt(KEY_TOTAL_COIN, INT_ZERO)
    }


    companion object {
        private val PREFS_NAME = "share_prefs"
        private var INSTANCE: Preferences? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(Preferences::class.java) {
            INSTANCE ?: Preferences(
                context.getSharedPreferences(PREFS_NAME, 0)
            ).also { INSTANCE = it }
        }
    }


}