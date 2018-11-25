package com.bing.mvvmbase.base.page;

import com.bing.mvvmbase.model.datawrapper.Status;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @param <T> 数据类型
 * @param <TR> 服务端返回类型
 */
public abstract class BaseDataSource<K, T, TR> extends PageKeyedDataSource<K, T> {
	protected CompositeDisposable mCompositeDisposable;
	protected MutableLiveData<Status> mNetworkState = new MutableLiveData<>();
	protected MutableLiveData<Status> mInitialLoadState = new MutableLiveData<>();
	/**
	 * Keep Completable reference for the retry event
	 */
	protected Completable mRetryCompletable;

	public BaseDataSource(CompositeDisposable compositeDisposable) {
		this.mCompositeDisposable = compositeDisposable;
	}

	public MutableLiveData<Status> getNetworkState() {
		return mNetworkState;
	}

	public MutableLiveData<Status> getInitialLoadState() {
		return mInitialLoadState;
	}

	public void retry() {
		if (mRetryCompletable != null) {
			mCompositeDisposable.add(mRetryCompletable
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Action() {
						@Override
						public void run() {

						}
					}, new Consumer<Throwable>() {
						@Override
						public void accept(Throwable throwable) {

						}
					}));
		}
	}

	@Override
	public void loadInitial(@NonNull final LoadInitialParams<K> params, @NonNull final LoadInitialCallback<K, T> callback) {
		mNetworkState.postValue(Status.LOADING);
		mInitialLoadState.postValue(Status.LOADING);
		mCompositeDisposable.add(getResponseFromWeb(firstKey())
				.subscribe(createInitialAcceptConsumer(callback), createInitialThrowableConsumer(params, callback)));
	}

	protected Consumer<TR> createInitialAcceptConsumer(final LoadInitialCallback<K, T> callback) {
		return new Consumer<TR>() {
			@Override
			public void accept(TR response) {
				setRetry(null);
				mNetworkState.postValue(Status.SUCCESS);
				mInitialLoadState.postValue(Status.SUCCESS);

				if (response == null || getDataFromResponse(response) == null) {
					mNetworkState.postValue(Status.EMPTY);
				} else {
					callback.onResult(getDataFromResponse(response), firstKey(), nextKey(firstKey(), response));
				}
			}
		};
	}

	protected Consumer<Throwable> createInitialThrowableConsumer(final LoadInitialParams<K> params, final LoadInitialCallback<K, T> callback) {
		return new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) {
				setRetry(new Action() {
					@Override
					public void run() {
						loadInitial(params, callback);
					}
				});
				mNetworkState.postValue(Status.ERROR);
				mInitialLoadState.postValue(Status.ERROR);
			}
		};
	}


	@Override
	public void loadBefore(@NonNull LoadParams<K> params, @NonNull LoadCallback<K, T> callback) {

	}

	@Override
	public void loadAfter(@NonNull final LoadParams<K> params, @NonNull final LoadCallback<K, T> callback) {
		mCompositeDisposable.add(getResponseFromWeb(params.key)
				.subscribe(createAfterAcceptConsumer(params, callback), createAfterThrowableConsumer(params, callback)));
	}

	protected Consumer<TR> createAfterAcceptConsumer(final LoadParams<K> params, final LoadCallback<K, T> callback) {
		return new Consumer<TR>() {
			@Override
			public void accept(TR response) {
				setRetry(null);
				mNetworkState.postValue(Status.SUCCESS);
				K nextKey = nextKey(params.key, response);
				callback.onResult(getDataFromResponse(response), nextKey);
			}
		};
	}

	protected Consumer<Throwable> createAfterThrowableConsumer(final LoadParams<K> params, final LoadCallback<K, T> callback) {
		return new Consumer<Throwable>() {
			@Override
			public void accept(Throwable throwable) {
				setRetry(new Action() {
					@Override
					public void run() {
						loadAfter(params, callback);
					}
				});
				mNetworkState.postValue(Status.ERROR);
			}
		};
	}

	protected abstract K firstKey();
	protected abstract K nextKey(K previousKey, TR response);
	protected abstract List<T> getDataFromResponse(TR response);
	protected abstract Observable<TR> getResponseFromWeb(K key);

	protected void setRetry(final Action action) {
		if (action == null) {
			this.mRetryCompletable = null;
		} else {
			this.mRetryCompletable = Completable.fromAction(action);
		}
	}
}
