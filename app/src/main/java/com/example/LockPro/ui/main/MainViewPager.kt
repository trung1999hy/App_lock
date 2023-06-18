package com.example.LockPro.ui.main


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainViewPager(fragmentManager: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fragmentManager, behavior) {
    private var listFragment: ArrayList<Fragment> = arrayListOf()
    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getItem(position: Int): Fragment {
        return listFragment[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (position == 0) {
            return "UnLock"
        } else if (position == 1) {
            return "Lock"
        }else if (position == 2){
            return "Setting"
        }
        return super.getPageTitle(position)

    }

    fun setFragment(list: ArrayList<Fragment>) {
        this.listFragment = list
        notifyDataSetChanged()
    }
}