package com.suixingame.news.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/9/10 17:02
 *
 * 描 述 ：一些零散的工具方法 这些方法要经常使用 但是又不足以构成一个系统的工具类
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class XUtils {
    private static Toast toast;
    /**
     * 表示当前在测试
     */
    public final static boolean DEBUG = true;

    public static double dp2px(Context context,double dp){
        return context.getResources ().getDisplayMetrics ().density * dp;
    }

    /**
     * 单例的Toast
     * @param context
     * @param content
     */
    public static Toast toastShort(Context context,String content){
        if (toast == null){
            toast = Toast.makeText (context,content,Toast.LENGTH_SHORT);
        }else {
            toast.setText (content);
            toast.setDuration (Toast.LENGTH_SHORT);
        }
        toast.show ();
        return toast;
    }

    /**
     * 单例的Toast
     * @param context
     * @param content
     */
    public static Toast toastLong(Context context,String content){
        if (toast == null){
            toast = Toast.makeText (context,content,Toast.LENGTH_LONG);
        }else {
            toast.setText (content);
            toast.setDuration (Toast.LENGTH_LONG);
        }
        toast.show ();
        return toast;
    }

    /**
     * 获取屏幕宽高信息
     * @return
     */
    public static Point getWindowSize(Context context){
        Point windowSize = new Point ();
        WindowManager windowManager = (WindowManager) context.getSystemService (Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay ().getSize (new Point ());
        return windowSize;
    }

    /**
     * 获取定义的dp值
     * @param context
     * @param resourceId
     * @return
     */
    public static float getDimension(Context context,int resourceId){
        return context.getResources ().getDimension (resourceId);
    }

    public static String MD5(String input) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(input.getBytes());
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < md.length; i++) {
                String shaHex = Integer.toHexString(md[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
