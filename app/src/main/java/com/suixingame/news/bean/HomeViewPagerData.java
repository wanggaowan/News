package com.suixingame.news.bean;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/10 12:13
 *
 * 描 述 ：首页需要的数据的对象
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class HomeViewPagerData {
    public String pageTitle;
    public String key;

    public HomeViewPagerData (String pageTitle, String key) {
        this.pageTitle = pageTitle;
        this.key = key;
    }
}
