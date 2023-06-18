package com.example.LockPro.ui.list_app_lock_private

import android.app.AlertDialog
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.LockPro.MainApp
import com.example.LockPro.base.PermissionActivity
import com.example.LockPro.model.AppLock
import com.example.LockPro.ui.MainActivity
import com.example.LockPro.ui.app_lock_private.AppLockPrivateFragment
import com.example.LockPro.ui.inapp.PurchaseInAppActivity
import com.example.login.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.thn.applock.R
import com.thn.applock.databinding.FragmentListAppLockPrivateBinding

class ListAppLockPrivateFragment : BaseFragment<FragmentListAppLockPrivateBinding>() {

    companion object {
        fun newInstance() = ListAppLockPrivateFragment()
    }

    private lateinit var viewModel: ListAppLockPrivateViewModel
    private var adapter: AppLockAdapter? = null
    override fun getLayoutBinding(inflater: LayoutInflater): FragmentListAppLockPrivateBinding =
        FragmentListAppLockPrivateBinding.inflate(inflater).apply {
            viewModel =
                ViewModelProvider(this@ListAppLockPrivateFragment)[ListAppLockPrivateViewModel::class.java]
        }

    override fun initView(savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        adapter = AppLockAdapter {applock ->
            if (applock.pass.isNullOrEmpty()) {
                (activity as PermissionActivity<*>).let {
                    it.checkPermission() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
                                context
                            ) && isUsageAccessGranted(requireContext())
                        ) {
                            showDialog(applock)
                        } else Toast.makeText(
                            requireContext(),
                            "Lỗi do cung cấp thiếu  quyền vui lòng cấp quyền để app hoạt đông",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else showDialogRemove(applock)
        }
        binding.listApp.adapter = adapter
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


    override fun initAction(savedInstanceState: Bundle?) {

    }

    fun showDialog(appLock: AppLock) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Bạn có chắc khóa riêng ứng dụng này ko ? ")
            .setMessage("Dùng mất 1 vàng bạn có chắc không ?")
        alertDialog.setPositiveButton(
            "Đồng ý"
        ) { p0, p1 ->

            MainApp.newInstance().preference?.apply {
                if (getValueCoin() ?: 0 > 2) {
                    setValueCoin(getValueCoin()?.minus(2) ?: 0)
                    Toast.makeText(
                        requireContext(),
                        "Đã thêm  thành công và trù 2 vàng",
                        Toast.LENGTH_SHORT

                    ).show()
                    (activity as MainActivity).getCoin()
                    (activity as MainActivity).let {
                        it.setAppLock(appLock)
                        it.openFragment(
                            this@ListAppLockPrivateFragment,
                            R.id.fragment_container, AppLockPrivateFragment::class.java, null, true
                        )
                    }

                } else startActivity(
                    Intent(
                        requireActivity(),
                        PurchaseInAppActivity::class.java
                    )
                )


            }
        }
        alertDialog.setNegativeButton("Hủy") { p0, p1 ->
            p0.dismiss()
        }
        alertDialog.create().show()
    }

    fun showDialogRemove(appLock: AppLock) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(" Xóa")
            .setMessage("Bạn có chắc muốn xóa khóa riêng ứng dụng này ko ?")
        alertDialog.setPositiveButton(
            "Đồng ý"
        ) { p0, p1 ->
            appLock.pass = null
            viewModel.update(appLock) {
                Snackbar.make(
                    requireActivity(),
                    binding.root, "Bạn đã xóa thành công ", Toast.LENGTH_LONG
                ).show()
            }
            p0.dismiss()
        }
        alertDialog.setNegativeButton("Hủy") { p0, p1 ->
            p0.dismiss()
        }
        alertDialog.create().show()
    }


    override fun listerData(savedInstanceState: Bundle?) {
        viewModel.getAll().observe(viewLifecycleOwner) {
            val intent = Intent("getData")
            val listData = it.filter { !it.isLock }
            listData.forEach {
                it.drawable = viewModel.getAppIconByPackageName(requireContext(), it.packetName)
            }
            requireActivity().sendBroadcast(intent)
            adapter?.submitList(it)
        }
    }
}