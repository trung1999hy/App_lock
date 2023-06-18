package com.example.applock.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.example.applock.MainApp
import com.example.applock.databinding.FragmentMainBinding
import com.example.applock.ui.MainActivity
import com.example.applock.ui.app_list.AppListFragment
import com.example.applock.ui.click
import com.example.applock.ui.inapp.PurchaseInAppActivity
import com.example.applock.ui.lock.AppLockFragment
import com.example.applock.ui.setting.SettingFragment
import com.example.login.base.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>() {

    companion object {
        fun newInstance() = MainFragment()
    }

    val appListFragment = AppListFragment.newInstance(this)
    val appLockFragment = AppLockFragment.newInstance(mainFragment = this)
    val settingFragment = SettingFragment.newInstance()
    private val listFragment =
        arrayListOf<Fragment>(appListFragment, appLockFragment, settingFragment)
    private lateinit var mainViewPager: MainViewPager

    private lateinit var viewModel: MainViewModel
    override fun getLayoutBinding(layoutInflater: LayoutInflater): FragmentMainBinding {
        return FragmentMainBinding.inflate(layoutInflater).apply {
            viewModel = ViewModelProvider(this@MainFragment)[MainViewModel::class.java]
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        (activity as MainActivity).setVisibility(View.VISIBLE)
        mainViewPager = MainViewPager(
            childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        binding.viewPager.adapter = mainViewPager
        mainViewPager.setFragment(listFragment)
        binding.viewPager.offscreenPageLimit = 2
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun initAction(savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun listerData(savedInstanceState: Bundle?) {

    }


}