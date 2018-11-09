package com.bing.mvvmbase.bindingadapter;

import android.view.View;
import android.widget.ImageView;

import com.bing.mvvmbase.module.glide.GlideApp;

import androidx.databinding.BindingAdapter;

public class BindingAdapters {
	@BindingAdapter("visibleGone")
	public static void showHide(View view, boolean show) {
		view.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@BindingAdapter("img_url")
	public static void showImg(ImageView iv, String url) {
		GlideApp.with(iv.getContext()).load(url).into(iv);
	}
}
