package com.bing.mvvmbase

import android.view.View

import com.bing.mvvmbase.base.BaseActivity
import com.bing.mvvmbase.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
        override fun initViewModel() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun layoutId(): Int {
                return R.layout.activity_main
        }

        override fun bindAndObserve() {

        }

        override fun onClick(v: View) {

        }
}
