package com.suixingame.news.db;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.suixingame.news.bean.ImageNews;
import com.suixingame.news.util.GlobalParam;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

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

public class ImageNewsDB {

    /**
     * 获取图片新闻数据
     * @param stopic
     * @param useCache 是否优先采用缓存
     * @param handler
     * @param what
     */
    public static void getImageNews (Integer stopic,boolean useCache, final Handler handler, final int what) {
        BmobQuery<ImageNews> query = new BmobQuery<> ();
        query.addWhereEqualTo ("stopic", stopic);
        query.order ("-createdAt");
        query.setLimit (4);
        if (useCache){
            //设置查询缓存，也就是本地数据缓存
            boolean b = query.hasCachedResult (ImageNews.class);
            if (b){
                query.setCachePolicy (BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            }else {
                query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }
        }else {
            query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ONLY);
        }
        //设置缓存最大保持时间1天
        query.setMaxCacheAge (java.util.concurrent.TimeUnit.DAYS.toMillis (1));
        query.findObjects (new FindListener<ImageNews> () {
            @Override
            public void done (List<ImageNews> list, BmobException e) {
                if (e!=null){
                    Log.e (GlobalParam.LOG_BMOB,e.getMessage ());
                }
                Message msg = new Message ();
                msg.what = what;
                msg.obj = list;
                handler.sendMessage (msg);
            }
        });

    }
}
