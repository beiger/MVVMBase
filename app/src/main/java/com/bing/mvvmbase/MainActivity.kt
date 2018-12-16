package com.bing.mvvmbase

import android.view.View

import com.bing.mvvmbase.base.BaseActivity
import com.bing.mvvmbase.databinding.ActivityMainBinding

import androidx.lifecycle.ViewModelProviders

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

        override fun layoutId(): Int {
                return R.layout.activity_main
        }

        override fun bindAndObserve() {
        }

        override fun onClick(v: View) {

        }
}
