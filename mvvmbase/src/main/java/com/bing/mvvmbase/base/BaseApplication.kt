package com.bing.mvvmbase.base

import android.app.Application
import android.content.Context

import com.blankj.utilcode.util.Utils

import androidx.multidex.MultiDex

open class BaseApplication : Application() {

        override fun attachBaseContext(base: Context) {
                super.attachBaseContext(base)
                MultiDex.install(this)
        }

        override fun onCreate() {
                super.onCreate()
                sContext = this
                Utils.init(this)
        }

        companion object {
                lateinit var sContext: BaseApplication
        }
}
