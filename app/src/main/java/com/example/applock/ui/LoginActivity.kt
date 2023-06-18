package com.example.applock.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.applock.R
import com.example.applock.databinding.ActivityLoginBinding
import com.example.applock.databinding.ViewPatternOverlayBinding
import com.example.applock.local.Preferences
import com.example.applock.service.AppLockService
import com.example.applock.ui.lock_app.LockFragment
import com.example.applock.ui.setting.SettingFragment
import com.example.applock.view.patternlockview.PatternLockView
import com.example.applock.view.patternlockview.listener.PatternLockViewListener
import com.example.applock.view.patternlockview.utils.PatternLockUtils
import com.example.athu.base.BaseActivity


class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private var preferences: com.example.applock.local.Preferences? = null
    private var biometrics: Boolean = false
    private var pkg: String? = null
    private var appBinder: AppLockService.AppLockBinder? = null
    private var appLockService: AppLockService? = null
    private var isBinder: Boolean = false
    private var windowManager: WindowManager? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var viewPatternOverlayBinding: ViewPatternOverlayBinding? = null
    private var password: String? = null

    private var params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // or other appropriate window type
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // optional flags
        PixelFormat.TRANSLUCENT // optional pixel format
    )


    override fun getLayoutBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        viewPatternOverlayBinding = ViewPatternOverlayBinding.inflate(layoutInflater, null, false)
        pkg = intent.getStringExtra("pkg")
        preferences = Preferences.getInstance(this)
        biometrics = preferences?.getBoolean(SettingFragment.KEY_BIOMETRICS) == true
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
                this
            )
        ) windowManager?.addView(viewPatternOverlayBinding?.root, params)
        else {
            Toast.makeText(
                this,
                "Lỗi do cung cấp thiếu  quyền vui lòng cấp quyền để app hoạt đông",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
        viewPatternOverlayBinding?.ivIcon?.setImageDrawable(getAppIconByPackageName(this, pkg))
        if (biometrics) {
            runOnUiThread {
                viewPatternOverlayBinding?.fingerprint?.visibility = View.VISIBLE
                set()
            }
        }
        viewPatternOverlayBinding?.fingerprint?.setOnClickListener {
            set()
        }
        viewPatternOverlayBinding?.patternLockView?.addPatternLockListener(object :
            PatternLockViewListener {
            override fun onStarted() {}
            override fun onProgress(progressPattern: List<PatternLockView.Dot>) {}
            override fun onComplete(pattern: List<PatternLockView.Dot>) {
                password = if (intent.getStringExtra("pass") != null) {
                    intent.getStringExtra("pass")
                } else
                    preferences?.getString(LockFragment.PASS_LOGIN)

                val pass = PatternLockUtils.patternToString(
                    viewPatternOverlayBinding?.patternLockView,
                    pattern
                )
                if (pass == password && password != null) {
                    appLockService?.setLastForegroundAppPackage(pkg)
                    biometricPrompt?.cancelAuthentication()
                    windowManager?.removeViewImmediate(viewPatternOverlayBinding?.root)
                    finish()
                } else {
                    runOnUiThread {
                        viewPatternOverlayBinding?.titlePass?.text =
                            "Mật khẩu không đúng vui lòng vẽ lại !"
                    }
                }
                viewPatternOverlayBinding?.patternLockView?.clearPattern()
            }

            override fun onCleared() {}
        })
    }


    private fun getAppIconByPackageName(context: Context, packageName: String?): Drawable? {
        try {
            val packageManager = context.packageManager
            val appInfo = packageName?.let { packageManager.getApplicationInfo(it, 0) }
            return appInfo?.loadIcon(packageManager)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null

    }


    override fun onBackPressed() {

    }

    override fun onStop() {
        val intent = Intent("set")
        intent.putExtra("isShowActivity", false)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        if (viewPatternOverlayBinding?.root?.isAttachedToWindow == true) {
            windowManager?.removeView(viewPatternOverlayBinding?.root);
            biometricPrompt?.cancelAuthentication()
        } else {
        }
        finish()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun set() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence
                        ) {

                            super.onAuthenticationError(errorCode, errString)
                            runOnUiThread {
                                viewPatternOverlayBinding?.titlePass?.text =
                                    "Mật khẩu không đúng vui lòng vẽ lại !"

                            }
                            biometricPrompt?.cancelAuthentication()

                        }

                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult
                        ) {
                            super.onAuthenticationSucceeded(result)
                            finish()
                        }

                        override fun onAuthenticationFailed() {

                            super.onAuthenticationFailed()
                            runOnUiThread {
                                viewPatternOverlayBinding?.titlePass?.text =
                                    "Mật khẩu không đúng vui lòng vẽ lại !"

                            }
                        }
                    })
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for ${getString(R.string.app_name)}")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Cancel")
                    .build()
                promptInfo?.let {
                    biometricPrompt?.authenticate(it)
                }

            }

        }

    }


}