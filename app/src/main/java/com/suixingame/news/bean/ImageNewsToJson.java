package com.suixingame.news.bean;

import java.io.Serializable;
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
 * 创建日期 ： 2016/10/22 20:10
 *
 * 描 述 ：从json解析后获取的数据
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class ImageNewsToJson implements Serializable {
    /**
     * 新闻数据获取的url
     */
    public String url;

    /**
     * 新闻所属的专题
     */
    public Integer stopic;

    /**
     * 新闻信息概览
     */
    public ImageNewsOver info;

    /**
     * 新闻中的图片数据集
     */
    public List<ImageNewsItem> list;

    /**
     * 新闻信息概览
     */
    public class ImageNewsOver implements Serializable{
        /**
         * 新闻总标题
         */
        public String setname;

        /**
         * 该新闻中图片数量
         */
        public Integer imgsum;

        /**
         * 图片新闻发布日期
         */
        public String lmodify;

        /**
         * 图片来源
         */
        public String source;

    }

    public class ImageNewsItem  implements Serializable{
        /**
         * 图片地址
         */
        public String img;

        /**
         * 图片标题
         */
        public String title;

        /**
         * 图片的宽高
         */
        public ImageSize osize;


        public class ImageSize implements Serializable{
            /**
             * 图片宽度
             */
            public Integer w;

            /**
             * 图片高度
             */
            public Integer h;
        }
    }
}

