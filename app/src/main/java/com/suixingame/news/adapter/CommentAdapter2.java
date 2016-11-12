package com.suixingame.news.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suixingame.news.R;
import com.suixingame.news.bean.Comment;
import com.suixingame.news.util.GlobalParam;
import com.suixingame.news.util.XUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/29 22:56
 *
 * 描 述 ：用于CommentActivity的lisView的适配
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class CommentAdapter2 extends BaseAdapter {
    private Context mContext;
    /**
     * item的类型
     */
    private final int type1 = 0;
    private final int type2 = 1;
    /**
     * 评论数据
     */
    private List<Comment> mComments;

    private Integer commentType;

    public CommentAdapter2 (Context context, List<Comment> comments, Integer key) {
        mContext = context;
        mComments = comments;
        commentType = key;
    }

    @Override
    public int getCount () {
        if (mComments.size () == 0){
            return 1;
        }else {
            return mComments.size ();
        }
    }

    @Override
    public Comment getItem (int position) {
        if (mComments.size () == 0){
            return null;
        }
        return mComments.get (position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }


    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        if (position == 0 && mComments.size () == 0){
            //无评论数据
            if (convertView == null){
                TextView textView = new TextView (mContext);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams (AbsListView.LayoutParams.MATCH_PARENT, (int) XUtils.dp2px (mContext, 40));
                textView.setLayoutParams (params);
                textView.setTextSize (16);
                textView.setGravity (Gravity.CENTER);
                if (commentType == GlobalParam.commentNew){
                    textView.setText ("暂无最新评论,快去评论吧!");
                }else{
                    textView.setText ("暂无最热评论,快去评论吧!");
                }
                convertView = textView;
            }
            return convertView;
        }else {
            //有评论数据
            ViewHolder viewHolder;
           if (convertView == null){
               convertView = View.inflate (mContext, R.layout.view_listview_comment, null);
               viewHolder = new ViewHolder (convertView);
               convertView.setTag (viewHolder);
           }else {
               viewHolder = (ViewHolder) convertView.getTag ();
           }
            //评论数据展示
            final Comment comment = mComments.get (position);
            setUserNick (viewHolder.userNick, comment.getUserId ());
            //设置发表时间
            String time = comment.getCreatedAt ();
            int beginIndex = time.indexOf ("-") + 1;
            int endIndex = time.lastIndexOf (":");
            time = time.substring (beginIndex, endIndex);
            viewHolder.time.setText (time);
            //设置评论内容
            viewHolder.comment.setText (comment.getContent ());
            //设置点赞次数
            Integer likeNumber = comment.getLikeNumber ();
            if (likeNumber != null && likeNumber > 0) {
                viewHolder.like.setText (String.valueOf (likeNumber));
            }
            viewHolder.like.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    TextView temp = (TextView) v;
                    updateCommentLikeNumber (temp, comment.getObjectId ());
                }
            });
            return convertView;
        }

    }

    /**
     * 设置评论用户的昵称，需要从网络获取
     */
    private void setUserNick (final TextView userNick, String objectId) {
        BmobQuery<BmobUser> query = new BmobQuery<BmobUser> ();
        query.addQueryKeys ("username");
        query.addWhereEqualTo ("objectId", objectId);
        boolean b = query.hasCachedResult (BmobUser.class);
        if (b) {
            query.setCachePolicy (BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        } else {
            query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ONLY);
        }
        query.setMaxCacheAge (TimeUnit.DAYS.toMillis (365));
        query.findObjects (new FindListener<BmobUser> () {
            @Override
            public void done (List<BmobUser> list, BmobException e) {
                if (e != null) {
                    Log.e (GlobalParam.LOG_BMOB, e.getMessage ());
                    userNick.setText ("火星网友");
                } else {
                    userNick.setText (list.get (0).getUsername ());
                }
            }
        });
    }

    private void updateCommentLikeNumber (final TextView view, String objectId) {
        Comment comment = new Comment ();
        comment.increment ("likeNumber");//likeNumber增加1,这是线程安全的
        comment.update (objectId, new UpdateListener () {
            @Override
            public void done (BmobException e) {
                if (e == null) {
                    view.setCompoundDrawablesWithIntrinsicBounds (0, 0, R.drawable.comment_like_select, 0);
                    String text = view.getText ().toString ();
                    if ("赞".equals (text)) {
                        view.setText (String.valueOf (1));
                    } else {
                        int i = Integer.parseInt (text);
                        view.setText (String.valueOf (++i));
                    }
                    view.setClickable (false);
                }
            }
        });
    }

    class ViewHolder {
        public View rootView;
        public ImageView userIcon;
        public TextView userNick;
        public TextView time;
        public TextView like;
        public TextView comment;

        public ViewHolder (View view) {
            rootView = view;
            userIcon = (ImageView) rootView.findViewById (R.id.civ_user_icon);
            userNick = (TextView) rootView.findViewById (R.id.tv_user_nick);
            time = (TextView) rootView.findViewById (R.id.tv_time);
            like = (TextView) rootView.findViewById (R.id.tv_like);
            comment = (TextView) rootView.findViewById (R.id.tv_content);
        }
    }
}
