package com.bing.mvvmbase.model;

import com.bing.mvvmbase.base.BaseApplication;
import com.readystatesoftware.chuck.ChuckInterceptor;
import com.tamic.novate.cache.CookieCacheImpl;
import com.tamic.novate.cookie.NovateCookieManager;
import com.tamic.novate.cookie.SharedPrefsCookiePersistor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 在reponsitoryManager中初始化
 */
public class RetrofitClient {
	private Retrofit mRetrofit;

	private RetrofitClient(String baseUrl) {
		//声明日志类
		Interceptor interceptor = new ChuckInterceptor(BaseApplication.sContext);
		//自定义OkHttpClient
		OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
		//添加拦截器
		okHttpClient.addInterceptor(interceptor);
		okHttpClient.cookieJar(new NovateCookieManager(new CookieCacheImpl(), new SharedPrefsCookiePersistor(BaseApplication.sContext)));

		mRetrofit = new Retrofit.Builder()
				.baseUrl(baseUrl) //设置网络请求的Url地址
				.addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(okHttpClient.build())
				.build();
	}

	public <T> T obtainRetrofitService(Class<T> service) {
		T retrofitService;
		retrofitService = mRetrofit.create(service);
		return retrofitService;
	}
}
