package com.example.carinfoapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

//禁止滑动的viewpager
public class NoSwipeViewPager extends ViewPager {

    private boolean canSwipe = true;
    public NoSwipeViewPager(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    //是否禁止滑动
    public void setCanSwipe(boolean canSwipe)
    {
        this.canSwipe = canSwipe;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canSwipe && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSwipe && super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }
}
