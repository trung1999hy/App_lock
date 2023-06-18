package com.example.applock.ui.list_app_lock_private

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.applock.MainApp
import com.example.applock.R
import com.example.applock.databinding.FragmentListAppLockPrivateBinding
import com.example.applock.model.AppLock
import com.example.applock.ui.MainActivity
import com.example.applock.ui.app_lock_private.AppLockPrivateFragment
import com.example.applock.ui.inapp.PurchaseInAppActivity
import com.example.login.base.BaseFragment
import com.google.android.material.snackbar.Snackbar

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
        adapter = AppLockAdapter {
            if (it.pass.isNullOrEmpty())
                showDialog(it)
            else showDialogRemove(it)
        }
        binding.listApp.adapter = adapter
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