package com.suixingame.news.bean;

import cn.bmob.v3.BmobObject;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/28 16:10
 *
 * 描 述 ：评论表
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class Comment extends BmobObject{
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 评论新闻url
     */
    private String url;
    /**
     * 评论新闻标题
     */
    private String newsTitle;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论被点赞次数
     */
    private Integer likeNumber;

    public String getUserId () {
        return userId;
    }

    public void setUserId (String userId) {
        this.userId = userId;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public String getNewsTitle () {
        return newsTitle;
    }

    public void setNewsTitle (String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public Integer getLikeNumber () {
        return likeNumber;
    }

    public void setLikeNumber (Integer likeNumber) {
        this.likeNumber = likeNumber;
    }
}
