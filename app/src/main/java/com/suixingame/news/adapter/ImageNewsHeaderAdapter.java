package com.suixingame.news.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.suixingame.news.R;
import com.suixingame.news.activity.ImageNewsActivity;
import com.suixingame.news.bean.ImageNewsToJson;
import com.suixingame.news.util.GlobalParam;
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
 * 创建日期 ： 2016/10/22 20:16
 *
 * 描 述 ：每个栏目头部图片新闻适配器
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class ImageNewsHeaderAdapter extends PagerAdapter{
    private Context mContext;
    private List<ImageNewsToJson> mDatas;
    /**
     * 照片缓存
     */
    private LruImageCache mCache;

    /**
     * 该轮播条的数目，这么大只是为了轮播
     */
    public static final int maxCount = 1000;
    public ImageNewsHeaderAdapter(Context context, List<ImageNewsToJson> datas){
        mContext = context;
        mDatas = datas;
        mCache = LruImageCache.getInstance ();
    }
    @Override
    public int getCount () {
        //设置两百是为了轮播
        return maxCount;
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
    public Object instantiateItem (ViewGroup container, final int position) {
        final int tempPosition = position % mDatas.size ();
        View view = View.inflate (mContext, R.layout.view_lisview_viewpager_header_item, null);
        ImageView imageView = (ImageView) view.findViewById (R.id.iv_image);
        LinearLayout background = (LinearLayout) view.findViewById (R.id.ll_background);

        view.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent (mContext, ImageNewsActivity.class);
                intent.putExtra (GlobalParam.imageNewsDataTag,mDatas.get (tempPosition));
                mContext.startActivity (intent);
            }
        });
        container.addView (view);

        //获取网络图片或者拿到缓存
        List<ImageNewsToJson.ImageNewsItem> list = mDatas.get (tempPosition).list;
        String url = list.get (0).img;
        for(ImageNewsToJson.ImageNewsItem item : list){
            if (item.osize.w > item.osize.h){
                //选取一张宽大于高的图片用于展示,默认拿图片集的第一张
                url = item.img;
                break;
            }
        }
        Bitmap bitmap = mCache.getBitmap (url);
        if (bitmap!=null){
            palette (bitmap,background);
            imageView.setImageBitmap (bitmap);
        }else {
            doImageRequest (url,imageView,background);
        }
        return view;
    }

    /**
     * 从网络拿照片
     * @param url
     * @param imageView
     * @param background
     */
    private void doImageRequest(final String url, final ImageView imageView, final LinearLayout background){
        RequestQueue queue = Volley.newRequestQueue (mContext);
        Point windowSize = XUtils.getWindowSize (mContext);
        int maxHeight = (int) mContext.getResources ().getDimension (R.dimen.imageNewsHeaderHeight);
        //请求网络数据
        ImageRequest request = new ImageRequest (url, new Response.Listener<Bitmap> () {
            @Override
            public void onResponse (Bitmap response) {
                palette (response,background);
                imageView.setImageBitmap (response);
                mCache.putBitmap (url,response);
            }
        }, windowSize.x,maxHeight, Bitmap.Config.ARGB_4444,new Response.ErrorListener () {
            @Override
            public void onErrorResponse (VolleyError error) {
                if (error!=null){
                    Log.e (GlobalParam.App_Log,error.getMessage ());
                }
            }
        });
        queue.add (request);
        queue.start ();
    }

    /**
     * 取色板
     * @param bitmap
     * @param background
     */
    private void palette(Bitmap bitmap, final LinearLayout background){
        Palette.Builder from = Palette.from (bitmap);
        from.generate (new Palette.PaletteAsyncListener () {
            @Override
            public void onGenerated (Palette palette) {
                background.setBackgroundColor (palette.getDarkVibrantColor (Color.TRANSPARENT));
            }
        });
    }
}
