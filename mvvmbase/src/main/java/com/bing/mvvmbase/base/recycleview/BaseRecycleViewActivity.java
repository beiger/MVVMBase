package com.bing.mvvmbase.base.recycleview;

import android.os.Bundle;
import android.view.View;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseRecycleViewActivity<DB extends ViewDataBinding, VM extends BaseViewModel, AD extends BaseRecycleViewAdapter, T> extends AppCompatActivity implements View.OnClickListener {
	protected DB mBinding;
	protected VM mViewModel;
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	protected RecyclerView mRecyclerView;
	protected RefreshLayout mRefreshLayout;
	protected AD mAdapter;
	protected LoadService mLoadService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCreateFirst();
		initViewModel();
		getLifecycle().addObserver(mViewModel);
		mBinding = DataBindingUtil.setContentView(this, layoutId());
		mLoadService = LoadSir.getDefault().register(getRefreshLayout(), new Callback.OnReloadListener() {
			@Override
			public void onReload(View v) {
				reload(v);
			}
		});
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
	protected abstract void reload(View v);

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

	protected abstract LiveData<Status> getNetworkState();
	protected abstract LiveData<Status> getRefreshState();
	protected abstract LiveData<List<T>> getData();

	protected void bindAndObserve() {
		getData().observe(this, new Observer<List<T>>() {
			@Override
			public void onChanged(List<T> data) {
				mAdapter.setData(data);
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
