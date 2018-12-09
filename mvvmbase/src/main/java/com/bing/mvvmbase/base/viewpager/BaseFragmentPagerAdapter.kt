package com.bing.mvvmbase.base.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BaseFragmentPagerAdapter(fm: FragmentManager, protected var mFragments: List<Fragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
                return mFragments[position]
        }

        override fun getCount(): Int {
                return mFragments.size
        }
}
