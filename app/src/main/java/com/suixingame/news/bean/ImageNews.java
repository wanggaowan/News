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
 * 创建日期 ： 2016/10/18 22:19
 *
 * 描 述 ：图片新闻数据类
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class ImageNews extends BmobObject{
    /**
     * 新闻的url
     */
    private String url;

    /**
     * 图片新闻的json数据
     */
    private String jsondata;

    /**
     * 所属专栏
     */
    private Integer stopic;

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public String getJsondata () {
        return jsondata;
    }

    public void setJsondata (String jsondata) {
        this.jsondata = jsondata;
    }

    public Integer getStopic () {
        return stopic;
    }

    public void setStopic (Integer stopic) {
        this.stopic = stopic;
    }
}
