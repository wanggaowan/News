package com.suixingame.news.db;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.suixingame.news.bean.Topic;
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
 * 创建日期 ： 2016/10/20 15:55
 *
 * 描 述 ：
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class TopicDB {
    /**
     * 获取新闻专栏
     */
    public static void getTopic (final Handler handler, final int what) {
        BmobQuery<Topic> query = new BmobQuery<> ();
        query.order ("key");
        query.findObjects (new FindListener<Topic> () {
            @Override
            public void done (List<Topic> list, BmobException e) {
                if (e!=null){
                    Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                    return;
                }
                Message message = new Message ();
                message.what = what;
                message.obj = list;
                handler.sendMessage (message);
            }
        });
    }

}
