package com.bing.mvvmbase.base.page

import com.bing.mvvmbase.model.datawrapper.Status
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 *
 * @param <T> 数据类型
 * @param <TR> 服务端返回类型
</TR></T> */
abstract class BaseDataSource<K, T, TR>(protected var mCompositeDisposable: CompositeDisposable) : PageKeyedDataSource<K, T>() {
        val networkState = MutableLiveData<Status>()
        val initialLoadState = MutableLiveData<Status>()

        /**
         * Keep Completable reference for the retry event
         */
        var mRetryCompletable: Completable? = null

        fun retry() {
                mRetryCompletable.let {
                        mCompositeDisposable.add(mRetryCompletable!!
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ }, { }))
                }
        }

        override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<K>, callback: PageKeyedDataSource.LoadInitialCallback<K, T>) {
                networkState.postValue(Status.LOADING)
                initialLoadState.postValue(Status.LOADING)
                mCompositeDisposable.add(getResponseFromWeb(firstKey())
                        .subscribe(createInitialAcceptConsumer(callback), createInitialThrowableConsumer(params, callback)))
        }

        protected fun createInitialAcceptConsumer(callback: PageKeyedDataSource.LoadInitialCallback<K, T>): Consumer<TR> {
                return Consumer { response ->
                        setRetry(null)
                        networkState.postValue(Status.SUCCESS)
                        initialLoadState.postValue(Status.SUCCESS)

                        if (response == null || getDataFromResponse(response) == null) {
                                networkState.postValue(Status.EMPTY)
                        } else {
                                callback.onResult(getDataFromResponse(response)!!, firstKey(), nextKey(firstKey(), response))
                        }
                }
        }

        protected fun createInitialThrowableConsumer(params: PageKeyedDataSource.LoadInitialParams<K>, callback: PageKeyedDataSource.LoadInitialCallback<K, T>): Consumer<Throwable> {
                return Consumer {
                        setRetry(Action { loadInitial(params, callback) })
                        networkState.postValue(Status.ERROR)
                        initialLoadState.postValue(Status.ERROR)
                }
        }


        override fun loadBefore(params: PageKeyedDataSource.LoadParams<K>, callback: PageKeyedDataSource.LoadCallback<K, T>) {

        }

        override fun loadAfter(params: PageKeyedDataSource.LoadParams<K>, callback: PageKeyedDataSource.LoadCallback<K, T>) {
                mCompositeDisposable.add(getResponseFromWeb(params.key)
                        .subscribe(createAfterAcceptConsumer(params, callback), createAfterThrowableConsumer(params, callback)))
        }

        protected fun createAfterAcceptConsumer(params: PageKeyedDataSource.LoadParams<K>, callback: PageKeyedDataSource.LoadCallback<K, T>): Consumer<TR> {
                return Consumer { response ->
                        setRetry(null)
                        networkState.postValue(Status.SUCCESS)
                        val nextKey = nextKey(params.key, response)
                        callback.onResult(getDataFromResponse(response)!!, nextKey)
                }
        }

        protected fun createAfterThrowableConsumer(params: PageKeyedDataSource.LoadParams<K>, callback: PageKeyedDataSource.LoadCallback<K, T>): Consumer<Throwable> {
                return Consumer {
                        setRetry(Action { loadAfter(params, callback) })
                        networkState.postValue(Status.ERROR)
                }
        }

        protected abstract fun firstKey(): K
        protected abstract fun nextKey(previousKey: K, response: TR?): K
        protected abstract fun getDataFromResponse(response: TR?): List<T>?
        protected abstract fun getResponseFromWeb(key: K): Observable<TR>

        protected fun setRetry(action: Action?) {
                if (action == null) {
                        this.mRetryCompletable = null
                } else {
                        this.mRetryCompletable = Completable.fromAction(action)
                }
        }
}
