package com.bing.mvvmbase.base.recycleview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bing.mvvmbase.R;
import com.bing.mvvmbase.base.BaseViewModel;
import com.bing.mvvmbase.model.datawrapper.Status;
import com.bing.mvvmbase.utils.DynamicTimeFormat;
import com.bing.statuslayout.StatusLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Date;
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
import io.reactivex.disposables.Disposable;

public abstract class BaseRecycleViewFragment<DB extends ViewDataBinding, VM extends BaseViewModel, AVM extends BaseViewModel, AD extends BaseRecycleViewAdapter, T> extends Fragment implements View.OnClickListener {
	protected DB mBinding;
	protected VM mViewModel;
	protected AVM mActivityViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	protected RecyclerView mRecyclerView;
	protected RefreshLayout mRefreshLayout;
	protected ClassicsHeader mClassicsHeader;
	protected AD mAdapter;
	protected StatusLayout mStatusLayout;

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
		return mBinding.getRoot();
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
		initStatusLayout();
		initRefreshLayout();
		initRefreshHeader();
		initRecycleView();
	}

	protected abstract void initStatusLayout();
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
						mStatusLayout.showViewByStatus(StatusLayout.STATUS_LOADING);
						break;

					case SUCCESS:
						mStatusLayout.showViewByStatus(StatusLayout.STATUS_CONTENT);
						break;

					case ERROR:
						mStatusLayout.showViewByStatus(StatusLayout.STATUS_ERROR);
						break;

					case EMPTY:
						mStatusLayout.showViewByStatus(StatusLayout.STATUS_EMPTY);
						break;

					case NO_NETWORK:
						mStatusLayout.showViewByStatus(StatusLayout.STATUS_NO_NETWORK);
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
					mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis()));
				}
			}
		});
	}

	protected void addOnClickListener(@NonNull View... views) {
		for (View view : views) {
			view.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {

	}

	protected void addDisposable(Disposable disposable) {
		mCompositeDisposable.add(disposable);
	}

	@Override
	public void onDestroy() {
		mCompositeDisposable.dispose();
		super.onDestroy();
	}
}
