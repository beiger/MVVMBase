package com.bing.mvvmbase.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel(application: Application) : AndroidViewModel(application), DefaultLifecycleObserver {
        protected var mCompositeDisposable = CompositeDisposable()
        protected var mAppExecutors = AppExecutors

        fun addDisposable(disposable: Disposable) {
                mCompositeDisposable.add(disposable)
        }

        override fun onCleared() {
                mCompositeDisposable.dispose()
                super.onCleared()
        }

        override fun onCreate(owner: LifecycleOwner) {

        }

        override fun onResume(owner: LifecycleOwner) {

        }

        override fun onStart(owner: LifecycleOwner) {

        }

        override fun onPause(owner: LifecycleOwner) {

        }

        override fun onStop(owner: LifecycleOwner) {

        }

        override fun onDestroy(owner: LifecycleOwner) {

        }
}
