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
 * 创建日期 ： 2016/11/2 16:36
 *
 * 描 述 ：用户新闻收藏表
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class NewsCollection extends BmobObject {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 收藏新闻的url
     */
    private String url;

    /**
     * 新闻的类型 0文字新闻 1图片新闻
     */
    private Integer type;

    public NewsCollection (String userId, String url, Integer type) {
        this.userId = userId;
        this.url = url;
        this.type = type;
    }

    public NewsCollection () {

    }

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

    public Integer getType () {
        return type;
    }

    public void setType (Integer type) {
        this.type = type;
    }

}
