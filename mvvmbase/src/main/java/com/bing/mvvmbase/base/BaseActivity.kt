package com.bing.mvvmbase.base

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders

abstract class BaseActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(), View.OnClickListener {

        val mBinding = lazy {
                DataBindingUtil.setContentView(this, layoutId()) as DB
        }
        lateinit var mViewModel: VM
        var mCompositeDisposable = CompositeDisposable()

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                onCreateFirst()
                mViewModel = ViewModelProviders.of(this).get(mViewModel.javaClass)
                lifecycle.addObserver(mViewModel)
                bindAndObserve()
        }

        protected fun onCreateFirst() {

        }

        abstract fun layoutId(): Int
        abstract fun bindAndObserve()

        fun addOnClickListener(vararg views: View) {
                for (view in views) {
                        view.setOnClickListener(this)
                }
        }

        override fun onClick(v: View) {

        }

        fun addDisposable(disposable: Disposable) {
                mCompositeDisposable.add(disposable)
        }

        override fun onDestroy() {
                mCompositeDisposable.dispose()
                super.onDestroy()
        }
}
