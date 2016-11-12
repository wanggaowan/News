package com.suixingame.news.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suixingame.news.bean.Topic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/18 19:05
 *
 * 描 述 ：定义一些全局需要的参数信息
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class GlobalParam {

    /**
     * 专栏信息用SharedPreferences存储到本地的key
     * 主要是记录用户自定义专栏板块顺序
     */
    public static final String SHAREDPREFERENCESTOPICDATA = "topicData";

    /**
     * 定义专栏信息
     */
    private static final String[] topicsValue = new String[]{"头条","娱乐","体育","财经","科技","汽车","游戏","旅游","教育","公益","校园"};

    //日志标志
    /**
     * 数据库日志的tag
     */
    public static final String LOG_BMOB = "bmoblog";

    /**
     * jsoup解析网页日志tag
     */
    public static final String LOG_JSOUP = "jsouplog";

    /**
     * 程序日志
     */
    public static final String App_Log = "com.suixingame.zixun";

    //数据标志

    /**
     * 专栏数据标志
     */
    public static final int TopicData = 1;

    /**
     * 文字新闻数据标志
     */
    public static final int NewsData = 2;
    public static final String NewsDataTag = "NewsData";
    public static final int NewsDataIsReady = 11;


    /**
     * 图片新闻数据是否准备好
     */
    public static final int imageNewsData = 6;
    public static final String imageNewsDataTag = "imageNewsData";
    /**
     * ViewPager展示下一个图片新闻
     */
    public static final int nextImageNews = 7;

    /**
     * 评论添加标志
     */
    public static final int commentAdd = 8;

    /**
     * 评论获取标志1 获取最新的评论数据
     */
    public static final int commentNew = 9;

    /**
     * 评论获取标志1 获取最热门的评论数据
     */
    public static final int commentHot = 10;

    //新闻收藏标志
    /**
     * 查找收藏新闻
     */
    public static final int newsCollectionFind = 12;

    /**
     * 增加新闻收藏
     */
    public static final int newsCollectionAdd = 13;

    /**
     * 删除新闻收藏
     */
    public static final int newsCollectionDelete = 14;


    /**
     * 用户数据 BmobUser
     */
    public static final String userData = "BmobUser";

    //数据准备状态标志

    /**
     * 文字新闻图片准备好
     */
    public static final int newsImageIsReady = 3;

    /**
     * 文字新闻数据是否准备好
     */
    public static boolean newsDataIsReady = false;

    /**
     * 图片新闻数据是否准备好
     */
    public static boolean imageNewsIsReady = false;

    /**
     * ListView加载更多
     */
    public static final int listViewLoadMore = 4;

    /**
     * listView下拉刷新
     */
    public static final int listViewPullRefresh = 5;




    public static void resetDataStatus(){
        imageNewsIsReady = false;
        newsDataIsReady = false;
    }

    /**
     * 返回专栏信息
     * @return
     */
    public static List<Topic> getTopics(Context context) throws JSONException {
        List<Topic> topics;
        Gson gson = new Gson ();
        Type type = new TypeToken<List<Topic>> () {}.getType ();
        String value = SharedPreferencesUtil.getString (context, SHAREDPREFERENCESTOPICDATA, "");
        if (!"".equals (value)){
            topics = gson.fromJson (value, type);
        }else {
            topics = gson.fromJson (topicsToJson (topicsValue), type);
        }
        return topics;

    }

    /**
     * 生成默认的新闻板块排序数据
     * @param topicsValue
     * @return
     * @throws JSONException
     */
    private static String topicsToJson(String[] topicsValue) throws JSONException {
        JSONArray jsonArray = new JSONArray ();
        for (int i = 0; i <topicsValue.length ; i++) {
            JSONObject jsonObject = new JSONObject ();
                jsonObject.put ("key",i+1);
                jsonObject.put("value",topicsValue[i]);
                jsonArray.put (jsonObject);
        }
        return jsonArray.toString ();
    }

    /**
     * 将dp单位转化为px单位
     * @param context
     * @param dp
     * @return
     */
    public static double dp2px(Context context,double dp){
        return context.getResources ().getDisplayMetrics ().density * dp;
    }

}
