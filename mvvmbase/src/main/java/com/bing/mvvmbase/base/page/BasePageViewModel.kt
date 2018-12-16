package com.bing.mvvmbase.base.page

import android.app.Application

import com.bing.mvvmbase.base.AppExecutors
import com.bing.mvvmbase.model.datawrapper.Status
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePageViewModel<K, T, TR, DS : BaseDataSource<K, T, TR>, DSF : BaseDataSourceFactory<DS, K, T>>(application: Application) : AndroidViewModel(application) {
        protected var mCompositeDisposable = CompositeDisposable()
        lateinit var mSourceFactory: DSF
        lateinit var reviseList: LiveData<PagedList<T>>
        protected var mAppExecutors = AppExecutors
        val networkState: LiveData<Status>
                get() = Transformations.switchMap(mSourceFactory!!.sourceLiveData) { input -> input.networkState }

        val refreshState: LiveData<Status>
                get() = Transformations.switchMap(mSourceFactory!!.sourceLiveData) { input -> input.initialLoadState }

        fun initDataSource() {
                mSourceFactory = initSourceFactory()
                val pageConfig = initPageConfig()
                reviseList = LivePagedListBuilder(mSourceFactory!!, pageConfig)
                        .setFetchExecutor(mAppExecutors.networkIO)
                        .build()
        }

        protected abstract fun initSourceFactory(): DSF

        open fun initPageConfig(): PagedList.Config {
                return PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(10)
                        .setPageSize(10)
                        .setPrefetchDistance(10)
                        .build()
        }

        fun retry() {
                (mSourceFactory.sourceLiveData.value as DS).retry()
        }

        fun refresh() {
                (mSourceFactory!!.sourceLiveData.value as DS).invalidate()
        }

        protected fun addDisposable(disposable: Disposable) {
                mCompositeDisposable.add(disposable)
        }

        override fun onCleared() {
                super.onCleared()
                mCompositeDisposable.dispose()
        }
}
