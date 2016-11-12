package com.suixingame.news.db;

import android.os.Handler;
import android.os.Message;

import com.suixingame.news.bean.NewsCollection;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
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
 * 创建日期 ： 2016/11/2 17:00
 *
 * 描 述 ：与用户收藏新闻表交互
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class NewsCollectionDB {

    /**
     * 添加收藏
     * @param userId  当前登录用户Id
     * @param url 当前正在浏览的新闻url
     * @param type 新闻的类型，是文字新闻(0)还是图片新闻(1)
     * @param handler
     * @param what
     */
    public static void addNewsCollection(String userId,String url,Integer type,final Handler handler, final int what){
        NewsCollection newsCollection = new NewsCollection ();
        newsCollection.setUserId (userId);
        newsCollection.setUrl (url);
        newsCollection.setType (type);
        newsCollection.save (new SaveListener<String> () {
            @Override
            public void done (String s, BmobException e) {
                Message message = new Message ();
                if (e!=null){
                    message.obj = false;
                }else {
                    message.obj = true;
                }
                message.what = what;
                handler.sendMessage (message);
            }
        });
    }

    /**
     * 删除收藏
     * @param objectId 收藏新闻对应的数据的id
     * @param handler
     * @param what
     */
    public static void deleteNewsCollection(String objectId,final Handler handler, final int what){
        NewsCollection newsCollection = new NewsCollection ();
        newsCollection.delete (objectId, new UpdateListener () {
            @Override
            public void done (BmobException e) {
                Message message = new Message ();
                if (e!=null){
                    message.obj = false;
                }else {
                    message.obj = true;
                }
                message.what = what;
                handler.sendMessage (message);
            }
        });
    }

    /**
     * 查询收藏
     * @param userId 当前登录用户Id
     * @param url 当前正在浏览的新闻url
     * @param handler
     * @param what
     */
    public static void findNewsCollection(String userId, String url, final Handler handler, final int what){
        BmobQuery<NewsCollection> query = new BmobQuery<> ();
        query.addWhereEqualTo ("userId",userId);
        query.addWhereEqualTo ("url",url);
        query.findObjects (new FindListener<NewsCollection> () {
            @Override
            public void done (List<NewsCollection> list, BmobException e) {
                Message message = new Message ();
                message.obj = list;
                message.what = what;
                handler.sendMessage (message);
            }
        });

    }

}
