package com.example.LockPro.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.ViewModelProvider
import com.example.LockPro.local.Preferences
import com.example.LockPro.ui.MainActivity
import com.example.LockPro.ui.inapp.PurchaseInAppActivity
import com.example.LockPro.ui.list_app_lock_private.ListAppLockPrivateFragment
import com.example.LockPro.ui.lock_app.LockFragment
import com.example.login.base.BaseFragment
import com.thn.applock.R
import com.thn.applock.databinding.FragmentSettingBinding

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    companion object {
        fun newInstance() = SettingFragment()
        const val KEY_BIOMETRICS = "KEY_BIOMETRICS"
        const val ENABLE_APP = "ENABLE_APP"
    }

    private lateinit var viewModel: SettingViewModel
    private var preferences: Preferences? = null
    private var biometrics: Boolean = false
    private var enabledApp: Boolean = false
    override fun getLayoutBinding(inflater: LayoutInflater): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(layoutInflater).apply {
            viewModel = ViewModelProvider(this@SettingFragment)[SettingViewModel::class.java]
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        preferences = context?.let { Preferences.getInstance(it) }
        biometrics = preferences?.getBoolean(KEY_BIOMETRICS) == true
        binding.switchBiometrics.isChecked = biometrics
        enabledApp = preferences?.getBoolean(ENABLE_APP) == true
        binding.switchEnbaledApp.isChecked = enabledApp
    }

    override fun initAction(savedInstanceState: Bundle?) {
        binding.switchBiometrics.setOnCheckedChangeListener { buttonView, isChecked ->
            preferences?.setBoolean(KEY_BIOMETRICS, isChecked)
        }
        binding.switchEnbaledApp.setOnCheckedChangeListener { buttonView, isChecked ->
            preferences?.setBoolean(ENABLE_APP, isChecked)
        }
        binding.changePass.setOnClickListener {
            val bundle = Bundle();
            bundle.putBoolean("changePass", true)
            (activity as MainActivity).openFragment(
                requireParentFragment(), R.id.fragment_container,
                LockFragment::class.java, bundle, true
            )
        }
        binding.listAppPrivate.setOnClickListener {
            (activity as MainActivity).openFragment(
                requireParentFragment(),
                R.id.fragment_container,
                ListAppLockPrivateFragment::class.java,
                null,
                true
            )
        }
        binding.store.setOnClickListener {
            startActivity(Intent(requireActivity(), PurchaseInAppActivity::class.java))
        }
    }

    override fun listerData(savedInstanceState: Bundle?) {

    }


}