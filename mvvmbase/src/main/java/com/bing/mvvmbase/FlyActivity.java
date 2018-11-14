package com.bing.mvvmbase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;

import com.bing.mvvmbase.utils.UiUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.header.FlyRefreshHeader;
import com.scwang.smartrefresh.header.flyrefresh.FlyView;
import com.scwang.smartrefresh.header.flyrefresh.MountainSceneView;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;
import com.scwang.smartrefresh.layout.util.DensityUtil;

public class FlyActivity extends AppCompatActivity {
	private RefreshLayout mRefreshLayout;
	private FlyView mFlyView;
	private FlyRefreshHeader mFlyRefreshHeader;
	private CollapsingToolbarLayout mToolbarLayout;
	private FloatingActionButton mActionButton;
	private View.OnClickListener mThemeListener;
	private NestedScrollView mScrollView;
	private static boolean isFirstEnter = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fly);
		// 1. 沉浸式状态栏 >19
		UiUtil.setBarColorAndFontWhite(this, Color.TRANSPARENT);

		final Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		UiUtil.setPaddingSmart(this, toolbar);

		MountainSceneView mSceneView = findViewById(R.id.mountain);
		mFlyView = findViewById(R.id.flyView);
		mFlyRefreshHeader = findViewById(R.id.flyRefresh);
		mFlyRefreshHeader.setUp(mSceneView, mFlyView);//绑定场景和纸飞机
		mRefreshLayout = findViewById(R.id.refreshLayout);
		mRefreshLayout.setReboundInterpolator(new ElasticOutInterpolator());//设置回弹插值器，会带有弹簧震动效果
		mRefreshLayout.setReboundDuration(800);//设置回弹动画时长
		mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(@NonNull RefreshLayout refreshLayout) {
				updateTheme();//改变主题颜色
				mRefreshLayout.getLayout().postDelayed(new Runnable() {
					@Override
					public void run() {
						//通知刷新完成，这里改为通知Header，让纸飞机飞回来
						mFlyRefreshHeader.finishRefresh(new AnimatorListenerAdapter() {
							public void onAnimationEnd(Animator animation) {

							}
						});
					}
				}, 2000);//模拟两秒的后台数据加载
			}
		});
		//设置 让 AppBarLayout 和 RefreshLayout 的滚动同步 并不保持 toolbar 位置不变
		final AppBarLayout appBar = findViewById(R.id.appbar);
		mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
			@Override
			public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
				appBar.setTranslationY(offset);
				toolbar.setTranslationY(-offset);
			}
//            @Override
//            public void onHeaderPulling(@NonNull RefreshHeader header, float percent, int offset, int footerHeight, int maxDragHeight) {
//                appBar.setTranslationY(offset);
//                toolbar.setTranslationY(-offset);
//            }
//            @Override
//            public void onHeaderReleasing(@NonNull RefreshHeader header, float percent, int offset, int footerHeight, int maxDragHeight) {
//                appBar.setTranslationY(offset);
//                toolbar.setTranslationY(-offset);
//            }
		});
		/*-----------------------------------------------------------
		 * 关键代码-结束
		 *----------------------------------------------------------*/

		if (isFirstEnter) {
			isFirstEnter = false;
			mRefreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
		}

		mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarLayout);
		mActionButton = (FloatingActionButton) findViewById(R.id.fab);
		mScrollView = findViewById(R.id.scrollView);
		/*
		 * 设置点击 ActionButton 时候触发自动刷新 并改变主题颜色
		 */
		mActionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateTheme();
				mRefreshLayout.autoRefresh();
			}
		});
		/*
		 * 监听 AppBarLayout 的关闭和开启 给 FlyView（纸飞机） 和 ActionButton 设置关闭隐藏动画
		 */
		appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			boolean misAppbarExpand = true;

			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				int scrollRange = appBarLayout.getTotalScrollRange();
				float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
				if (fraction < 0.1 && misAppbarExpand) {
					misAppbarExpand = false;
					mActionButton.animate().scaleX(0).scaleY(0);
					mFlyView.animate().scaleX(0).scaleY(0);
					ValueAnimator animator = ValueAnimator.ofInt(mScrollView.getPaddingTop(), 0);
					animator.setDuration(300);
					animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							mScrollView.setPadding(0, (int) animation.getAnimatedValue(), 0, 0);
						}
					});
					animator.start();
				}
				if (fraction > 0.8 && !misAppbarExpand) {
					misAppbarExpand = true;
					mActionButton.animate().scaleX(1).scaleY(1);
					mFlyView.animate().scaleX(1).scaleY(1);
					ValueAnimator animator = ValueAnimator.ofInt(mScrollView.getPaddingTop(), DensityUtil.dp2px(25));
					animator.setDuration(300);
					animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							mScrollView.setPadding(0, (int) animation.getAnimatedValue(), 0, 0);
						}
					});
					animator.start();
				}
			}
		});
	}

	private void updateTheme() {
		if (mThemeListener == null) {
			mThemeListener = new View.OnClickListener() {
				int index = 0;
				int[] ids = new int[]{
						R.color.colorPrimary,
						android.R.color.holo_green_light,
						android.R.color.holo_red_light,
						android.R.color.holo_orange_light,
						android.R.color.holo_blue_bright,
				};
				@Override
				public void onClick(View v) {
					int color = ContextCompat.getColor(getApplication(), ids[index % ids.length]);
					mRefreshLayout.setPrimaryColors(color);
					mActionButton.setBackgroundColor(color);
					mActionButton.setBackgroundTintList(ColorStateList.valueOf(color));
					mToolbarLayout.setContentScrimColor(color);
					index++;
				}
			};
		}
		mThemeListener.onClick(null);
	}

	public class ElasticOutInterpolator implements Interpolator {

		@Override
		public float getInterpolation(float t) {
			if (t == 0) return 0;
			if (t >= 1) return 1;
			float p=.3f;
			float s=p/4;
			return ((float)Math.pow(2,-10*t) * (float)Math.sin( (t-s)*(2*(float)Math.PI)/p) + 1);
		}
	}
}
