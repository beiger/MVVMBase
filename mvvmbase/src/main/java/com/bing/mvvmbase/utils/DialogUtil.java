package com.bing.mvvmbase.utils;

import android.content.Context;

import com.bing.mvvmbase.R;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {
	/**
	 * 用于全屏禁止点击事件
	 */
	public static AlertDialog createTranslucentDialog(Context context) {
		return new AlertDialog.Builder(context, R.style.TransparentDialog).setCancelable(false).create();
	}
}
