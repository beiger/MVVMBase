package com.bing.mvvmbase.base.viewpager

import android.os.Bundle
import android.view.MenuItem
import android.view.View

import com.bing.mvvmbase.base.BaseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseBottomNavigationActivity<DB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(), View.OnClickListener {
        lateinit var mBinding:DB
        lateinit var mViewModel: VM
        val mCompositeDisposable = CompositeDisposable()
        lateinit var mViewPager: ViewPager
        lateinit var mFragmentPagerAdapter: FragmentPagerAdapter

        private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item -> navigationItemSelected(item) }

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                onCreateFirst()
                mBinding = DataBindingUtil.setContentView(this, layoutId())
                initViewModel()
                lifecycle.addObserver(mViewModel)
                initViewPagerAndBottomNav()
                bindAndObserve()
        }

        open fun onCreateFirst() {

        }

        protected abstract fun layoutId(): Int
        abstract fun initViewModel()

        open fun initViewPagerAndBottomNav() {
                mViewPager = viewpager()
                mFragmentPagerAdapter = BaseFragmentPagerAdapter(supportFragmentManager, initFragments())
                mViewPager.adapter = mFragmentPagerAdapter
                navigationView().setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        }

        protected abstract fun viewpager(): ViewPager
        protected abstract fun navigationView(): BottomNavigationView
        protected abstract fun initFragments(): List<Fragment>
        protected abstract fun bindAndObserve()

        protected fun addOnClickListener(vararg views: View) {
                for (view in views) {
                        view.setOnClickListener(this)
                }
        }

        override fun onClick(v: View) {

        }

        protected fun addDisposable(disposable: Disposable) {
                mCompositeDisposable.add(disposable)
        }

        override fun onDestroy() {
                mCompositeDisposable.dispose()
                super.onDestroy()
        }

        protected abstract fun navigationItemSelected(item: MenuItem): Boolean
}
