package com.bing.mvvmbase.base.page;

import android.app.Application;

import com.bing.mvvmbase.base.AppExecutors;
import com.bing.mvvmbase.base.BaseApplication;
import com.bing.mvvmbase.model.datawrapper.Status;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BasePageViewModel<T, DSF extends BaseDataSourceFactory> extends AndroidViewModel {
	protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	private DSF mSourceFactory;
	private LiveData<PagedList<T>> mReviseList;
	protected AppExecutors mAppExecutors;

	public BasePageViewModel(@NonNull Application application) {
		super(application);
		mAppExecutors = ((BaseApplication) application).getAppExecutors();
	}

	public void initDataSource() {
		mSourceFactory = initSourceFactory();
		PagedList.Config pageConfig = initPageConfig();
		mReviseList = new LivePagedListBuilder<>(mSourceFactory, pageConfig)
				.setFetchExecutor(mAppExecutors.networkIO())
				.build();
	}

	protected abstract DSF initSourceFactory();

	protected PagedList.Config initPageConfig() {
		return new PagedList.Config.Builder()
				.setEnablePlaceholders(false)
				.setInitialLoadSizeHint(10)
				.setPageSize(10)
				.setPrefetchDistance(10)
				.build();
	}

	public void retry() {
		((BaseDataSource)mSourceFactory.getSourceLiveData().getValue()).retry();
	}

	public void refresh() {
		((BaseDataSource)mSourceFactory.getSourceLiveData().getValue()).invalidate();
	}

	protected void addDisposable(Disposable disposable) {
		mCompositeDisposable.add(disposable);
	}

	public LiveData<PagedList<T>> getReviseList() {
		return mReviseList;
	}
	public LiveData<Status> getNetworkState() {
		return Transformations.switchMap(mSourceFactory.getSourceLiveData(), new Function<BaseDataSource, LiveData<Status>>() {
			@Override
			public LiveData<Status> apply(BaseDataSource input) {
				return input.getNetworkState();
			}
		});
	}

	public LiveData<Status> getRefreshState() {
		return Transformations.switchMap(mSourceFactory.getSourceLiveData(), new Function<BaseDataSource, LiveData<Status>>() {
			@Override
			public LiveData<Status> apply(BaseDataSource input) {
				return input.getInitialLoadState();
			}
		});
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		mCompositeDisposable.dispose();
	}
}
