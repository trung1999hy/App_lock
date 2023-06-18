package com.example.LockPro.ui.app_lock_private

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.LockPro.model.AppLock
import com.example.LockPro.ui.MainActivity
import com.example.LockPro.view.patternlockview.PatternLockView
import com.example.LockPro.view.patternlockview.listener.PatternLockViewListener
import com.example.LockPro.view.patternlockview.utils.PatternLockUtils
import com.example.login.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.thn.applock.databinding.FragmentAppLockPrivateBinding

class AppLockPrivateFragment : BaseFragment<FragmentAppLockPrivateBinding>() {

    companion object {
        fun newInstance(appLock: AppLock) = AppLockPrivateFragment().apply {
            this.appLock = appLock
        }

    }

    private var pass: String? = null
    private var appLock: AppLock? = null
    private lateinit var viewModel: AppLockPrivateViewModel
    override fun getLayoutBinding(inflater: LayoutInflater): FragmentAppLockPrivateBinding {
        return FragmentAppLockPrivateBinding.inflate(inflater).apply {
            viewModel =
                ViewModelProvider(this@AppLockPrivateFragment)[AppLockPrivateViewModel::class.java]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appLock = (activity as MainActivity).getAppLock()
    }

    override fun initView(savedInstanceState: Bundle?) {
        if (appLock == null) {
            (activity as MainActivity).onBackPressed()
        }
        (activity as MainActivity).setVisibility(View.GONE)
    }

    override fun initAction(savedInstanceState: Bundle?) {

    }

    override fun listerData(savedInstanceState: Bundle?) {
        binding.patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {

            }

            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
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
                    appLock?.pass = pass
                    appLock?.isLock = true
                    appLock?.let {
                        viewModel.update(it) {
                            Snackbar.make(
                                binding.root,
                                "Bạn đã thêm thành công !",
                                Toast.LENGTH_LONG
                            ).show()
                            (activity as MainActivity).onBackPressed()
                        }
                    }
                } else {
                    binding.patternLockView.clearPattern()
                    binding.titlePass.text = "Mật khẩu sai vui  lòng nhập lại "
                }

            }


            override fun onCleared() {

            }

        })

    }

}