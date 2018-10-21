package com.bing.mvvmbase.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ScaleRecyclerView extends RecyclerView {
    public ScaleRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    String TAG = "ScaleRecyView";
    /**
     * action down 产生的x,y坐标
     */
    float lastX;
    float lastY;
    /**
     * 当前View是否处于缩放状态
     */
    boolean isScale = false;
    /**
     * 当前View的缩放值
     */
    float mScale = 1.0f;
    /**
     * 产生缩放时，使用的缩放系数，该值越大缩放比率就越大
     */
    float scaleRatio = 0.7f;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录Down时的 x y
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (y - lastY > 0) {
                    //获取Recycler的第一个展示的View
                    View view = getChildAt(0);
                    int pos = getChildViewHolder(view).getLayoutPosition();
                    //获取该View对应在数据集合中的位置
                    //如果位置=0 并且该View的上边界=0 并且当前正在向下拉动
                    if (pos == 0 && view.getTop() >= 0) {
                        //计算偏移量
                        float distance = y - lastY;
                        //计算出要缩放的scale
                        mScale = 1 + distance * scaleRatio / getHeight();
                        //设置缩放的锚点
                        setPivotY(0f);
                        setPivotX(getWidth() / 2);
                        //进行缩放
                        ViewCompat.setScaleY(this, mScale);
                        isScale = true;
                    }
                } else {
                    //获取最后一个展示的View
                    int count = getChildCount();
                    View lastView = getChildAt(count - 1);
                    int pos = getChildViewHolder(lastView).getLayoutPosition();
                    //判断其是否为数据集合最后一个位置
                    if (pos + 1 == getAdapter().getItemCount() && lastView.getBottom() <= getBottom()) {
                        //滑动到最底部 向上拉
                        float distance = y - lastY;
                        mScale = 1 - scaleRatio * distance / getHeight();
                        //设置锚点
                        setPivotX(getWidth() / 2);
                        setPivotY(getHeight());
                        ViewCompat.setScaleY(this, mScale);
                        isScale = true;
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
                if (isScale) {
                    //如果已经进行了缩放，up的时候应该恢复原状，从当前scale 恢复至1.0f
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
