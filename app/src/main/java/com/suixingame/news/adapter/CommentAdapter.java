package com.suixingame.news.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * 创建日期 ： 2016/10/28 18:32
 *
 * 描 述 ：适配新闻详情界面下的评论，展示最新的最多十条数据
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class CommentAdapter extends BaseAdapter {
    private Context mContext;
    private List<Comment> mComments;

    private static final int itemType1 = 0;
    private static final int itemType2 = 1;
    private static final int itemType3 = 2;


    public CommentAdapter (Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }


    @Override
    public int getCount () {
        if (mComments == null) {
            return 0;
        }
        return mComments.size () + 2;
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
    public int getViewTypeCount () {
        return 3;
    }

    @Override
    public int getItemViewType (int position) {
        if (position == 0) {
            return itemType1;
        } else if (position == getCount () - 1) {
            return itemType2;
        } else {
            return itemType3;
        }
    }

    @Override
    public View getView (final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType (position);
        ViewHolder viewHolder;
        if (convertView == null) {
            if (itemViewType == itemType1) {
                convertView = View.inflate (mContext, R.layout.view_comment_header, null);
            } else if (itemViewType == itemType2) {
                TextView textView = new TextView (mContext);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) XUtils.dp2px (mContext, 40));
                textView.setLayoutParams (params);
                textView.setTextSize (16);
                textView.setGravity (Gravity.CENTER);

                convertView = textView;
            } else {
                convertView = View.inflate (mContext, R.layout.view_listview_comment, null);
                viewHolder = new ViewHolder (convertView);
                convertView.setTag (viewHolder);
            }
        }
        if (itemViewType == itemType3) {
            //评论数据展示
            viewHolder = (ViewHolder) convertView.getTag ();
            final Comment comment = mComments.get (position - 1);
            setUserNick (viewHolder.userNick, comment.getUserId ());
            String time = comment.getCreatedAt ();
            int beginIndex = time.indexOf ("-") + 1;
            int endIndex = time.lastIndexOf (":");
            time = time.substring (beginIndex, endIndex);
            viewHolder.time.setText (time);
            viewHolder.comment.setText (comment.getContent ());
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
        } else if (itemViewType == itemType2) {
            TextView view = (TextView) convertView;
            if (getCount () < 12) {
                //界面没有十条评论，12是因为加上评论区域的头和底部View的数据量
                if (getCount () == 2) {
                    //表示该新闻没有一条评论
                    view.setText ("暂无最新评论");
                } else {
                    //该新闻评论数量大于0小于10
                    view.setText ("--到底啦--");
                }
                view.setBackgroundColor (Color.WHITE);
                view.setOnClickListener (null);
            } else {
                //该新闻有超过十条评论
                view.setText ("点击查看更多评论");
                view.setBackgroundResource (R.drawable.list_item_select_light);
                view.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {
                        Toast.makeText (mContext, "点击查看更多评论", Toast.LENGTH_SHORT).show ();
                    }
                });
            }
        }
        return convertView;
    }

    public void setComments (List<Comment> comments) {
        mComments = comments;
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
