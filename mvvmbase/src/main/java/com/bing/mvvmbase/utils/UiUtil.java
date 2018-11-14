package com.bing.mvvmbase.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class UiUtil {
	/**
	 * 会使activity全屏
	 * 1. 如果状态栏是单一的颜色，可以直接在xml中添加fitSystemWindow=true，比较简便;
	 * 2. 如果要实现沉浸式，可以用FullscreenActivity的方式
	 */
	/**
	 * 会使activity全屏
	 * 1. 如果状态栏是单一的颜色，可以直接在xml中添加fitSystemWindow=true，比较简便;
	 * 2. 如果要实现沉浸式，可以用FullscreenActivity的方式,或者下面的方式
	 */
	public static void setBarColorAndFontBlack(Activity activity, int color) {
		Window window = activity.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				window.setStatusBarColor(color);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				} else {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
				}
			} else {
				window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
		}
	}

	public static void setBarColorAndFontWhite(Activity activity, int color) {
		Window window = activity.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				window.setStatusBarColor(color);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
				} else {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
				}
			} else {
				window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
		}
	}

	/**
	 * 不需要fitSystemWindow,注意只能使用一次
	 */
	public static void setBarColorAndFontBlackByChangeView(Activity activity, int color, View view) {
		Window window = activity.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				window.setStatusBarColor(color);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				} else {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
				}
			} else {
				window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
			setPaddingSmart(activity, view);
		}
	}

	/**
	 * 不需要fitSystemWindow
	 */
	public static void setBarColorAndFontWhiteByChangeView(Activity activity, int color, View view) {
		Window window = activity.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				window.setStatusBarColor(color);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
				} else {
					window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
				}
			} else {
				window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
			setPaddingSmart(activity, view);
		}
	}

	public static void setPaddingSmart(Context context, View view) {
		if (Build.VERSION.SDK_INT >= 19) {
			ViewGroup.LayoutParams lp = view.getLayoutParams();
			if (lp != null && lp.height > 0) {
				lp.height += getStatusBarHeight(context);//增高
			}
			view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
					view.getPaddingRight(), view.getPaddingBottom());
		}
	}

	public static int getStatusBarHeight(Context context){
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
