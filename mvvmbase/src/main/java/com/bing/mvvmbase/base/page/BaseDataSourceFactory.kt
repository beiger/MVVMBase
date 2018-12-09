package com.bing.mvvmbase.base.page

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.reactivex.disposables.CompositeDisposable

abstract class BaseDataSourceFactory<DS : BaseDataSource<K, T, *>, K, T>(protected var mCompositeDisposable: CompositeDisposable) : DataSource.Factory<K, T>() {
        var sourceLiveData = MutableLiveData<DS>()
                protected set

        override fun create(): DataSource<K, T> {
                val source = createDataSource()
                sourceLiveData.postValue(source)
                return source
        }

        protected abstract fun createDataSource(): DS
}
