package com.suixingame.news.db;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.suixingame.news.bean.NewsImage;
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

public class NewsImageDB {

    /**
     * 获取文字新闻中含有的图片新闻的信息
     *
     * @param url 图片对应新闻的url
     */
    public static void getNewsImage (String url, final Handler handler) {
        BmobQuery<NewsImage> query = new BmobQuery<> ();
        query.addWhereEqualTo ("url", url);
        //设置查询缓存，也就是本地数据缓存
        boolean b = query.hasCachedResult (NewsImage.class);
        if (b){
            query.setCachePolicy (BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        }else {
            query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ONLY);
        }
        //设置缓存最大保持时间1天
        query.setMaxCacheAge (java.util.concurrent.TimeUnit.DAYS.toMillis (1));
        query.findObjects (new FindListener<NewsImage> () {
            @Override
            public void done (List<NewsImage> list, BmobException e) {
                if (e != null) {
                    Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                    return;
                }
                Message msg = new Message ();
                msg.what = GlobalParam.newsImageIsReady;
                msg.obj = list;
                handler.sendMessage (msg);
            }
        });

    }
}
