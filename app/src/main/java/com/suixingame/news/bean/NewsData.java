package com.suixingame.news.bean;

import java.util.List;

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
 * 创建日期 ： 2016/10/18 15:38
 *
 * 描 述 ：新闻数据
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class NewsData extends BmobObject{
    /**
     * 新闻地址
     */
    private String url;

    /**
     * 新闻标题
     */
    private String title;

    private String source;

    /**
     * 新闻内容
     */
    private String content;

    /**
     * 新闻浏览次数
     */
    private Integer seeNumber;

    /**
     * 新闻所属专栏
     */
    private Integer stopic;

    /**
     * 新闻中图片的数量
     */
    private Integer imageCount;

    /**
     * 该新闻是否包含视频
     */
    private boolean isVideo;

    /**
     * 新闻中图片的信息
     */
    private List<NewsImage> mImages;

    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getSource () {
        return source;
    }

    public void setSource (String source) {
        this.source = source;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public Integer getSeeNumber () {
        return seeNumber;
    }

    public void setSeeNumber (Integer seeNumber) {
        this.seeNumber = seeNumber;
    }

    public Integer getStopic () {
        return stopic;
    }

    public void setStopic (Integer stopic) {
        this.stopic = stopic;
    }

    public Integer getImageCount () {
        return imageCount;
    }

    public void setImageCount (Integer imageCount) {
        this.imageCount = imageCount;
    }

    public boolean isVideo () {
        return isVideo;
    }

    public void setIsViewo (boolean isVideo) {
        this.isVideo = isVideo;
    }

    public List<NewsImage> getImages () {
        return mImages;
    }

    public void setImages (List<NewsImage> images) {
        mImages = images;
    }

    @Override
    public String toString () {
        return "NewsData{" +
                "stopic='" + stopic + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", seeNumber=" + seeNumber +
                '}';
    }
}
