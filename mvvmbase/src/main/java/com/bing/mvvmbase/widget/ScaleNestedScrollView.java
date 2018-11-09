package com.bing.mvvmbase.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

public class ScaleNestedScrollView extends NestedScrollView {
    public ScaleNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float lastX;
    float lastY;
    String TAG = "ScaleScrollView";
    boolean isScale = false;

    float mScale = 1.0f;

    float scaleRatio = 0.7f;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:

                if (getScrollY() == 0 && y - lastY > 0) {
                    //向下拉动
                    float distance = y - lastY;
                    int height = getHeight();
                    mScale = 1 + distance * scaleRatio / height;
                    setPivotY(0f);
                    setPivotX(getWidth() / 2);
                    ViewCompat.setScaleY(this, mScale);
                    isScale = true;
                } else {
                    //滑动到最底部 向上拉
                    int childHeight = getChildAt(0).getHeight();
                    int height = getHeight();
                    if (getScrollY() >= childHeight - height && y - lastY < 0) {
                        float distance = y - lastY;
                        mScale = 1 - scaleRatio * distance / height;
                        setPivotY(getHeight());
                        setPivotX(getWidth() / 2);
                        ViewCompat.setScaleY(this, mScale);
                        isScale = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isScale) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "scaleY", mScale, 1.0f);
                    animator.setDuration(300);
                    animator.start();
                    isScale = false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
