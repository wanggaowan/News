package com.suixingame.news.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.suixingame.news.R;
import com.suixingame.news.bean.ImageNewsToJson;
import com.suixingame.news.util.LruImageCache;
import com.suixingame.news.util.XUtils;

import java.util.List;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/24 0:07
 *
 * 描 述 ：图片新闻每一条新闻详细展示的Adapter
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class ImageNewsItemAdapter extends PagerAdapter implements View.OnTouchListener {
    /**
     * 上下文
     */
    private Context mContext;

    /**
     * 展示需要的数据
     */
    private List<ImageNewsToJson.ImageNewsItem> mData;

    /**
     * 记录当前是不是只显示了图片，ActionBar和mBackground隐藏
     */
    private boolean justShowImage = false;

    /**
     * 展示图片新闻的Activity的ActionBar
     */
    private Toolbar mToolbar;

    /**
     * 图片新闻中展示图片信息的根布局
     */
    private LinearLayout mBackground;

    /**
     * 记录用户手指按下时相对于整个屏幕的X坐标
     */
    private float downX;
    /**
     * 记录用户手指按下时相对于整个屏幕的Y坐标
     */
    private float downY;

    /**
     * @param context      上下文
     * @param data         新闻数据
     * @param toolbar    当前新闻所在Activity的Toolbar
     * @param linearLayout 用于展示新闻文字信息布局的根布局
     */
    public ImageNewsItemAdapter (Context context, List<ImageNewsToJson.ImageNewsItem> data, Toolbar toolbar, LinearLayout linearLayout) {
        mContext = context;
        mData = data;
        mToolbar = toolbar;
        mBackground = linearLayout;
    }

    @Override
    public int getCount () {
        return mData.size ();
    }

    @Override
    public boolean isViewFromObject (View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        container.removeView ((View) object);
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        NetworkImageView imageView = new NetworkImageView (mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams (params);
        imageView.setOnTouchListener (this);

        imageView.setDefaultImageResId (R.drawable.news_image_default);
        RequestQueue requestQueue = Volley.newRequestQueue (mContext);
        LruImageCache cache = LruImageCache.getInstance ();
        imageView.setImageUrl (mData.get (position).img, new ImageLoader (requestQueue, cache));

        container.addView (imageView);
        return imageView;
    }

    @Override
    public boolean onTouch (View v, MotionEvent event) {
        switch (event.getAction ()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY ();
                downX = event.getRawX ();
                break;
            case MotionEvent.ACTION_UP:
                float absX = Math.abs (event.getRawX () - downX);
                float absY = Math.abs (event.getRawY () - downY);
                if (absX < 10 && absY < 10) {
                    if (justShowImage) {
                        //当前只显示了图片
                        if (mToolbar != null) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mToolbar.getLayoutParams ();
                            params.height = (int) XUtils.dp2px (mContext,48);
                            mToolbar.setLayoutParams (params);
                        }
                        if (mBackground != null) {
                            mBackground.setAlpha (1);
                        }

                        justShowImage = false;
                    } else {
                        //当前除了显示图片还有其它内容
                        if (mToolbar != null) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mToolbar.getLayoutParams ();
                            params.height = 0;
                            mToolbar.setLayoutParams (params);
                        }
                        if (mBackground != null) {
                            mBackground.setAlpha (0);
                        }
                        justShowImage = true;
                    }
                }
                break;
        }
        return true;
    }
}
