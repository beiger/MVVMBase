package com.bing.mvvmbase.base;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bing.mvvmbase.R;

/**
 * 沉浸式acitivity模板
 * 注意：
 * 1. 如果使用mStatusbarView， theme里不要设置 android:windowTranslucentStatus=true,也不要设置fitSystemWindow属性;
 * 2. 如果不使用mStatusbarView， 可以设置fitSystemWindow=true，然后设置getWindow().setStatusBarColor(Color)为需要的颜色;
 */
public class FullscreenActivity extends AppCompatActivity {
	private View mStatusbarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		// 1. 沉浸式状态栏 >19
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				getWindow().setStatusBarColor(Color.TRANSPARENT);
				getWindow()
						.getDecorView()
						.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			} else {
				getWindow()
						.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			}
		}
		mStatusbarView = findViewById(R.id.statusbar_view);
		dealStatusBar(); // 调整状态栏高度

		// 可选
		setStatusBarColor(getResources().getColor(R.color.color20));
		changeStatusBarTextColor(true);
	}



	/**
	 * 调整沉浸式菜单的title
	 */
	private void dealStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int statusBarHeight = getStatusBarHeight();
			ViewGroup.LayoutParams lp = mStatusbarView.getLayoutParams();
			lp.height = statusBarHeight;
			mStatusbarView.setLayoutParams(lp);
		}
	}

	private int getStatusBarHeight(){
		int result = 0;
		int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = this.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * 设置状态栏背景颜色，给占位view设置各种color或drawable
	 */
	private void setStatusBarColor(int color) {
		mStatusbarView.setBackgroundColor(color);
	}

	/**
	 * 调整状态栏字体为黑色，>23
	 * 可以和沉浸式状态栏一下设置完
	 */
	private void changeStatusBarTextColor(boolean isBlack) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
			if (isBlack) {
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//设置状态栏黑色字体
			} else {
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_VISIBLE);//恢复状态栏白色字体
			}
		}
	}
}
