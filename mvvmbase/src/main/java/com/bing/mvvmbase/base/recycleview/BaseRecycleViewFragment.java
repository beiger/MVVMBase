package com.bing.mvvmbase.base.recycleview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bing.mvvmbase.R;
import com.bing.mvvmbase.base.BaseViewModel;
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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseRecycleViewFragment<DB extends ViewDataBinding, VM extends BaseViewModel, AVM extends BaseViewModel, AD extends BaseRecycleViewAdapter, T> extends Fragment {
	protected DB mBinding;
	protected VM mViewModel;
	protected AVM mActivityViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	protected RecyclerView mRecyclerView;
	protected RefreshLayout mRefreshLayout;
	protected AD mAdapter;
	protected LoadService mLoadService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			handleArguments();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mBinding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
		initView();
		return mLoadService.getLoadLayout();
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initActivityViewModel();
		initViewModel();
		getLifecycle().addObserver(mViewModel);
		bindAndObserve();
	}

	/**
	 * 此时还没有viewModel
	 */
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
		ClassicsHeader classicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
		classicsHeader.setTimeFormat(new DynamicTimeFormat(getString(R.string.refresh_at) + " %s"));
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
	protected abstract LiveData<List<T>> getData();

	protected abstract void handleArguments();
	protected abstract int layoutId();

	protected abstract void initActivityViewModel();
	protected abstract void initViewModel();

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
				if (list == null || list.size() == 0) {
					getNetworkState().setValue(Status.NONE);
				} else {
					getNetworkState().setValue(Status.SUCCESS);
				}
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
