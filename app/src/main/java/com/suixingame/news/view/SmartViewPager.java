package com.suixingame.news.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/23 17:20
 *
 * 描 述 ：优化版ViewPager 能够实现我们要的功能
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class SmartViewPager extends ViewPager {
    /**
     * 界面滑动监听器
     */
    private OnPageChangeListener mListener;
    public SmartViewPager (Context context) {
        super (context);
    }

    public SmartViewPager (Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    @Override
    public void addOnPageChangeListener (OnPageChangeListener listener) {
        super.addOnPageChangeListener (listener);
        mListener = listener;
    }

    /**
     * 使调用{@link #setCurrentItem(int)} 可以触发OnPageChangeListener的onPageSelected方法
     * @param item
     */
    @Override
    public void setCurrentItem (int item) {
        super.setCurrentItem (item);
        if (mListener!=null){
            mListener.onPageSelected (item);
        }
    }
    /**
     * 使调用{@link #setCurrentItem(int, boolean)} 可以触发OnPageChangeListener的onPageSelected方法
     * @param item
     */
    @Override
    public void setCurrentItem (int item, boolean smoothScroll) {
        super.setCurrentItem (item, smoothScroll);
        if (mListener!=null){
            mListener.onPageSelected (item);
        }
    }
}
