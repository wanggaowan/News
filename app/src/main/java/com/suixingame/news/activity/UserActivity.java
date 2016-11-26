package com.suixingame.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suixingame.news.R;

import cn.bmob.v3.BmobUser;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 展示用户名
     */
    private TextView mUserName;

    /**
     * 用户收藏
     */
    private LinearLayout mUserLike;

    /**
     * 用户评论
     */
    private LinearLayout mUserComment;

    /**
     * 退出当前账户
     */
    private Button mLoginOut;

    /**
     * 当前用户数据
     */
    private BmobUser mBmobUser;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_user);
        initView ();
        initData();

    }
    private void initView () {
        ActionBar actionBar = getSupportActionBar ();
        actionBar.setTitle ("我的");
        actionBar.setDisplayHomeAsUpEnabled (true);

        mUserName = (TextView) findViewById (R.id.tv_username);
        mUserLike = (LinearLayout) findViewById (R.id.ll_news_like);
        mUserComment = (LinearLayout) findViewById (R.id.ll_comment);
        mLoginOut = (Button) findViewById (R.id.bt_login_out);

        mUserLike.setOnClickListener (this);
        mUserComment.setOnClickListener (this);
        mLoginOut.setOnClickListener (this);
    }

    private void initData () {
        mBmobUser = BmobUser.getCurrentUser ();
        if (mBmobUser == null){
            startActivity (new Intent (this,LoginActivity.class));
            finish ();
            return;
        }
        mUserName.setText (mBmobUser.getUsername ());
    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()) {
            case R.id.ll_news_like:
                break;
            case R.id.ll_comment:
                startActivity (new Intent (this,CommentActivity2.class));
                break;
            case R.id.bt_login_out:
                BmobUser.logOut ();
                startActivity (new Intent (this,LoginActivity.class));
                finish ();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId () == android.R.id.home){
            finish ();
        }
        return true;
    }
}
