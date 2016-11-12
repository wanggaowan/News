package com.suixingame.news.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/20 18:55
 *
 * 描 述 ：
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class LruImageCache implements ImageLoader.ImageCache {


    private final LruCache<String, Bitmap> mLruCache;
    private static LruImageCache mLruImageCache;

    private LruImageCache () {
        mLruCache = new LruCache<String, Bitmap> (1024 * 1024 * 10) {
            @Override
            protected int sizeOf (String key, Bitmap value) {
                return value.getHeight () * value.getWidth ();
            }
        };
    }

    public static LruImageCache getInstance () {
        if (mLruImageCache == null) {
            mLruImageCache = new LruImageCache ();
        }
        return mLruImageCache;
    }

    @Override
    public Bitmap getBitmap (String url) {
        return mLruCache.get (url);
    }

    @Override
    public void putBitmap (String url, Bitmap bitmap) {
        mLruCache.put (url, bitmap);
    }
}
