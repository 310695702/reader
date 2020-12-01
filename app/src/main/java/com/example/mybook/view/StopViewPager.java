package com.example.mybook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class StopViewPager extends ViewPager {

    public StopViewPager(@NonNull Context context) {
        super(context);
    }

    public StopViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
//让这两个事件没有控件处理，即停止滑动翻页
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    //禁用滑动动画
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }
}
