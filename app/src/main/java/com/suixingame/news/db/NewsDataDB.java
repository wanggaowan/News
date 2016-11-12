package com.suixingame.news.db;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.suixingame.news.bean.NewsData;
import com.suixingame.news.util.GlobalParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/20 15:54
 *
 * 描 述 ：
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class NewsDataDB {

    /**
     * 获取新闻数据,用于ListView 加载更多
     *
     * @param stopic  新闻专题
     * @param date  基于该时间之后的数据
     * @param handler 用于将数据带出
     * @param what    用于判断带出的数据
     */
    public static void getNewsData (Integer stopic,String date, final Handler handler, final int what) {
        BmobQuery<NewsData> query = new BmobQuery<> ();
        SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        try {
            query.addQueryKeys ("url,title,source,seeNumber,stopic,imageCount,isVideo");
            query.addWhereLessThanOrEqualTo ("createdAt",new BmobDate (format.parse (date)));
            query.addWhereEqualTo ("stopic", stopic);
            query.order ("-createdAt");
            query.setLimit (20);
            query.findObjects (new FindListener<NewsData> () {
                @Override
                public void done (List<NewsData> list, BmobException e) {
                    if (e != null) {
                        Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                    }
                    Message message = new Message ();
                    message.obj = list;
                    message.what = what;
                    handler.sendMessage (message);
                }
            });
        } catch (ParseException e) {
            Log.e (GlobalParam.App_Log,"格式化时间出错!");
            handler.sendEmptyMessage (what);
        }
    }

    /**
     * 获取新闻数据,用于ListView 下拉刷新，初始化
     *
     * @param stopic  新闻专题
     * @param useCache 是否优先从缓存拿数据
     * @param handler 用于将数据带出
     * @param what    用于判断带出的数据
     */
    public static void getNewsData (Integer stopic,boolean useCache, final Handler handler, final int what) {
        BmobQuery<NewsData> query = new BmobQuery<> ();
        query.addQueryKeys ("url,title,source,seeNumber,stopic,imageCount,isVideo");
        query.addWhereEqualTo ("stopic", stopic);
        query.order ("-createdAt");
        query.setLimit (20);
        if (useCache){
            //设置查询缓存，也就是本地数据缓存
//            System.out.println ("优先从缓存拿数据");
            boolean b = query.hasCachedResult (NewsData.class);
            if (b){
//                System.out.println ("从缓存");
                query.setCachePolicy (BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            }else {
//                System.out.println ("从网络");
                query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }
        }else {
            query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ONLY);

        }
        //设置缓存最大保持时间2天
        query.setMaxCacheAge (java.util.concurrent.TimeUnit.DAYS.toMillis (2));
        query.findObjects (new FindListener<NewsData> () {
            @Override
            public void done (List<NewsData> list, BmobException e) {
                if (e != null) {
                    Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                }
                Message message = new Message ();
                message.what = what;
                message.obj = list;
                handler.sendMessage (message);
            }
        });
    }

    /**
     * 获取新闻详情数据
     *
     * @param url     新闻地址
     * @param handler 用于将数据带出
     * @param what    用于判断带出的数据
     */
    public static void getNewsDataDetail (String url, final Handler handler, final int what) {
        BmobQuery<NewsData> query = new BmobQuery<> ();
        query.addQueryKeys ("url,content");
        query.addWhereEqualTo ("url", url);
        query.setLimit (1);
        //设置查询缓存，也就是本地数据缓存
        boolean b = query.hasCachedResult (NewsData.class);
        if (b){
            query.setCachePolicy (BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        }else {
            query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ONLY);
        }
        //设置缓存最大保持时间2天
        query.setMaxCacheAge (java.util.concurrent.TimeUnit.DAYS.toMillis (2));
        query.findObjects (new FindListener<NewsData> () {
            @Override
            public void done (List<NewsData> list, BmobException e) {
                if (e != null) {
                    Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                }
                Message message = new Message ();
                message.what = what;
                message.obj = list;
                handler.sendMessage (message);
            }
        });
    }

    /**
     * 更新新闻浏览次数
     * @param objectId
     */
    public static void updateSeeNumber(String objectId,boolean isVideo){
        NewsData newsData = new NewsData ();
        newsData.setIsViewo (isVideo);
        newsData.increment ("seeNumber");//seeNumber增加1
        //监听器不能为空，否则调用该方法无效，估计这应该是Bug
        newsData.update (objectId, new UpdateListener () {
            @Override
            public void done (BmobException e) {
              if (e!=null){
                  Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
              }
            }
        });
    }
}
