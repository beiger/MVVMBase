package com.bing.mvvmbase.base;

import android.app.Application;
import android.content.Context;

import com.bing.mvvmbase.R;
import com.bing.mvvmbase.module.loadingpage.LoadingCallback;
import com.bing.mvvmbase.module.loadingpage.NetErrorCallback;
import com.bing.mvvmbase.module.loadingpage.NoDataCallback;
import com.blankj.utilcode.util.Utils;
import com.kingja.loadsir.core.LoadSir;

import androidx.multidex.MultiDex;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application {
	private static Context sContext;
	private AppExecutors mAppExecutors;

	public static Context getContext() {
		return sContext;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = getApplicationContext();
		Utils.init(this);
		mAppExecutors = new AppExecutors();

		LoadSir.beginBuilder()
				.addCallback(new LoadingCallback())
				.addCallback(new NetErrorCallback())
				.addCallback(new NoDataCallback())
				.commit();

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setFontAttrId(R.attr.fontPath)
				.build());
	}

	public AppExecutors getAppExecutors() {
		return mAppExecutors;
	}
}
