package com.bing.mvvmbase.base.recycleview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bing.mvvmbase.R
import com.bing.mvvmbase.base.BaseViewModel
import com.bing.mvvmbase.base.IsSame
import com.bing.mvvmbase.model.datawrapper.Status
import com.bing.mvvmbase.utils.DynamicTimeFormat
import com.bing.statuslayout.StatusLayout
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader

import java.util.Date
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseRecycleViewFragment<DB : ViewDataBinding, VM : BaseViewModel, AVM : BaseViewModel,  AD : BaseRecycleViewAdapter<T, *>, T : IsSame> : Fragment(), View.OnClickListener {
        lateinit var mBinding: DB
        lateinit var mViewModel: VM
        lateinit var mActivityViewModel: AVM
        protected var mCompositeDisposable = CompositeDisposable()

        lateinit var mRecyclerView: RecyclerView
        lateinit var mRefreshLayout: RefreshLayout
        lateinit var mClassicsHeader: ClassicsHeader
        lateinit var mAdapter: AD
        lateinit var mStatusLayout: StatusLayout

        protected abstract val refreshLayout: SmartRefreshLayout

        protected abstract val recyclerView: RecyclerView
        protected abstract val layoutManager: RecyclerView.LayoutManager

        protected var itemAnimator = DefaultItemAnimator()
        protected abstract val networkState: MutableLiveData<Status>
        protected abstract val refreshState: MutableLiveData<Status>
        protected abstract val data: LiveData<List<T>>

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                if (arguments != null) {
                        handleArguments()
                }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
                initView()
                return mBinding.root
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
                super.onActivityCreated(savedInstanceState)
                mActivityViewModel = ViewModelProviders.of(activity!!).get(mActivityViewModel.javaClass)
                mViewModel = ViewModelProviders.of(this).get(mViewModel.javaClass)
                lifecycle.addObserver(mViewModel)
                bindAndObserve()
        }

        /**
         * 此时还没有viewModel
         */
        protected fun initView() {
                initStatusLayout()
                initRefreshLayout()
                initRefreshHeader()
                initRecycleView()
        }

        protected abstract fun initStatusLayout()
        protected fun initRefreshLayout() {
                mRefreshLayout = refreshLayout
                mRefreshLayout.setEnableLoadMore(false)
                mRefreshLayout.setEnableOverScrollDrag(true)//是否启用越界拖动
                mRefreshLayout.setOnRefreshListener { refreshLayout -> refresh(refreshLayout) }
        }

        protected abstract fun refresh(refreshLayout: RefreshLayout)

        protected fun initRefreshHeader() {
                mClassicsHeader = mRefreshLayout.refreshHeader as ClassicsHeader
                mClassicsHeader.setTimeFormat(DynamicTimeFormat(getString(R.string.refresh_at) + " %s"))
        }

        protected fun initRecycleView() {
                mRecyclerView = recyclerView
                mRecyclerView.layoutManager = layoutManager
                mRecyclerView.itemAnimator = itemAnimator
                initAdapter()
                mRecyclerView.adapter = mAdapter
        }

        protected abstract fun initAdapter()

        protected abstract fun handleArguments()
        protected abstract fun layoutId(): Int

        protected fun bindAndObserve() {
                networkState.observe(this, Observer { status ->
                        if (status == null) {
                                return@Observer
                        }
                        when (status) {
                                Status.LOADING -> mStatusLayout.showViewByStatus(StatusLayout.STATUS_LOADING)

                                Status.SUCCESS -> mStatusLayout.showViewByStatus(StatusLayout.STATUS_CONTENT)

                                Status.ERROR -> mStatusLayout.showViewByStatus(StatusLayout.STATUS_ERROR)

                                Status.EMPTY -> mStatusLayout.showViewByStatus(StatusLayout.STATUS_EMPTY)

                                Status.NO_NETWORK -> mStatusLayout.showViewByStatus(StatusLayout.STATUS_NO_NETWORK)

                                else -> {
                                }
                        }
                })
                data.observe(this, Observer { list -> mAdapter.data = list })
                refreshState.observe(this, Observer { status ->
                        if (status != Status.LOADING) {
                                mRefreshLayout.finishRefresh(status == Status.SUCCESS)
                                mClassicsHeader.setLastUpdateTime(Date(System.currentTimeMillis()))
                        }
                })
        }

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
}
