package com.bing.mvvmbase.base.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bing.mvvmbase.R;
import com.bing.mvvmbase.base.BaseFragment;
import com.bing.mvvmbase.module.loadingpage.LoadingCallback;
import com.bing.mvvmbase.module.loadingpage.NetErrorCallback;
import com.bing.mvvmbase.module.loadingpage.NoDataCallback;
import com.bing.mvvmbase.model.datawrapper.Status;
import com.bing.mvvmbase.utils.DynamicTimeFormat;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BasePageFragment<AD extends PagedListAdapter, T> extends BaseFragment {
	protected RecyclerView mRecyclerView;
	protected RefreshLayout mRefreshLayout;
	protected ClassicsHeader mClassicsHeader;
	protected AD mAdapter;
	protected LoadService mLoadService;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
		initView();
		return mLoadService.getLoadLayout();
	}

	@Override
	protected void initView() {
		mLoadService = LoadSir.getDefault().register(mBinding.getRoot(), new Callback.OnReloadListener() {
			@Override
			public void onReload(View v) {
				reload(v);
			}
		});
		initRefreshLayout();
		initRefreshHeader();
		initRecycleView();
	}

	protected abstract void reload(View view);
	protected void initRefreshLayout() {
		mRefreshLayout = getRefreshLayout();
		mRefreshLayout.setEnableLoadMore(false);
		mRefreshLayout.setEnableOverScrollDrag(true);//是否启用越界拖动
		mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				refresh(refreshLayout);
			}
		});
	}

	protected abstract SmartRefreshLayout getRefreshLayout();
	protected abstract void refresh(@NonNull RefreshLayout refreshLayout);

	protected void initRefreshHeader() {
		mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
		mClassicsHeader.setTimeFormat(new DynamicTimeFormat(getString(R.string.refresh_at) + " %s"));
	}

	protected void initRecycleView() {
		mRecyclerView = getRecyclerView();
		mRecyclerView.setLayoutManager(getLayoutManager());
		mRecyclerView.setItemAnimator(getItemAnimator());
		initAdapter();
		mRecyclerView.setAdapter(mAdapter);
	}

	protected abstract RecyclerView getRecyclerView();
	protected abstract RecyclerView.LayoutManager getLayoutManager();

	protected RecyclerView.ItemAnimator getItemAnimator() {
		return new DefaultItemAnimator();
	}
	protected abstract void initAdapter();
	protected abstract MutableLiveData<Status> getNetworkState();
	protected abstract MutableLiveData<Status> getRefreshState();
	protected abstract LiveData<PagedList<T>> getData();

	@Override
	protected void bindAndObserve() {
		getData().observe(this, new Observer<PagedList<T>>() {
			@Override
			public void onChanged(PagedList<T> data) {
				mAdapter.submitList(data);
				if (data == null || data.size() == 0) {
					getNetworkState().setValue(Status.NONE);
				} else {
					getNetworkState().setValue(Status.SUCCESS);
				}
			}
		});
		getNetworkState().observe(this, new Observer<Status>() {
			@Override
			public void onChanged(@Nullable Status status) {
				if (status == null) {
					return;
				}
				switch (status) {
					case LOADING:
						mLoadService.showCallback(LoadingCallback.class);
						break;

					case SUCCESS:
						mLoadService.showSuccess();
						break;

					case ERROR:
						mLoadService.showCallback(NetErrorCallback.class);
						break;

					case NONE:
						mLoadService.showCallback(NoDataCallback.class);
						break;

					default:
						break;
				}
			}
		});
		getRefreshState().observe(this, new Observer<Status>() {
			@Override
			public void onChanged(Status status) {
				if (status != Status.LOADING) {
					mRefreshLayout.finishRefresh(status == Status.SUCCESS);
					mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()));
				}
			}
		});
	}
}
