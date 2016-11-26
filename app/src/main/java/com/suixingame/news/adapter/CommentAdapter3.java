package com.suixingame.news.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suixingame.news.R;
import com.suixingame.news.bean.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * 创建日期 ： 2016/11/19 15:08
 *
 * 描 述 ：用于展示与用户关联的评论内容的适配器
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class CommentAdapter3 extends BaseAdapter {

    private Context mContext;
    /**
     * 评论数据
     */
    private List<Comment> mComments;

    public CommentAdapter3 (Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    @Override
    public int getCount () {
        return mComments.size ();
    }

    @Override
    public Comment getItem (int position) {
        return mComments.get (position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate (mContext, R.layout.view_listview_comment_user, null);
            viewHolder = new ViewHolder (convertView);
            convertView.setTag (viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag ();
        }
        Comment comment = mComments.get (position);
        viewHolder.title.setText ("原文:" + comment.getNewsTitle ());
        //时间格式化
        try {
            String time = comment.getCreatedAt ().trim ();
            SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat ("MM月dd日");
            Date date = format.parse (time);
            time = dateFormat.format (date);
            viewHolder.time.setText (time);
        } catch (ParseException e) {
            e.printStackTrace ();
        }

        viewHolder.content.setText (comment.getContent ());
        return viewHolder.rootView;
    }

    /**
     * 增加评论数据
     */
    public void addComments (List<Comment> comments) {
        mComments.addAll (comments);
    }

    class ViewHolder {
        public View rootView;

        /**
         * 评论文章的标题
         */
        public TextView title;
        /**
         * 评论文章的时间
         */
        public TextView time;
        /**
         * 评论内容
         */
        public TextView content;

        public ViewHolder (View view) {
            rootView = view;
            title = (TextView) rootView.findViewById (R.id.tv_title);
            time = (TextView) rootView.findViewById (R.id.tv_time);
            content = (TextView) rootView.findViewById (R.id.tv_content);
        }
    }
}
