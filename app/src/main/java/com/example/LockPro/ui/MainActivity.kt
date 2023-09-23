package com.example.LockPro.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.LockPro.MainApp
import com.example.LockPro.base.PermissionActivity
import com.example.LockPro.local.DataController
import com.example.LockPro.model.AppLock
import com.example.LockPro.model.User
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
        getData()
        binding.coin.click {
            startActivity(Intent(this, PurchaseInAppActivity::class.java))
        }
    }



    private fun setDataBaseGold() {
        val dataController = DataController(MainApp.newInstance()?.deviceId ?: "")
        dataController.writeNewUser(MainApp.newInstance()?.deviceId ?: "", 30)
    }

    fun getData() {
        val dataController = DataController(MainApp.newInstance()?.deviceId ?: "")
        dataController.setOnListenerFirebase(object : DataController.OnListenerFirebase {
            override fun onCompleteGetUser(user: User?) {
                user?.let {
                    MainApp.newInstance()?.preference?.setValueCoin(user.coin)
                    binding.coin.text = String.format(
                        resources.getString(R.string.amount_gold),
                        MainApp.newInstance()?.preference?.getValueCoin()
                    )
                } ?: kotlin.run {
                    setDataBaseGold()
                }
            }

            override fun onSuccess() {

            }

            override fun onFailure() {
                Toast.makeText(this@MainActivity, "Có lỗi kết nối đến server!", Toast.LENGTH_LONG)
                    .show()
            }
        })
        dataController.user
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

