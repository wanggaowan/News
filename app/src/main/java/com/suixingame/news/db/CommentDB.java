package com.suixingame.news.db;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.suixingame.news.bean.Comment;
import com.suixingame.news.util.GlobalParam;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
 * 创建日期 ： 2016/10/28 16:12
 *
 * 描 述 ：处理和数据库评论表交互的逻辑
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class CommentDB {

    /**
     * 添加评论
     *
     * @param comment 评论bean
     */
    public static void addComment (Comment comment, final Handler handler, final int what) {
        comment.save (new SaveListener<String> () {
            @Override
            public void done (String s, BmobException e) {
                boolean success = true;
                if (e != null) {
                    success = false;
                    Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                }
                Message message = new Message ();
                message.what = what;
                message.obj = success;
                handler.sendMessage (message);
            }
        });
    }

    /**
     * 更新评论被赞次数
     * @param objectId
     * @param handler
     * @param what
     */
    public static void updateLikeNumber(String objectId, final Handler handler, final int what){
        Comment comment = new Comment ();
        comment.increment ("likeNumber");//likeNumber增加1
        comment.update (objectId, new UpdateListener () {
            @Override
            public void done (BmobException e) {
                boolean success = true;
                if (e!=null){
                    success = false;
                }
                Message msg = new Message ();
                msg.what = what;
                msg.obj = success;
                handler.sendMessage (msg);
            }
        });
    }


    /**
     * 获取最热评论
     * @param url 评论新闻url
     */
    public static void getCommentFavorite (String url,Integer skip, final Handler handler, final int what) {
        BmobQuery<Comment> query = new BmobQuery<> ();
        query.addWhereEqualTo ("url", url);
        query.order ("-likeNumber");
        query.setLimit (20);
        query.setSkip (skip);
        query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        //设置缓存最长保留一天
        query.setMaxCacheAge (TimeUnit.DAYS.toMillis (1));
        query.findObjects (new FindListener<Comment> () {
            @Override
            public void done (List<Comment> list, BmobException e) {
                if (e!=null){
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
     * 获取新闻Activity展示的最新十条评论
     * @param url 评论新闻url
     */
    public static void getCommentJustTen (String url, final Handler handler, final int what) {
        BmobQuery<Comment> query = new BmobQuery<> ();
        query.addWhereEqualTo ("url", url);
        query.order ("-createdAt");
        query.setLimit (10);
        query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        //设置缓存最长保留一天
        query.setMaxCacheAge (TimeUnit.DAYS.toMillis (1));
        query.findObjects (new FindListener<Comment> () {
            @Override
            public void done (List<Comment> list, BmobException e) {
                if (e!=null){
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
     * 获取最新评论
     * @param url 评论新闻url
     */
    public static void getCommentNew (String url,Integer skip, final Handler handler, final int what) {
        BmobQuery<Comment> query = new BmobQuery<> ();
        query.addWhereEqualTo ("url", url);
        query.order ("-createdAt");
        query.setLimit (20);
        query.setSkip (skip);
        query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        //设置缓存最长保留一天
        query.setMaxCacheAge (TimeUnit.DAYS.toMillis (1));
        query.findObjects (new FindListener<Comment> () {
            @Override
            public void done (List<Comment> list, BmobException e) {
                if (e!=null){
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
     * 获取单个用户的所有评论
     * @param userId 评论用户Id
     */
    public static void getCommentWithUser (String userId, int skip, final Handler handler, final int what) {
        BmobQuery<Comment> query = new BmobQuery<> ();
        query.addWhereEqualTo ("userId", userId);
        query.order ("-createdAt");
        query.setLimit (20);
        query.setSkip (skip);
        query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        //设置缓存最长保留7天
        query.setMaxCacheAge (TimeUnit.DAYS.toMillis (7));
        query.findObjects (new FindListener<Comment> () {
            @Override
            public void done (List<Comment> list, BmobException e) {
                if (e!=null){
                    Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                }
                Message message = new Message ();
                message.what = what;
                message.obj = list;
                handler.sendMessage (message);
            }
        });
    }


}
