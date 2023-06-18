package com.example.applock.ui.lock_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.applock.R
import com.example.applock.databinding.FragmentLockBinding
import com.example.applock.local.Preferences
import com.example.applock.ui.MainActivity
import com.example.applock.ui.main.MainFragment
import com.example.applock.view.patternlockview.PatternLockView
import com.example.applock.view.patternlockview.listener.PatternLockViewListener
import com.example.applock.view.patternlockview.utils.PatternLockUtils
import com.example.login.base.BaseFragment


class LockFragment : BaseFragment<FragmentLockBinding>() {

    companion object {
        fun newInstance(changePass: Boolean) = LockFragment().apply {
            this.chanegpass = changePass
        }

        const val PASS_LOGIN = "PASS_LOGIN"
        const val KEY_BIOMETRICS = "KEY_BIOMETRICS"

    }

    private lateinit var preferences: Preferences
    private var pass: String = ""
    private var chanegpass: Boolean = false
    private var biometrics: Boolean = false


    private lateinit var viewModel: LockViewModel
    override fun getLayoutBinding(inflater: LayoutInflater): FragmentLockBinding =
        FragmentLockBinding.inflate(inflater).apply {
            viewModel = ViewModelProvider(this@LockFragment).get(LockViewModel::class.java)
        }

    override fun initView(savedInstanceState: Bundle?) {
        preferences = Preferences.getInstance(requireContext())
        pass = preferences.getString(PASS_LOGIN) ?: ""
        chanegpass = arguments?.getBoolean("changePass", false) ?: false
        biometrics = preferences.getBoolean(KEY_BIOMETRICS) == true;
        if (chanegpass || pass.isNullOrEmpty()) {
            pass = ""
            biometrics = false
            binding.fingerprint.visibility = View.GONE

        } else {
            binding.titlePass.setText("Vẽ mật khẩu để đăng nhập")
        }
        if (biometrics) {
            binding.fingerprint.visibility = View.VISIBLE
            set()
        }

    }


    override fun initAction(savedInstanceState: Bundle?) {
        binding.fingerprint.setOnClickListener {
            set()
        }
    }

    override fun listerData(savedInstanceState: Bundle?) {
        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

            }

            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                if (preferences.getString(PASS_LOGIN) == null || chanegpass) {
                    if (pass.isNullOrEmpty() && pattern?.size ?: 0 > 3) {
                        pass = PatternLockUtils.patternToString(
                            binding.patternLockView,
                            pattern
                        )
                        binding.patternLockView.clearPattern()
                        binding.titlePass.text = "Nhập lại mật khẩu để xác nhận"
                    } else if (pass == PatternLockUtils.patternToString(
                            binding.patternLockView,
                            pattern
                        ) && pattern?.size ?: 0 > 3
                    ) {
                        preferences.setString(PASS_LOGIN, pass)
                        (activity as MainActivity).openFragment(
                            this@LockFragment,
                            R.id.fragment_container,
                            (activity as MainActivity).mainFragment.javaClass,
                            null,
                            false
                        )
                    } else {
                        binding.patternLockView.clearPattern()
                        binding.titlePass.text = "Mật khẩu sai vui  lòng nhập lại "
                    }
                } else
                    if (pass == PatternLockUtils.patternToString(
                            binding.patternLockView,
                            pattern
                        ) && !pass.isNullOrEmpty() && pattern?.size ?: 0 > 3
                    ) {
                        (activity as MainActivity).openFragment(
                            this@LockFragment,
                            R.id.fragment_container,
                            MainFragment::class.java,
                            null,
                            false
                        )
                    } else if (!pass.isNullOrEmpty() && pattern?.size ?: 0 > 3) {
                        binding.patternLockView.clearPattern()
                        binding.titlePass.text = "Mật khẩu sai"
                    } else if (pass.isNullOrEmpty() && pattern?.size ?: 0 > 3) {
                        pass = PatternLockUtils.patternToMD5(
                            binding.patternLockView,
                            pattern
                        )
                        binding.patternLockView.clearPattern()
                        binding.titlePass.text = "Vui Lòng vẽ lại mật khẩu ?"
                    } else {
                        binding.patternLockView.clearPattern()
                        binding.titlePass.text = "Vui Lòng vẽ lại mật khẩu ?"
                    }
            }


            override fun onCleared() {

            }

        })

    }

    fun set() {
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(requireContext())
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                        }

                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult
                        ) {
                            super.onAuthenticationSucceeded(result)
                            (activity as MainActivity).openFragment(
                                this@LockFragment,
                                R.id.fragment_container,
                                MainFragment::class.java,
                                null,
                                false
                            )
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                        }
                    })

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for ${getString(R.string.app_name)}")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Cance")
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }

        }
    }
}