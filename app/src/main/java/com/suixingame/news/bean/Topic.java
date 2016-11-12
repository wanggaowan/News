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
 * 创建日期 ： 2016/10/18 19:22
 *
 * 描 述 ：新闻类别
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class Topic extends BmobObject{
    /**
     * 专题key
     */
    private Integer key;

    /**
     * 专题的值
     */
    private String value;

    public Integer getKey () {
        return key;
    }

    public void setKey (Integer key) {
        this.key = key;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }
}
