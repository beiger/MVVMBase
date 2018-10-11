package com.bing.mvvmbase.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.blankj.utilcode.util.BarUtils;

public class UiUtil {
	/**
	 * 会使activity全屏
	 * 1. 如果状态栏是单一的颜色，可以直接在xml中添加fitSystemWindow=true，比较简便;
	 * 2. 如果要实现沉浸式，可以用FullscreenActivity的方式
	 */
	public static void setBarColorAndFontBlack(Activity activity, int color,int alpha) {
		BarUtils.setStatusBarColor(activity, color, alpha);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
	}

	public static void setBarColorAndFontWhite(Activity activity, int color,int alpha) {
		BarUtils.setStatusBarColor(activity, color, alpha);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_VISIBLE);
		}
	}
}
