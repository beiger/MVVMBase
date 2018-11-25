package com.bing.mvvmbase.base;

import android.app.Application;
import android.content.Context;

import com.bing.mvvmbase.R;
import com.blankj.utilcode.util.Utils;

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

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setFontAttrId(R.attr.fontPath)
				.build());
	}

	public AppExecutors getAppExecutors() {
		return mAppExecutors;
	}
}
