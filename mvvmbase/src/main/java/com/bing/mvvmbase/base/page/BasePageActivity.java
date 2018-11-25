package com.bing.mvvmbase.base.page;

import android.os.Bundle;
import android.view.View;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePageActivity<DB extends ViewDataBinding, VM extends BaseViewModel, AD extends PagedListAdapter, T> extends AppCompatActivity implements View.OnClickListener {
	protected DB mBinding;
	protected VM mViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	protected RecyclerView mRecyclerView;
	protected RefreshLayout mRefreshLayout;
	protected ClassicsHeader mClassicsHeader;
	protected AD mAdapter;
	protected StatusLayout mStatusLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreateFirst();
		initViewModel();
		getLifecycle().addObserver(mViewModel);
		mBinding = DataBindingUtil.setContentView(this, layoutId());

		initStatusLayout();
		initRefreshLayout();
		initRefreshHeader();
		initRecycleView();
		bindAndObserve();
	}

	protected void onCreateFirst() {

	}

	protected abstract void initViewModel();
	protected abstract int layoutId();
	protected abstract SmartRefreshLayout getRefreshLayout();
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
	}

	protected abstract RecyclerView getRecyclerView();
	protected abstract RecyclerView.LayoutManager getLayoutManager();
	protected RecyclerView.ItemAnimator getItemAnimator() {
		return new DefaultItemAnimator();
	}
	protected abstract void initAdapter();

	protected abstract LiveData<Status> getNetworkState();
	protected abstract LiveData<Status> getRefreshState();
	protected abstract LiveData<PagedList<T>> getData();

	protected void bindAndObserve() {
		getData().observe(this, new Observer<PagedList<T>>() {
			@Override
			public void onChanged(PagedList<T> data) {
				mAdapter.submitList(data);
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
	protected void onDestroy() {
		mCompositeDisposable.dispose();
		super.onDestroy();
	}
}
