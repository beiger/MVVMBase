package com.bing.mvvmbase.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment<DB : ViewDataBinding, VM : BaseViewModel, AVM : BaseViewModel> : Fragment(), View.OnClickListener {
        lateinit var mBinding: DB
        lateinit var mViewModel: VM
        lateinit var mActivityViewModel: AVM
        var mCompositeDisposable = CompositeDisposable()

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                arguments?.let {
                        handleArguments()
                }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
                mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
                initView()
                return mBinding.root
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
                super.onActivityCreated(savedInstanceState)
                mActivityViewModel = ViewModelProviders.of(activity!!).get(mActivityViewModel.javaClass)
                mViewModel = ViewModelProviders.of(this).get(mViewModel.javaClass)
                lifecycle.addObserver(mViewModel!!)
                bindAndObserve()
        }

        abstract fun handleArguments()
        abstract fun layoutId(): Int

        /**
         * 此时还没有viewModel
         */
        abstract fun initView()
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
