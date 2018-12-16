package com.bing.mvvmbase.base.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bing.mvvmbase.R
import com.bing.mvvmbase.base.BaseFragment
import com.bing.mvvmbase.model.datawrapper.Status
import com.bing.mvvmbase.utils.DynamicTimeFormat
import com.bing.statuslayout.StatusLayout
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.header.ClassicsHeader

import java.util.Date
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.bing.mvvmbase.base.BaseViewModel
import com.bing.mvvmbase.base.IsSame

abstract class BasePageFragment<DB : ViewDataBinding, VM : BaseViewModel, AVM : BaseViewModel, AD : PagedListAdapter<T, *>, T: IsSame> : BaseFragment<DB, VM, AVM>() {
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
        protected abstract val data: LiveData<PagedList<T>>

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false)
                initView()
                return mBinding.root
        }

        override fun initView() {
                initStatusLayout()
                initRefreshLayout()
                initRefreshHeader()
                initRecycleView()
        }

        protected abstract fun initStatusLayout()

        open fun initRefreshLayout() {
                mRefreshLayout = refreshLayout
                mRefreshLayout.setEnableLoadMore(false)
                mRefreshLayout.setEnableOverScrollDrag(true)//是否启用越界拖动
                mRefreshLayout.setOnRefreshListener { refreshLayout -> refresh(refreshLayout) }
        }

        protected abstract fun refresh(refreshLayout: RefreshLayout)

        open fun initRefreshHeader() {
                mClassicsHeader = mRefreshLayout.refreshHeader as ClassicsHeader
                mClassicsHeader.setTimeFormat(DynamicTimeFormat(getString(R.string.refresh_at) + " %s"))
        }

        open fun initRecycleView() {
                mRecyclerView = recyclerView
                mRecyclerView.layoutManager = layoutManager
                mRecyclerView.itemAnimator = itemAnimator
                initAdapter()
                mRecyclerView.adapter = mAdapter
        }

        protected abstract fun initAdapter()

        override fun bindAndObserve() {
                data.observe(this, Observer { data -> mAdapter!!.submitList(data) })
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
                refreshState.observe(this, Observer { status ->
                        if (status != Status.LOADING) {
                                mRefreshLayout.finishRefresh(status == Status.SUCCESS)
                                mClassicsHeader.setLastUpdateTime(Date(System.currentTimeMillis()))
                        }
                })
        }
}
