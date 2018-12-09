package com.bing.mvvmbase.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager

/**
 * 可以开启关闭是否滑动
 */
class CustomViewPager : ViewPager {
        private var isCanScroll = false

        constructor(context: Context) : super(context)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

        /**
         * 设置其是否能滑动换页
         * @param isCanScroll false 不能换页， true 可以滑动换页
         */
        fun setScanScroll(isCanScroll: Boolean) {
                this.isCanScroll = isCanScroll
        }

        override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
                return isCanScroll && super.onInterceptTouchEvent(ev)
        }

        override fun onTouchEvent(ev: MotionEvent): Boolean {
                return isCanScroll && super.onTouchEvent(ev)
        }
}
