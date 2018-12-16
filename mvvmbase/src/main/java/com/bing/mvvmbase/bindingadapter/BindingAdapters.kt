package com.bing.mvvmbase.bindingadapter

import android.view.View
import android.widget.ImageView

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("img_url")
fun showImg(iv: ImageView, url: String) {
        Glide.with(iv.context).load(url).into(iv)
}
