package com.bing.mvvmbase.model

import com.bing.mvvmbase.base.BaseApplication
import com.readystatesoftware.chuck.ChuckInterceptor
import com.tamic.novate.cache.CookieCacheImpl
import com.tamic.novate.cookie.NovateCookieManager
import com.tamic.novate.cookie.SharedPrefsCookiePersistor

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 在reponsitoryManager中初始化
 */
class RetrofitClient(baseUrl: String) {
        private val mRetrofit: Retrofit

        init {
                //声明日志类
                val interceptor = ChuckInterceptor(BaseApplication.sContext)
                //自定义OkHttpClient
                val okHttpClient = OkHttpClient.Builder()
                //添加拦截器
                okHttpClient.addInterceptor(interceptor)
                okHttpClient.cookieJar(NovateCookieManager(CookieCacheImpl(), SharedPrefsCookiePersistor(BaseApplication.sContext)))

                mRetrofit = Retrofit.Builder()
                        .baseUrl(baseUrl) //设置网络请求的Url地址
                        .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .client(okHttpClient.build())
                        .build()
        }

        fun <T> obtainRetrofitService(service: Class<T>): T {
                val retrofitService: T
                retrofitService = mRetrofit.create(service)
                return retrofitService
        }
}
