package com.bing.mvvmbase;

import com.bing.mvvmbase.base.BaseApplication;
import com.squareup.leakcanary.LeakCanary;

public class MyAppliction extends BaseApplication {
	@Override
	public void onCreate() {

		if (LeakCanary.isInAnalyzerProcess(this)) {
			return;
		}
		LeakCanary.install(this);
		super.onCreate();
	}
}
