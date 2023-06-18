package com.example.LockPro.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.LockPro.MainApp
import com.example.LockPro.base.PermissionActivity
import com.example.LockPro.model.AppLock
import com.example.LockPro.service.AppLockService
import com.example.LockPro.ui.inapp.PurchaseInAppActivity
import com.example.LockPro.ui.lock_app.LockFragment
import com.example.LockPro.ui.main.MainFragment
import com.thn.applock.R
import com.thn.applock.databinding.ActivityMainBinding


class MainActivity : PermissionActivity<ActivityMainBinding>() {
    private var appLock: AppLock? = null
    var mainFragment = MainFragment.newInstance()
    override fun getLayoutBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        AppLockService.startService(this)

        val lockFragment = LockFragment.newInstance(false)
        openFragment(null, R.id.fragment_container, lockFragment.javaClass, null, false)
        setVisibility(View.GONE)
        getCoin()
        binding.coin.click {
            startActivity(Intent(this, PurchaseInAppActivity::class.java))
        }
    }

    fun getCoin() {
        binding.coin.text = MainApp.newInstance()?.preference?.getValueCoin().toString()
    }

    fun setAppLock(appLock: AppLock) {
        this.appLock = appLock
    }

    fun getAppLock(): AppLock? = appLock
    override fun onBackPressed() {
        super.onBackPressed()
        setVisibility(View.VISIBLE)
    }

    fun setVisibility(isVisibility: Int) {
        binding.coin.visibility = isVisibility
    }

}

