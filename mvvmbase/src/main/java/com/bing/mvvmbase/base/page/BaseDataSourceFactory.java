package com.bing.mvvmbase.base.page;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseDataSourceFactory<DS extends BaseDataSource, K, T> extends DataSource.Factory<K, T> {
	protected CompositeDisposable mCompositeDisposable;
	protected MutableLiveData<DS> mSourceLiveData = new MutableLiveData<>();

	public BaseDataSourceFactory(CompositeDisposable compositeDisposable) {
		mCompositeDisposable = compositeDisposable;
	}

	public MutableLiveData<DS> getSourceLiveData() {
		return mSourceLiveData;
	}

	@Override
	public DataSource<K, T> create() {
		DS source = createDataSource();
		mSourceLiveData.postValue(source);
		return source;
	}

	protected abstract DS createDataSource();
}
