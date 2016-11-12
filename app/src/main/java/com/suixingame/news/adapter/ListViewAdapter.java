package com.suixingame.news.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.suixingame.news.R;
import com.suixingame.news.bean.NewsData;
import com.suixingame.news.bean.NewsImage;
import com.suixingame.news.db.NewsImageDB;
import com.suixingame.news.util.GlobalParam;
import com.suixingame.news.util.LruImageCache;
import com.suixingame.news.util.XUtils;
import com.suixingame.news.view.ScreenImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.suixingame.news.util.XUtils.getDimension;
import static com.suixingame.news.util.XUtils.getWindowSize;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/20 16:36
 *
 * 描 述 ：
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class ListViewAdapter extends BaseAdapter {
    //两种不同的Item标志
    /**
     * 只在左边显示一张图片的布局
     */
    private static final int TYPE1 = 0;

    /**
     * 在上边显示三张图片的布局
     */
    private static final int TYPE2 = 1;


    private Context mContext;

    /**
     * 展示新闻需要的信息
     */
    private List<NewsData> mNewsDatas;

    /**
     * 用来存储异步获取图片后给哪个对象设置的Map集合
     */
    private Map<String, Map<Integer, View>> viewHolders = new HashMap<> ();

    /**
     * 缓存网络图片的缓存对象
     */
    private final LruImageCache mLruImageCache;

    public ListViewAdapter (Context context, List<NewsData> newsDatas) {
        mContext = context;
        mNewsDatas = newsDatas;
        mLruImageCache = LruImageCache.getInstance ();
    }

    @Override
    public int getCount () {
        return mNewsDatas.size ();
    }

    /**
     * 返回position对应新闻的url，用于获取新闻数据
     */
    @Override
    public NewsData getItem (int position) {
        return mNewsDatas.get (position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public int getViewTypeCount () {
        //此处定义为2，其实是定义了一个长度为2的数组，所以再getItemViewType里返回的值不要超过定义的值-1
        return 2;
    }

    @Override
    public int getItemViewType (int position) {
        return mNewsDatas.get (position).getImageCount () == 3 ? TYPE2 : TYPE1;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        NewsData newsData = mNewsDatas.get (position);
        int itemViewType = getItemViewType (position);
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;
        if (convertView == null) {
            if (itemViewType == TYPE1) {
                convertView = View.inflate (mContext, R.layout.view_news_content_type1, null);
                viewHolder1 = new ViewHolder1 (convertView);
                convertView.setTag (viewHolder1);
            } else if (itemViewType == TYPE2) {
                convertView = View.inflate (mContext, R.layout.view_news_content_type2, null);
                viewHolder2 = new ViewHolder2 (convertView);
                convertView.setTag (viewHolder2);
            }
        } else {
            if (itemViewType == TYPE1) {
                viewHolder1 = (ViewHolder1) convertView.getTag ();
            } else if (itemViewType == TYPE2) {
                viewHolder2 = (ViewHolder2) convertView.getTag ();
            }
        }
        Map<Integer, View> map = new HashMap<> ();
        map.put (position, convertView);
        viewHolders.put (newsData.getUrl (), map);
        //只有第一种和第二种新闻要加载Item图片
        if (position < 4){
            //加载前四条新闻的图片，其它图片滑动时才加载
            NewsImageDB.getNewsImage (newsData.getUrl (), mHandler);
        }
        if (itemViewType == TYPE1) {
            viewHolder1.title.setText (newsData.getTitle ().trim ());
            viewHolder1.source.setText (newsData.getSource ().trim ());
            viewHolder1.seeNumber.setText (newsData.getSeeNumber () + "人看过");
            //设置默认展示的图片
            viewHolder1.screenImageView.setImageResource (R.drawable.news_image_default);

            //设置视频新闻标志
            if (newsData.isVideo ()) {
                viewHolder1.seeNumber.setCompoundDrawablesWithIntrinsicBounds (0, 0, R.drawable.video, 0);
            } else {
                //重用时如果没有比方法，一些不是视频的也被加上标记了
                viewHolder1.seeNumber.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0);
            }

            return viewHolder1.rootView;
        } else {
            viewHolder2.title.setText (newsData.getTitle ().trim ());
            viewHolder2.source.setText (newsData.getSource ().trim ());
            viewHolder2.seeNumber.setText (newsData.getSeeNumber () + "次浏览");

            //设置视频新闻标志
            if (newsData.isVideo ()) {
                viewHolder2.seeNumber.setCompoundDrawablesWithIntrinsicBounds (0, 0, R.drawable.video, 0);
            } else {
                //重用时如果没有比方法，一些不是视频的也被加上标记了
                viewHolder2.seeNumber.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0);
            }

            //设置默认展示的图片
            for(ScreenImageView imageView:viewHolder2.screenImageViews){
                imageView.setImageResource (R.drawable.news_image_default);
            }

            return viewHolder2.rootView;
        }
    }

    /**
     * 获取当前lisView展示的专题Key
     */
    public Integer getStopic () {
        return mNewsDatas.get (0).getStopic ();
    }

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what) {
                case GlobalParam.newsImageIsReady:
                    List<NewsImage> images = (List<NewsImage>) msg.obj;
                    if (images != null && images.size () > 0) {
                        Map<Integer, View> map = viewHolders.get (images.get (0).getUrl ());
                        if (map == null){
                            return;
                        }
                        View view = null;
                        int itemViewType = TYPE1;
                        for (Integer position : map.keySet ()) {
                            view = map.get (position);
                            itemViewType = getItemViewType (position);
                            break;
                        }
                        if (view != null) {
                            setViewData (itemViewType, images, view);
                        }
                    }
                    break;
            }
        }
    };

    public Handler getHandler () {
        return mHandler;
    }

    /**
     * 设置Item的数据
     */
    private void setViewData (int itemViewType, List<NewsImage> images, View view) {
        if (itemViewType == TYPE1) {
            final ViewHolder1 viewHolder1 = (ViewHolder1) view.getTag ();
            if (images.size () > 0) {
                String url = images.get (0).getImageurl ();
                ScreenImageView screenImageView = viewHolder1.screenImageView;
                Bitmap bitmap = mLruImageCache.getBitmap (url);
                if (bitmap !=null){
                    //有缓存
                    screenImageView.setImageBitmap (bitmap);
                }else {
                    //无缓存
                    int maxWidth = (int) getDimension (mContext, R.dimen.newsData_item_type1_image_width);
                    int maxHeight = (int) getDimension (mContext, R.dimen.newsData_item_type1_image_height);
                    RequestQueue requestQueue = Volley.newRequestQueue (mContext);
                    requestQueue.add (imageRequest (url,screenImageView,maxWidth,maxHeight));
                    requestQueue.start ();
                }
            }
        } else if (itemViewType == TYPE2) {
            ViewHolder2 viewHolder2 = (ViewHolder2) view.getTag ();
            Point windowSize = getWindowSize (mContext);
            int maxWidth = windowSize.x / 3;
            int maxHeight = (int) XUtils.getDimension (mContext,R.dimen.newsData_item_type2_image_height);
            Bitmap bitmap;
            for (int i = 0; i <viewHolder2.screenImageViews.size () ; i++) {
                String url = images.get (i).getImageurl ();
                ScreenImageView screenImageView = viewHolder2.screenImageViews.get (i);
                bitmap = mLruImageCache.getBitmap (url);
                if (bitmap!=null){
                    //有缓存
                    screenImageView.setImageBitmap (bitmap);
                }else {
                    //无缓存
                    RequestQueue requestQueue = Volley.newRequestQueue (mContext);
                    requestQueue.add (imageRequest (url,screenImageView,maxWidth,maxHeight));
                    requestQueue.start ();
                }
            }
        }
    }

    /**
     * 获取网络图片并根据要求压缩
     * @param url 图片地址
     * @param view 设置图片的ImageView
     * @param maxWidth 获取图片压缩后的最大宽度
     * @param maxHeight 获取图片压缩后的最大高度
     * @return
     */
    private ImageRequest imageRequest(final String url, final ScreenImageView view, int maxWidth, int maxHeight){
        return new ImageRequest (url,//
                new Response.Listener<Bitmap> () {
                    @Override
                    public void onResponse (Bitmap response) {
                        view.setImageBitmap (response);
                        mLruImageCache.putBitmap (url,response);
                    }
                },maxWidth,maxHeight ,Bitmap.Config.ARGB_4444,//
                new Response.ErrorListener () {
                    @Override
                    public void onErrorResponse (VolleyError error) {
                        Log.e (GlobalParam.App_Log,"请求照片出错:"+url);
                    }
                });
    }


    /**
     * 加载更多更新数据
     */
    public void updateDataForLoadMore (List<NewsData> list) {
        mNewsDatas.addAll (list);
    }

    /**
     * type1对应的数据实体
     */
    class ViewHolder1 {
        /**
         * 界面根布局
         */
        public View rootView;

        /**
         * 新闻图片
         */
        public ScreenImageView screenImageView;

        /**
         * 新闻标题
         */
        public TextView title;

        /**
         * 新闻浏览次数
         */
        public TextView source;

        /**
         * 新闻评论
         */
        public TextView seeNumber;

        public ViewHolder1 (View view) {
            rootView = view;
            screenImageView = (ScreenImageView) rootView.findViewById (R.id.niv_image);
            title = (TextView) rootView.findViewById (R.id.tv_title);
            source = (TextView) rootView.findViewById (R.id.tv_source);
            seeNumber = (TextView) rootView.findViewById (R.id.tv_seeNumber);

        }

    }

    /**
     * type2对应的数据实体
     */
    class ViewHolder2 {
        /**
         * 界面根布局
         */
        public View rootView;

        /**
         * 新闻图片集合
         */
        public List<ScreenImageView> screenImageViews;

        /**
         * 新闻标题
         */
        public TextView title;

        /**
         * 新闻来源
         */
        public TextView source;

        /**
         * 新闻浏览次数
         */
        public TextView seeNumber;

        public ViewHolder2 (View view) {
            rootView = view;

            title = (TextView) rootView.findViewById (R.id.tv_title);
            source = (TextView) rootView.findViewById (R.id.tv_source);
            seeNumber = (TextView) rootView.findViewById (R.id.tv_seeNumber);

            screenImageViews = new ArrayList<> ();
            screenImageViews.add ((ScreenImageView) rootView.findViewById (R.id.niv_image1));
            screenImageViews.add ((ScreenImageView) rootView.findViewById (R.id.niv_image2));
            screenImageViews.add ((ScreenImageView) rootView.findViewById (R.id.niv_image3));
        }
    }

}
