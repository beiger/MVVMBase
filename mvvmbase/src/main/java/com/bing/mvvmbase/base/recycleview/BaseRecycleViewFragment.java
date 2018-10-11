package com.bing.mvvmbase.base.recycleview;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecycleViewFragment<AD extends BaseRecycleViewAdapter, T> extends BaseFragment {
	protected RecyclerView mRecyclerView;
	protected RefreshLayout mRefreshLayout;
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
		int delta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
		ClassicsHeader classicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
		classicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis() - delta));
		classicsHeader.setTimeFormat(new SimpleDateFormat(getString(R.string.refresh_at) + " MM-dd HH:mm", Locale.getDefault()));
		classicsHeader.setTimeFormat(new DynamicTimeFormat(getString(R.string.refresh_at) + " %s"));
	}

	protected void initRecycleView() {
		mRecyclerView = getRecyclerView();
		mRecyclerView.setLayoutManager(getLayoutManager());
		mRecyclerView.setItemAnimator(getItemAnimator());
		initAdapter();
	}

	protected abstract RecyclerView getRecyclerView();
	protected abstract RecyclerView.LayoutManager getLayoutManager();

	protected RecyclerView.ItemAnimator getItemAnimator() {
		return new DefaultItemAnimator();
	}
	protected abstract void initAdapter();
	protected abstract LiveData<Status> getNetworkState();
	protected abstract LiveData<Status> getRefreshState();
	protected abstract LiveData<List<T>> getData();

	@Override
	protected void bindAndObserve() {
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
		getData().observe(this, new Observer<List<T>>() {
			@Override
			public void onChanged(List<T> list) {
				mAdapter.setData(list);
			}
		});
		getRefreshState().observe(this, new Observer<Status>() {
			@Override
			public void onChanged(Status status) {
				if (status != Status.LOADING) {
					mRefreshLayout.finishRefresh(status == Status.SUCCESS);
				}
			}
		});
	}
}
