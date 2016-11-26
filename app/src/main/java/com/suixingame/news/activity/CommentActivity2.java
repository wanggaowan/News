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
 * 创建日期 ： 2016/11/2  18:35
 *
 * 描 述 ：跟用户关联的评论
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suixingame.library.view.PullRefreshView;
import com.suixingame.news.R;
import com.suixingame.news.adapter.CommentAdapter3;
import com.suixingame.news.bean.Comment;
import com.suixingame.news.bean.NewsData;
import com.suixingame.news.db.CommentDB;
import com.suixingame.news.util.GlobalParam;

import java.util.List;

import cn.bmob.v3.BmobUser;

public class CommentActivity2 extends AppCompatActivity implements View.OnClickListener{

    /**
     * 刷新标志，用于刷新和第一次进入该Activity
     */
    private final int onRefreshing = 0;

    /**
     * 加载更多标志
     */
    private final int onLoadMore = 1;

    /**
     * 重置PullRefreshView的状态
     */
    private final int resetPullRefreshView = 2;

    /**
     * 刷新成功或者第一次进入拿到数据
     */
    private final int onRefreshingSuccess = 3;

    private BmobUser mUser;
    private ActionBar mActionBar;


    /**
     * 展示与用户关联的评论内容
     */
    private PullRefreshView mPullRefreshView;
    private CommentAdapter3 mAdapter;

    //错误信息布局
    /**
     * 错误信息根布局
     */
    private RelativeLayout mRlErrorRoot;

    /**
     * 显示错误信息
     */
    private TextView mTvErrorMessage;

    /**
     * 显示正在重新加载
     */
    private ProgressBar mPbErrorMessage;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_comment2);
        mUser = BmobUser.getCurrentUser ();
        initView ();
        CommentDB.getCommentWithUser (mUser.getObjectId (),0,mHandler,onRefreshing);
    }

    private void initView(){
        mActionBar = getSupportActionBar ();
        mActionBar.setTitle ("我的评论");
        mActionBar.setDisplayHomeAsUpEnabled (true);

        mRlErrorRoot = (RelativeLayout) findViewById (R.id.rl_error_message_root);
        mTvErrorMessage = (TextView) findViewById (R.id.tv_error_message);
        mPbErrorMessage = (ProgressBar) findViewById (R.id.pb_error_message);
        mRlErrorRoot.setOnClickListener (this);


        mPullRefreshView = (PullRefreshView) findViewById (R.id.lv_content_comment);
        setPullRefreshView();
    }

    /**
     * 设置PullRefreshView的一些监听器
     */
    private void setPullRefreshView () {
        mPullRefreshView.setOnRefreshListener (new PullRefreshView.onRefreshListener () {
            @Override
            public void onRefreshing () {
                CommentDB.getCommentWithUser (mUser.getObjectId (),0,mHandler,onRefreshing);
                //1s后重置PullRefreshView的状态，无论是否获取到数据
                mHandler.sendEmptyMessageDelayed (resetPullRefreshView,1000);
            }

            @Override
            public void onLoadMore () {
                CommentDB.getCommentWithUser (mUser.getObjectId (),mAdapter.getCount (),mHandler,onLoadMore);
                //1s后重置PullRefreshView的状态，无论是否获取到数据
                mHandler.sendEmptyMessageDelayed (resetPullRefreshView,1000);
            }
        });

        mPullRefreshView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                //展开评论文章详情页
                Comment comment = mAdapter.getItem (position - 1);//注意position转化，因为该ListView有一个header
                NewsData newsData =  new NewsData ();
                newsData.setUrl (comment.getUrl ());
                newsData.setTitle (comment.getNewsTitle ());
                Intent intent = new Intent (CommentActivity2.this, TextNewsActivity.class);
                intent.putExtra (GlobalParam.NewsDataTag, newsData);
                startActivity (intent);
            }
        });
    }

    private Handler mHandler = new Handler (){
        @Override
        public void handleMessage (Message msg) {
            List<Comment> comments = null;
            switch (msg.what){
                //重新拿到评论数据,第一次进入和刷新时回调
                case onRefreshing:
                    Message message = new Message ();
                    message.what = onRefreshingSuccess;
                    comments = (List<Comment>) msg.obj;
                    if (comments != null && comments.size () > 0){
                        if (comments.size () < 20){
                            //说明没有更多数据
                            mPullRefreshView.setLoadMoreEnable (false);
                        }
                        mAdapter = new CommentAdapter3 (CommentActivity2.this, comments);
                        mPullRefreshView.setAdapter (mAdapter);
                        message.obj = true;
                        mHandler.sendMessageDelayed (message,1000);
                    }else if (mAdapter==null){
                        message.obj = false;
                        mHandler.sendMessageDelayed (message,1000);
                    }
                    break;

                //加载更多评论数据
                case onLoadMore:
                    comments = (List<Comment>) msg.obj;
                    if (comments !=null){
                        if (comments.size () < 20){
                            //说明没有更多数据
                            mPullRefreshView.setLoadMoreEnable (false);
                        }
                        if (comments.size () > 0){
                            //拿到数据
                            mAdapter.addComments ((List<Comment>) msg.obj);
                            mAdapter.notifyDataSetChanged ();
                        }
                    }
                    break;

                case resetPullRefreshView:
                    mPullRefreshView.resetRefreshState ();
                    break;

                case onRefreshingSuccess:
                    if ((boolean)msg.obj){
                        mTvErrorMessage.setVisibility (View.INVISIBLE);
                        mPbErrorMessage.setVisibility (View.VISIBLE);
                        mRlErrorRoot.setVisibility (View.GONE);
                        mPullRefreshView.setVisibility (View.VISIBLE);
                    }else {
                        mRlErrorRoot.setVisibility (View.VISIBLE);
                        mTvErrorMessage.setVisibility (View.VISIBLE);
                        mPbErrorMessage.setVisibility (View.INVISIBLE);
                        mPullRefreshView.setVisibility (View.INVISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId () == android.R.id.home){
            finish ();
        }
        return true;
    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()){
            case R.id.rl_error_message_root:
                if (mTvErrorMessage.getVisibility () == View.VISIBLE && //
                        mPbErrorMessage.getVisibility () == View.INVISIBLE){
                    mTvErrorMessage.setVisibility (View.INVISIBLE);
                    mPbErrorMessage.setVisibility (View.VISIBLE);
                    CommentDB.getCommentWithUser (mUser.getObjectId (),0,mHandler,onRefreshing);
                }
                break;
        }
    }
}
