package com.suixingame.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.suixingame.news.activity.NewsColumnActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        mHandler.sendEmptyMessageDelayed (0, 1000);
    }

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            showMainView ();
        }
    };

    /**
     * 展示主界面
     */
    private void showMainView () {
        startActivity (new Intent (SplashActivity.this, NewsColumnActivity.class));
        finish ();
        overridePendingTransition (R.anim.fade_in, R.anim.fade_out);
    }


}
