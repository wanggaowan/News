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
 * 创建日期 ： 2016/10/18 15:40
 *
 * 描 述 ：新闻中的图片资源
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class NewsImage extends BmobObject{
    /**
     * 图片的url
     */
    private String imageurl;

    /**
     * 图片关联章的url
     */
    private String url;

    public String getImageurl () {
        return imageurl;
    }

    public void setImageurl (String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    @Override
    public String toString () {
        return "NewsImage{" +
                "imageurl='" + imageurl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
