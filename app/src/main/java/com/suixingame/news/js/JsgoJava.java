package com.suixingame.news.js;

import android.webkit.JavascriptInterface;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/19 20:30
 *
 * 描 述 ：js调用java代码，webView的js交互
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class JsgoJava {
    private String mContent;
    public JsgoJava(String content){
        this.mContent = content;
    }

    @JavascriptInterface
    public String getShowData(){
        return mContent;
    }
}
