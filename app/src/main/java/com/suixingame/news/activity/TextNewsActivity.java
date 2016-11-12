package com.suixingame.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suixingame.library.view.PullRefreshView;
import com.suixingame.news.R;
import com.suixingame.news.adapter.CommentAdapter;
import com.suixingame.news.bean.Comment;
import com.suixingame.news.bean.NewsCollection;
import com.suixingame.news.bean.NewsData;
import com.suixingame.news.db.CommentDB;
import com.suixingame.news.db.NewsCollectionDB;
import com.suixingame.news.db.NewsDataDB;
import com.suixingame.news.js.JsgoJava;
import com.suixingame.news.util.GlobalParam;
import com.suixingame.news.util.XUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;

public class TextNewsActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private PullRefreshView mListView;
    private WebView mWebView;

    private ActionBar mActionBar;
    /**
     * 返回上一个界面
     */
    private ImageView mBack;

    /**
     * 弹出更多菜单项
     */
    private ImageView mMenuMore;

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
     * 评论内容
     */
    private EditText mComment;
    /**
     * 记录评论EditText初始时相对于屏幕的X，Y坐标
     */
    private int[] mCommentLoaction = new int[2];

    /**
     * 该新闻的数据
     */
    private NewsData mNewsData;
    /**
     * 评论数量
     */
    private TextView mCommentNumber;
    /**
     * 评论发送按钮
     */
    private ImageView mCommentSend;
    private CommentAdapter mCommentAdapter;

    /**
     * 新闻底部包裹评论EditText的跟布局，当新闻加载好才显示
     */
    private CardView mCvBottom;

    /**
     * comment评论数据标志
     */
    private final int commentData = 0;

    /**
     * 当新闻数据没有获取到的时候显示错误信息的根布局
     */
    private RelativeLayout mErrorMessageRoot;

    /**
     * 显示错误消息
     */
    private TextView mTvErrorMessage;

    /**
     * 显示重新加载数据动画
     */
    private ProgressBar mPbErrorMessage;

    /**
     * 记录当前用户收藏新闻后需要记录的数据
     */
    private NewsCollection mNewsCollection;

    private BmobUser mUser;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_text_news);
        //mNewsData要较早拿到
        mNewsData = (NewsData) getIntent ().getSerializableExtra (GlobalParam.NewsDataTag);
        mUser = BmobUser.getCurrentUser ();
        initView ();
        initDta ();
    }

    private void initView () {
        mActionBar = getSupportActionBar ();

        //初始化ActionBar自定义View
        View view = View.inflate (this, R.layout.view_actionbar_news_detail, null);
        mBack = (ImageView) view.findViewById (R.id.iv_back);
        mBack.setOnClickListener (this);
        mMenuMore = (ImageView) view.findViewById (R.id.iv_menu_more);
        mMenuMore.setOnClickListener (this);
        mActionBar.setCustomView (view);
        //只展示自定义视图
        mActionBar.setDisplayOptions (ActionBar.DISPLAY_SHOW_CUSTOM);
        //设置ActionBar的海拔，这样就没有阴影了
        mActionBar.setElevation (0);

        mListView = (PullRefreshView) findViewById (R.id.lv_content_detail);
        //数据获取错误时的组件
        mErrorMessageRoot = (RelativeLayout) findViewById (R.id.rl_error_message_root);
        mTvErrorMessage = (TextView) findViewById (R.id.tv_error_message);
        mPbErrorMessage = (ProgressBar) findViewById (R.id.pb_error_message);


        mCvBottom = (CardView) findViewById (R.id.cv_bottom);
        mComment = (EditText) findViewById (R.id.et_comment);
        mCommentNumber = (TextView) findViewById (R.id.tv_comment_number);
        mCommentSend = (ImageView) findViewById (R.id.iv_comment_send);
        initPopWindow ();
    }


    /**
     * 初始化界面数据
     */
    private void initDta () {
        mActionBar.setTitle ("");
        //获取mComment初始时的x，y坐标
        mComment.getViewTreeObserver ().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener () {
            @Override
            public void onGlobalLayout () {
                mComment.getLocationOnScreen (mCommentLoaction);
                mComment.getViewTreeObserver ().removeGlobalOnLayoutListener (this);
            }
        });
        //设置EditText对应软键盘上回车键按下的监听
        mComment.addTextChangedListener (this);
        mCommentSend.setOnClickListener (this);
        mCommentNumber.setOnClickListener (this);
        mErrorMessageRoot.setOnClickListener (this);

        mListView.setOnRefreshListener (new PullRefreshView.onRefreshListener () {
            @Override
            public void onRefreshing () {
                NewsDataDB.getNewsDataDetail (mNewsData.getUrl (), mHandler, GlobalParam.listViewPullRefresh);
            }
            @Override
            public void onLoadMore () {

            }
        });

        mListView.setOnScrollListener (new PullRefreshView.OnScrollListener () {
            @Override
            public void onScrollStateChanged (AbsListView view, int scrollState) {
                if (inputMethodIsVisible ()){
                    closeInputMethod ();
                }
            }
            @Override
            public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        //获取新闻数据
        NewsDataDB.getNewsDataDetail (mNewsData.getUrl (), mHandler, GlobalParam.NewsData);
    }

    private Handler mHandler = new Handler () {
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what) {
                //新闻数据获取回调
                case GlobalParam.NewsData:
                    List<NewsData> newsDatas = (List<NewsData>) msg.obj;
                    if (newsDatas != null && newsDatas.size () > 0) {
                        initListViewData (newsDatas.get (0));
                        mHandler.sendEmptyMessageDelayed (GlobalParam.NewsDataIsReady,500);
                    }else {
                        XUtils.toastShort (TextNewsActivity.this,"网络不给力");
                        mTvErrorMessage.setVisibility (View.VISIBLE);
                        mPbErrorMessage.setVisibility (View.INVISIBLE);
                    }
                    break;

                //新闻数据填充完毕
                case GlobalParam.NewsDataIsReady:
                    mListView.setVisibility (View.VISIBLE);
                    initErrorStatus ();
                    break;

                //评论发表回调
                case GlobalParam.commentAdd:
                    boolean success = (boolean) msg.obj;
                    if (success) {
                        Toast.makeText (TextNewsActivity.this, "评论发表成功!", Toast.LENGTH_SHORT).show ();
                    } else {
                        Toast.makeText (TextNewsActivity.this, "评论发表失败!", Toast.LENGTH_SHORT).show ();
                    }
                    break;

                //获取最新的评论数据
                case GlobalParam.commentNew:
                    List<Comment> comments = (List<Comment>) msg.obj;
                    if (comments == null) {
                        comments = new ArrayList<> ();
                    }
                    mCommentAdapter.setComments (comments);
                    mCommentAdapter.notifyDataSetChanged ();
                    break;

                //界面刷新回调
                case GlobalParam.listViewPullRefresh:
                    List<NewsData> newsDatas2 = (List<NewsData>) msg.obj;
                    if (newsDatas2 != null && newsDatas2.size () > 0) {
                        mWebView.addJavascriptInterface (new JsgoJava (newsDatas2.get (0).getContent ()), "JsgoJava");
                        mWebView.loadUrl ("file:///android_asset/newsContent.html");
                    }else {
                        XUtils.toastShort (TextNewsActivity.this,"网络不给力");
                    }
                    mListView.resetRefreshState ();
                    break;

                //获取当前界面需要展示的最新十条评论数据
                case commentData:
                    CommentDB.getCommentJustTen (mNewsData.getUrl (), mHandler, GlobalParam.commentNew);
                    break;

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
                        XUtils.toastShort (TextNewsActivity.this,"收藏成功!");
                        mLikeImage.setImageDrawable (getResources ().getDrawable (R.drawable.icon_news_like_selected));
                        mNewsLike.setTag (true);
                        mNewsCollection = new NewsCollection (mUser.getObjectId (),mNewsData.getUrl (),0);
                    }else {
                        XUtils.toastShort (TextNewsActivity.this,"请检查网络");
                    }
                    break;

                //取消收藏
                case GlobalParam.newsCollectionDelete:
                    boolean deleteSuccess = (boolean) msg.obj;
                    if (deleteSuccess){
                        XUtils.toastShort (TextNewsActivity.this,"已取消收藏!");
                        mLikeImage.setImageDrawable (getResources ().getDrawable (R.drawable.icon_news_like_normal));
                        mNewsLike.setTag (false);
                        mNewsCollection = null;
                    }else {
                        XUtils.toastShort (TextNewsActivity.this,"请检查网络!");
                    }
                    break;
            }
        }
    };

    /**
     * 将数据展示到ListView
     */
    private void initListViewData (NewsData newsData) {
        View headerView = View.inflate (this, R.layout.view_news_detail_listview_header, null);
        mWebView = (WebView) headerView.findViewById (R.id.wv_news);

        WebSettings s = mWebView.getSettings ();
        s.setLayoutAlgorithm (WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//设置自动排版布局
        s.setUseWideViewPort (true);//可任意比例缩放
        s.setLoadWithOverviewMode (true);//设置webview加载的页面的模式。
        s.setJavaScriptEnabled (true);
        s.setJavaScriptCanOpenWindowsAutomatically (true);
        s.setDomStorageEnabled (true);
        s.setSupportZoom (true);
        s.setAllowFileAccess (true); //允许访问文件
        mWebView.requestFocus ();

        //设置滚动条覆盖在内容上，不占单独的宽度
        mWebView.setScrollBarStyle (View.SCROLLBARS_INSIDE_OVERLAY);

        mWebView.setWebViewClient (new WebViewClient () {
            @Override
            public void onPageFinished (WebView view, String url) {
                //这里面做初始化和刷新相关界面的操作，比如新闻刷就意味着评论跟着刷新
                //设置要等WebView完全显示才加载评论，所以要发延迟消息
                mHandler.sendEmptyMessageDelayed (commentData, 500);
                setCommentNumber (mNewsData.getUrl ());
                mCvBottom.setVisibility (View.VISIBLE);
            }
        });
        mWebView.setWebChromeClient (new WebChromeClient ());

        mWebView.addJavascriptInterface (new JsgoJava (newsData.getContent ()), "JsgoJava");
        mWebView.loadUrl ("file:///android_asset/newsContent.html");

        mListView.addHeaderView (headerView);
        mCommentAdapter = new CommentAdapter (this, null);
        mListView.setAdapter (mCommentAdapter);
        mListView.setLoadMoreEnable (false);
    }

    /**
     * 初始化更多菜单界面
     */
    private void initPopWindow () {
        View menuMore = View.inflate (this, R.layout.view_news_detail_pop_menu_bg_white, null);
        //分享
        LinearLayout newsShare = (LinearLayout) menuMore.findViewById (R.id.ll_share);
        newsShare.setOnClickListener (this);
        //收藏
        mNewsLike = (LinearLayout) menuMore.findViewById (R.id.ll_like);
        mLikeImage = (ImageView) menuMore.findViewById (R.id.iv_image);
        mNewsLike.setTag (false);
        mNewsLike.setOnClickListener (this);
        //报错
        LinearLayout referenceError = (LinearLayout) menuMore.findViewById (R.id.ll_referenceError);
        referenceError.setOnClickListener (this);
        mPopupWindow = new PopupWindow (menuMore, (int) XUtils.dp2px (this, 160), LinearLayout.LayoutParams.WRAP_CONTENT);
        //下面两个方法要一起使用才有效，而且BackgroundDrawable不能为空
        mPopupWindow.setBackgroundDrawable (getResources ().getDrawable (R.drawable.pop_bg_white));
        mPopupWindow.setOutsideTouchable (true);//此方法表示点击popWindow外部，参数为true就dismiss掉

        if (mUser != null && !"".equals (mUser.getObjectId ())){
            NewsCollectionDB.findNewsCollection (mUser.getObjectId (),mNewsData.getUrl (),mHandler,GlobalParam.newsCollectionFind);
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy ();
        //重新加载一次网页，用于停止网页中的视频和声音，因为如果不这样，当该Activity Finish后声音还存在
        if (mWebView != null) {
            mWebView.reload ();
            mWebView.getSettings ().setJavaScriptEnabled (false);
        }
    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()) {
            //返回上个界面
            case R.id.iv_back:
                finish ();
                break;
            case R.id.iv_menu_more:
                if (mPopupWindow != null && !mPopupWindow.isShowing ()) {
                    mPopupWindow.showAsDropDown (mMenuMore, 0, (int) -XUtils.dp2px (this, 48));
                }
                break;
            //分享
            case R.id.ll_share:
                Toast.makeText (this, "分享", Toast.LENGTH_SHORT).show ();
                break;

            //收藏
            case R.id.ll_like:
                mPopupWindow.dismiss ();
                boolean isCollect = (boolean) mNewsLike.getTag ();
                if (isCollect && mNewsCollection!=null){
                    //已收藏，取消收藏
                    NewsCollectionDB.deleteNewsCollection (mNewsCollection.getObjectId (),mHandler,GlobalParam.newsCollectionDelete);
                }else {
                    //没收藏
                    if (mUser == null || "".equals (mUser.getObjectId ())){
                        //没登录
                        startActivity (new Intent (this,LoginActivity.class));
                    }else {
                        //已登录
                        NewsCollectionDB.addNewsCollection (mUser.getObjectId (),mNewsData.getUrl (),0,mHandler,GlobalParam.newsCollectionAdd);
                    }
                }
                break;

            //我要报错
            case R.id.ll_referenceError:
                Toast.makeText (this, "我要报错", Toast.LENGTH_SHORT).show ();
                break;

            //评论发送
            case R.id.iv_comment_send:
                if (BmobUser.getCurrentUser () != null) {
                    //当前以后用户登录
                    sendComment ();
                } else {
                    //用户没有登录
                    startActivity (new Intent (this, LoginActivity.class));
                }
                break;

            //评论数量
            case R.id.tv_comment_number:
                //跳到评论展示区域
                mListView.setSelection (2);
                break;

            //重新加载界面
            case R.id.rl_error_message_root:
                NewsDataDB.getNewsDataDetail (mNewsData.getUrl (), mHandler, GlobalParam.NewsData);
                mPbErrorMessage.setVisibility (View.VISIBLE);
                mTvErrorMessage.setVisibility (View.INVISIBLE);
                break;
        }
    }

    /**
     * 可接收MotionEvent.ACTION_DOWN
     */
    @Override
    public void onUserInteraction () {
        super.onUserInteraction ();
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
                if (Math.abs (ev.getRawX () - downX) > lengthY2 && lengthX2 > 300 && !inputMethodIsVisible ()) {
                    finish ();
                } else if (Math.abs (ev.getRawX () - downX) > lengthY2 && lengthX2 < -300 && !inputMethodIsVisible ()) {
                    Intent intent = new Intent (TextNewsActivity.this, CommentActivity.class);
                    intent.putExtra ("url", mNewsData.getUrl ());
                    startActivity (intent);
                }
                break;
        }
        return super.dispatchTouchEvent (ev);
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mWebView != null && mWebView.canGoBack ()) {
                    //网页可返回
                    mWebView.goBack ();
                } else if (inputMethodIsVisible ()) {
                    closeInputMethod ();
                } else {
                    finish ();
                }
                return true;
            default:
                return super.onKeyUp (keyCode, event);
        }
    }

    @Override
    public void beforeTextChanged (CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged (CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged (Editable s) {
        if (!"".equals (mComment.getText ().toString ().trim ())) {
            mComment.setCompoundDrawablesWithIntrinsicBounds (0, 0, 0, 0);
            mCommentNumber.setVisibility (View.GONE);
            mCommentSend.setVisibility (View.VISIBLE);
        } else {
            mComment.setCompoundDrawablesWithIntrinsicBounds (R.drawable.comment_pen, 0, 0, 0);
            mCommentNumber.setVisibility (View.VISIBLE);
            mCommentSend.setVisibility (View.GONE);
        }
    }

    /**
     * 发表评论
     */
    private void sendComment () {
        Comment comment = new Comment ();
        comment.setUrl (mNewsData.getUrl ());
        comment.setUserId (BmobUser.getCurrentUser ().getObjectId ());
        comment.setContent (mComment.getText ().toString ().trim ());
        comment.setNewsTitle (mNewsData.getTitle ());
        comment.setLikeNumber (0);
        CommentDB.addComment (comment, mHandler, GlobalParam.commentAdd);

        //清空输入内容
        mComment.setText ("");
        //关闭输入法
        if (inputMethodIsVisible ()) {
            closeInputMethod ();
        }
    }

    /**
     * 初始化错误信息显示布局的显示状态
     */
    private void initErrorStatus(){
        mTvErrorMessage.setVisibility (View.VISIBLE);
        mPbErrorMessage.setVisibility (View.INVISIBLE);
        mErrorMessageRoot.setVisibility (View.GONE);
    }

    /**
     * 判断输入法是否可见
     *
     * @return true表示可见
     */
    private boolean inputMethodIsVisible () {
        if (mErrorMessageRoot.getVisibility () == View.VISIBLE || mErrorMessageRoot.getVisibility () == View.INVISIBLE){
            return false;
        }else {
            int[] temp = new int[2];
            mComment.getLocationOnScreen (temp);
            if (temp[1] != mCommentLoaction[1]) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 关闭输入法
     */
    private void closeInputMethod(){
        //判断输入法界面是否显示
        InputMethodManager manager = (InputMethodManager) getSystemService (INPUT_METHOD_SERVICE);
        //强制关闭输入法
        manager.hideSoftInputFromWindow (mComment.getWindowToken (),0);
    }

    /**
     * 设置评论数量
     */
    private void setCommentNumber (String url) {
        BmobQuery<Comment> query = new BmobQuery<> ();
        query.addWhereEqualTo ("url", url);
        boolean b = query.hasCachedResult (Comment.class);
        if (b) {
            query.setCachePolicy (BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        } else {
            query.setCachePolicy (BmobQuery.CachePolicy.NETWORK_ONLY);
        }
        query.count (Comment.class, new CountListener () {
            @Override
            public void done (Integer integer, BmobException e) {
                if (e != null) {
                    mCommentNumber.setText ("0");
                } else {
                    mCommentNumber.setText (integer.toString ());
                }
            }
        });
    }


}
