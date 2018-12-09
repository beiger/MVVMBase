package com.bing.mvvmbase.base.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class BaseFragmentStatePagerAdapter<E>(fm: FragmentManager, protected var mData: List<E>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
                return null
        }

        override fun getCount(): Int {
                return mData.size
        }
}
