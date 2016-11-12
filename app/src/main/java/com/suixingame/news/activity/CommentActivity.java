package com.suixingame.news.activity;
/**
 * ============================================================
 *
 * 版 权 ： (c)
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/11/2  18:33
 *
 * 描 述 ：跟新闻关联的评论
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.suixingame.library.view.PullRefreshView;
import com.suixingame.news.R;
import com.suixingame.news.adapter.CommentAdapter2;
import com.suixingame.news.bean.Comment;
import com.suixingame.news.db.CommentDB;
import com.suixingame.news.util.GlobalParam;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    /**
     * 展示评论内容
     */
    private PullRefreshView mLisView;

    /**
     * 最热评论和最新评论按钮根布局
     */
    private RadioGroup mRgComment;

    /**
     * 最新评论
     */
    private RadioButton mRbCommentNew;
    /**
     * 最热评论
     */
    private RadioButton mRbCommentHot;

    /**
     * 该评论相关新闻的url
     */
    private String mNewsUrl;

    private CommentAdapter2 mCommentAdapter;
    /**
     * 内存中放一份最新评论的最多20条缓存
     */
    private List<Comment> mNewComments;

    /**
     * 内存中放一份最热评论的最多20条缓存
     */
    private List<Comment> mHotComments;
    private ActionBar mActionBar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_comment);
        mActionBar = getSupportActionBar ();
        mRgComment = (RadioGroup) findViewById (R.id.rg_comment);
        mRbCommentNew = (RadioButton) findViewById (R.id.rb_comment_new);
        mRbCommentHot = (RadioButton) findViewById (R.id.rb_comment_hot);

        mLisView = (PullRefreshView) findViewById (R.id.lv_content);
        initData ();
    }

    /**
     * 初始化数据
     */
    private void initData () {
        mActionBar.setTitle ("评论");
        mActionBar.setDisplayHomeAsUpEnabled (true);

        mLisView.setDownPower (0.4f);
        mNewsUrl = getIntent ().getStringExtra ("url");
        CommentDB.getCommentNew (mNewsUrl, 0, mHandler, GlobalParam.commentNew);
        mRgComment.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged (RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_comment_new:
                        if (mNewComments != null) {
                            initListViewData (mNewComments, GlobalParam.commentNew);
                        } else {
                            //获取最新评论数据
                            CommentDB.getCommentNew (mNewsUrl, 0, mHandler, GlobalParam.commentNew);
                        }
                        break;
                    case R.id.rb_comment_hot:
                        if (mHotComments != null) {
                            initListViewData (mHotComments, GlobalParam.commentHot);
                        } else {
                            //获取最热评论数据
                            CommentDB.getCommentFavorite (mNewsUrl, 0, mHandler, GlobalParam.commentHot);
                        }
                        break;
                }
            }
        });
        mRbCommentNew.setChecked (true);
    }

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what) {
                case GlobalParam.commentNew:
                    mNewComments = (List<Comment>) msg.obj;
                    initListViewData (mNewComments, GlobalParam.commentNew);
                    break;
                case GlobalParam.commentHot:
                    mHotComments = (List<Comment>) msg.obj;
                    initListViewData (mHotComments, GlobalParam.commentHot);
                    break;
            }
        }
    };

    private void initListViewData (List<Comment> comments, Integer key) {
        List<Comment> temp = comments;
        if (temp == null) {
            temp = new ArrayList<> ();
        }
        mCommentAdapter = new CommentAdapter2 (this, temp, key);
        mLisView.setAdapter (mCommentAdapter);
        if (temp.size () < 20) {
            //评论少于20条就不用加载更多
            mLisView.setLoadMoreEnable (false);
        }
    }

    private float downX;
    private float downY;

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        switch (ev.getAction ()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX ();
                downY = ev.getRawY ();
                break;
            case MotionEvent.ACTION_UP:
                float lengthX2 = ev.getRawX () - downX;
                float lengthY2 = Math.abs (ev.getRawY () - downY);
                int checkedRadioButtonId = mRgComment.getCheckedRadioButtonId ();
                if (Math.abs (ev.getRawX () - downX) > lengthY2 && lengthX2 > 200) {
                    //往右滑
                    if (checkedRadioButtonId == R.id.rb_comment_new) {
                        finish ();
                    } else {
                        mRbCommentNew.setChecked (true);
                    }
                } else if (Math.abs (ev.getRawX () - downX) > lengthY2 && lengthX2 < -200) {
                    if (checkedRadioButtonId == R.id.rb_comment_new) {
                        mRbCommentHot.setChecked (true);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent (ev);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId () == android.R.id.home) {
            finish ();
        }
        return true;
    }
}
