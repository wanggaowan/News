package com.suixingame.news.util;

import android.content.Context;
import android.content.SharedPreferences;
/**
 * ============================================================
 * <p/>
 * 版 权 ： (c) 2016
 * <p/>
 * 作 者 : 汪高皖
 * <p/>
 * 版 本 ： 1.0
 * <p/>
 * 创建日期 ： 2016/3/27 15:45
 * <p/>
 * 描 述 ：SharedPreferences工具类
 * <p/>
 * 修订历史 ：
 * <p/>
 * ============================================================
 **/
public class SharedPreferencesUtil {
    private final static String NAME = "globalConfig";

    public static boolean getBoolean(Context context,String key,boolean defValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences (NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean (key,defValue);
    }

    public static void setBoolean(Context context,String key,boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences (NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit ().putBoolean (key,value).commit ();
    }

    public static String getString(Context context,String key,String defValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences (NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString (key,defValue);
    }

    public static void setString(Context context,String key,String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences (NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit ().putString (key,value).commit ();

    }


}
