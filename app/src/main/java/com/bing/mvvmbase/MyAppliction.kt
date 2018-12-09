package com.bing.mvvmbase

import com.bing.mvvmbase.base.BaseApplication
import com.squareup.leakcanary.LeakCanary

class MyAppliction : BaseApplication() {
        override fun onCreate() {

                if (LeakCanary.isInAnalyzerProcess(this)) {
                        return
                }
                LeakCanary.install(this)
                super.onCreate()
        }
}
