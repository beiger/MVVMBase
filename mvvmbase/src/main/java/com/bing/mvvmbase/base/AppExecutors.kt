package com.bing.mvvmbase.base

import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor
import java.util.concurrent.Executors

object AppExecutors {
        val diskIO = Executors.newSingleThreadExecutor()
        val networkIO = Executors.newFixedThreadPool(3)
        val nainThread = MainThreadExecutor()

        class MainThreadExecutor : Executor {
                private val mainThreadHandler = Handler(Looper.getMainLooper())

                override fun execute(command: Runnable) {
                        mainThreadHandler.post(command)
                }
        }
}
