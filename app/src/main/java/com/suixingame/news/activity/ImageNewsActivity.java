package com.suixingame.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suixingame.news.R;
import com.suixingame.news.adapter.ImageNewsItemAdapter;
import com.suixingame.news.bean.ImageNewsToJson;
import com.suixingame.news.bean.NewsCollection;
import com.suixingame.news.db.NewsCollectionDB;
import com.suixingame.news.listener.AbsOnPageChangeListener;
import com.suixingame.news.util.GlobalParam;
import com.suixingame.news.util.XUtils;
import com.suixingame.news.view.SmartViewPager;

import java.util.List;

import cn.bmob.v3.BmobUser;

public class ImageNewsActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private SmartViewPager mViewPager;

    /**
     * 顶部工具栏
     */
    private Toolbar mToolbar;

    /**
     * 新闻标题
     */
    private TextView mTitle;

    /**
     * 显示当前浏览新闻位置
     */
    private TextView mCurrent;

    /**
     * 当前新闻的内容
     */
    private TextView mContent;

    /**
     * 新闻数据
     */
    private ImageNewsToJson mImageNewsData;

    /**
     * 包裹展示新闻数据的布局
     */
    private LinearLayout mBackground;

    /**
     * 底部图片新闻文字详情布局的最小高度
     */
    private int backgroundMinHeight;

    /**
     * 底部图片新闻文字详情布局的最大高度
     */
    private int backgroundMaxHeight;

    /**
     * 记录用户手指按下时相对于整个屏幕的Y坐标
     */
    private float downY;

    /**
     * 当前登录的用户
     */
    private BmobUser mUser;

    /**
     * 显示更多的菜单
     */
    private PopupWindow mPopupWindow;
    /**
     * 收藏View
     */
    private LinearLayout mNewsLike;

    /**
     * 收藏状态图片
     */
    private ImageView mLikeImage;

    /**
     * 收藏新闻数据
     */
    private NewsCollection mNewsCollection;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_image_news);
        //下面的数据要早点拿到
        mImageNewsData = (ImageNewsToJson) getIntent ().getSerializableExtra (GlobalParam.imageNewsDataTag);
        mUser = BmobUser.getCurrentUser ();
        initView ();
        initData ();
    }

    /**
     * 初始化界面
     */
    private void initView () {
        mToolbar = (Toolbar) findViewById (R.id.toolbar);
        setSupportActionBar (mToolbar);
        mToolbar.setNavigationIcon (R.drawable.icon_back);

        mViewPager = (SmartViewPager) findViewById (R.id.vp_content);
        mBackground = (LinearLayout) findViewById (R.id.ll_background);
        mTitle = (TextView) findViewById (R.id.tv_title);
        mCurrent = (TextView) findViewById (R.id.tv_current);
        mContent = (TextView) findViewById (R.id.tv_content);

        initPopWindow ();
    }

    /**
     * 初始化更多菜单界面
     */
    private void initPopWindow () {
        View menuMore = View.inflate (this, R.layout.view_news_detail_pop_menu_bg_black, null);
        //分享
        LinearLayout newsShare = (LinearLayout) menuMore.findViewById (R.id.ll_share);
        newsShare.setOnClickListener (this);
        //收藏
        mNewsLike = (LinearLayout) menuMore.findViewById (R.id.ll_like);
        //收藏布局左边的图标对象
        mLikeImage = (ImageView) menuMore.findViewById (R.id.iv_image);
        mNewsLike.setTag (false);
        mNewsLike.setOnClickListener (this);
        //报错
        LinearLayout referenceError = (LinearLayout) menuMore.findViewById (R.id.ll_referenceError);
        referenceError.setOnClickListener (this);
        mPopupWindow = new PopupWindow (menuMore, (int) XUtils.dp2px (this, 160), LinearLayout.LayoutParams.WRAP_CONTENT);
        //下面两个方法要一起使用才有效，而且BackgroundDrawable不能为空
        mPopupWindow.setBackgroundDrawable (getResources ().getDrawable (R.drawable.pop_bg_black));
        mPopupWindow.setOutsideTouchable (true);//此方法表示点击popWindow外部，参数为true就dismiss掉

        if (mUser != null && !"".equals (mUser.getObjectId ())) {
            NewsCollectionDB.findNewsCollection (mUser.getObjectId (), mImageNewsData.url, mHandler, GlobalParam.newsCollectionFind);
        }
    }

    /**
     * 初始化数据
     */
    private void initData () {
        backgroundMinHeight = (int) XUtils.dp2px (this, 86);
        backgroundMaxHeight = (int) XUtils.dp2px (this, 160);

        mViewPager.setAdapter (new ImageNewsItemAdapter (this, mImageNewsData.list, mToolbar, mBackground));
        mViewPager.setOnTouchListener (this);
        mViewPager.addOnPageChangeListener (new AbsOnPageChangeListener () {
            @Override
            public void onPageSelected (int position) {
                //设置新闻标题
                mTitle.setText (mImageNewsData.info.setname);
                //设置当前浏览图片的位置
                mCurrent.setText ((position + 1) + "/" + mImageNewsData.info.imgsum);
                //首行缩进两个字符"  "
                mContent.setText (mImageNewsData.list.get (position).title);
                //重置mBackground到最小值
                setBackgroundHeight (-1000);

            }
        });
        mViewPager.setCurrentItem (0);

        mBackground.setOnTouchListener (this);
    }



    /**
     * 设置包裹展示新闻数据布局的高度
     */
    private void setBackgroundHeight (int length) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBackground.getLayoutParams ();
        params.height = params.height + length;
        if (params.height < backgroundMinHeight) {
            params.height = backgroundMinHeight;
        } else if (params.height > backgroundMaxHeight) {
            params.height = backgroundMaxHeight;
        }
        mBackground.setLayoutParams (params);
    }

    @Override
    public boolean onTouch (View v, MotionEvent event) {
        //是否处理touch事件
        boolean isProcessTouch = false;
        switch (v.getId ()) {
            case R.id.ll_background:
                isProcessTouch = processNewsContentTouch (event);
                break;
        }
        return isProcessTouch;
    }

    /**
     * 处理包裹图片新闻信息的布局的touch事件
     */
    private boolean processNewsContentTouch (MotionEvent event) {
        switch (event.getAction ()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY ();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getRawY ();
                setBackgroundHeight ((int) (downY - currentY));
                downY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.image_news_activity_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            //点击ActionBar上的返回按钮
            case android.R.id.home:
                finish ();
                break;

            //点击更多菜单按钮
            case R.id.menu_more:
                if (mPopupWindow != null && !mPopupWindow.isShowing ()) {
                    mPopupWindow.showAsDropDown (mToolbar, mToolbar.getWidth (), (int) -XUtils.dp2px (this, 48));
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()) {
            //分享
            case R.id.ll_share:
                Toast.makeText (this, "分享", Toast.LENGTH_SHORT).show ();
                break;

            //收藏
            case R.id.ll_like:
                mPopupWindow.dismiss ();
                boolean isCollect = (boolean) mNewsLike.getTag ();
                if (isCollect && mNewsCollection != null) {
                    //已收藏，取消收藏
                    NewsCollectionDB.deleteNewsCollection (mNewsCollection.getObjectId (), mHandler, GlobalParam.newsCollectionDelete);
                } else {
                    //没收藏
                    if (mUser == null || "".equals (mUser.getObjectId ())) {
                        //没登录
                        startActivity (new Intent (this, LoginActivity.class));
                    } else {
                        //已登录
                        NewsCollectionDB.addNewsCollection (mUser.getObjectId (), mImageNewsData.url, 1, mHandler, GlobalParam.newsCollectionAdd);
                    }
                }
                break;

            //我要报错
            case R.id.ll_referenceError:
                Toast.makeText (this, "我要报错", Toast.LENGTH_SHORT).show ();
                break;
        }

    }

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what){
                //新闻收藏数据获取回调
                case GlobalParam.newsCollectionFind:
                    List<NewsCollection> list = (List<NewsCollection>) msg.obj;
                    if (list != null && list.size () > 0){
                        mNewsCollection = list.get (0);
                        mLikeImage.setImageDrawable (getResources ().getDrawable (R.drawable.icon_news_like_selected));
                        mNewsLike.setTag (true);
                    }
                    break;

                //添加收藏
                case GlobalParam.newsCollectionAdd:
                    boolean addSuccess = (boolean) msg.obj;
                    if (addSuccess){
                        XUtils.toastShort (ImageNewsActivity.this,"收藏成功!");
                        mLikeImage.setImageDrawable (getResources ().getDrawable (R.drawable.icon_news_like_selected));
                        mNewsLike.setTag (true);
                        mNewsCollection = new NewsCollection (mUser.getObjectId (),mImageNewsData.url,1);
                    }else {
                        XUtils.toastShort (ImageNewsActivity.this,"请检查网络");
                    }
                    break;

                //取消收藏
                case GlobalParam.newsCollectionDelete:
                    boolean deleteSuccess = (boolean) msg.obj;
                    if (deleteSuccess){
                        XUtils.toastShort (ImageNewsActivity.this,"已取消收藏!");
                        mLikeImage.setImageDrawable (getResources ().getDrawable (R.drawable.icon_news_like_normal));
                        mNewsLike.setTag (false);
                        mNewsCollection = null;
                    }else {
                        XUtils.toastShort (ImageNewsActivity.this,"请检查网络!");
                    }
                    break;
            }
        }
    };

}
