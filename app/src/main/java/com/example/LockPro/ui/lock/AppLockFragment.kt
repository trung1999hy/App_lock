package com.example.LockPro.ui.lock

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.LockPro.base.PermissionActivity
import com.example.LockPro.ui.main.MainFragment
import com.example.LockPro.view.patternlockview.widget.AppLockItemAnimator
import com.example.login.base.BaseFragment
import com.thn.applock.databinding.FragmentAppLockBinding

class AppLockFragment : BaseFragment<FragmentAppLockBinding>() {

    companion object {
        fun newInstance(mainFragment: MainFragment) = AppLockFragment().apply {
            this.mainFragment = mainFragment
        }

    }

    private lateinit var viewModel: AppLockViewModel
    private lateinit var appListAdapter: AppLockAdapter
    private lateinit var mainFragment: MainFragment

    override fun getLayoutBinding(inflater: LayoutInflater): FragmentAppLockBinding {
        return FragmentAppLockBinding.inflate(inflater).apply {
            viewModel = ViewModelProvider(this@AppLockFragment)[AppLockViewModel::class.java]
        }
    }


    override fun initView(savedInstanceState: Bundle?) {
        appListAdapter = AppLockAdapter() { appLock ->
            (activity as PermissionActivity<*>).let {
                it.checkPermission() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
                            context
                        ) && isUsageAccessGranted(requireContext())
                    ) {
                        viewModel.removeAppLock(appLock.apply { isLock = false }) {
                            val intent = Intent("getData")
                            LocalBroadcastManager.getInstance(activity as PermissionActivity<*>)
                                .sendBroadcast(intent)
                        }
                    } else Toast.makeText(
                        requireContext(),
                        "Lỗi do cung cấp thiếu  quyền vui lòng cấp quyền để app hoạt đông",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val itemAnimator = AppLockItemAnimator()
        itemAnimator.setLocked(true)
        binding.rvListApp.itemAnimator = itemAnimator
        binding.rvListApp.setHasFixedSize(true)
        binding.rvListApp.adapter = appListAdapter
    }

    override fun initAction(savedInstanceState: Bundle?) {

    }

    fun isUsageAccessGranted(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun listerData(savedInstanceState: Bundle?) {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.rvListApp.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.rvListApp.visibility = View.VISIBLE
            }
        }

        viewModel.getAll().observe(viewLifecycleOwner) {
            var listData = it.filter { it.isLock }
            if (listData.isNotEmpty()) {
                listData.forEach { item ->
                    item.drawable =
                        viewModel.getAppIconByPackageName(requireContext(), item.packetName)
                }
                listData = listData.sortedBy { it.appName }
                appListAdapter.setData(listData)
                binding.title.visibility = View.GONE
            } else {
                appListAdapter.setData(listOf())
                binding.title.visibility = View.VISIBLE
            }

        }
    }
}