package com.bing.mvvmbase.base

import androidx.appcompat.app.AppCompatActivity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager

import com.bing.mvvmbase.R
import com.bing.mvvmbase.utils.UiUtil

/**
 * 沉浸式acitivity模板
 * 注意：
 * 1. 如果使用mStatusbarView， theme里不要设置 android:windowTranslucentStatus=true,也不要设置fitSystemWindow属性;
 * 2. 如果不使用mStatusbarView， 可以设置fitSystemWindow=true，然后设置getWindow().setStatusBarColor(Color)为需要的颜色;
 * 3. 也可以给Activityy设置背景，调整toolbar高度，并设置padding
 */
class FullscreenActivity : AppCompatActivity() {
        lateinit var mStatusbarView: View

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_fullscreen)
                // 1. 沉浸式状态栏 >19
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                window.statusBarColor = Color.TRANSPARENT
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        } else {
                                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                        }
                }
                mStatusbarView = findViewById(R.id.statusbar_view)
                dealStatusBar() // 调整状态栏高度

                // 可选
                setStatusBarColor(resources.getColor(R.color.color20))
                changeStatusBarTextColor(true)
        }


        /**
         * 调整沉浸式菜单的title
         */
        private fun dealStatusBar() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        val statusBarHeight = UiUtil.getStatusBarHeight(this)
                        val lp = mStatusbarView.layoutParams
                        lp.height = statusBarHeight
                        mStatusbarView.layoutParams = lp
                }
        }

        /**
         * 设置状态栏背景颜色，给占位view设置各种color或drawable
         */
        private fun setStatusBarColor(color: Int) {
                mStatusbarView!!.setBackgroundColor(color)
        }

        /**
         * 调整状态栏字体为黑色，>23
         * 可以和沉浸式状态栏一下设置完
         */
        private fun changeStatusBarTextColor(isBlack: Boolean) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        if (isBlack) {
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//设置状态栏黑色字体
                        } else {
                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_VISIBLE//恢复状态栏白色字体
                        }
                }
        }
}
