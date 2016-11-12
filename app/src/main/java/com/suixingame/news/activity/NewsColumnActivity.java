package com.suixingame.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.suixingame.library.view.PullRefreshView;
import com.suixingame.news.R;
import com.suixingame.news.adapter.HomeViewPagerAdapter;
import com.suixingame.news.adapter.ImageNewsHeaderAdapter;
import com.suixingame.news.adapter.ListViewAdapter;
import com.suixingame.news.bean.ImageNews;
import com.suixingame.news.bean.ImageNewsToJson;
import com.suixingame.news.bean.NewsData;
import com.suixingame.news.bean.Topic;
import com.suixingame.news.db.ImageNewsDB;
import com.suixingame.news.db.NewsDataDB;
import com.suixingame.news.db.NewsImageDB;
import com.suixingame.news.listener.AbsOnPageChangeListener;
import com.suixingame.news.util.GlobalParam;
import com.suixingame.news.util.XUtils;
import com.suixingame.news.view.SmartViewPager;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;


public class NewsColumnActivity extends AppCompatActivity {
    /**
     * 专栏导航条
     */
    private TabPageIndicator mIndicator;

    /**
     * 展示专栏ViewPager
     */
    private SmartViewPager mViewPager;
    /**
     * 专栏ViewPager适配器
     */
    private HomeViewPagerAdapter mHomeViewPagerAdapter;


    private ActionBar mActionBar;

    /**
     * 当前正在显示的ListView
     */
    private PullRefreshView mListView;
    /**
     * 显示当前内容的ListView的适配器
     */
    private ListViewAdapter mListViewAdapter;


    /**
     * 显示轮播条当前图片位置及总图片数量的点
     */
    private LinearLayout mPoints;

    /**
     * 当前选中轮播图片所处位置的点
     */
    private ImageView mPointSelect;

    /**
     * 两个轮播点之间的距离
     */
    private int point2pointLength = 0;

    /**
     * 用于展示图片新闻
     */
    private SmartViewPager mImageNewsViewPager;

    /**
     * 显示图片新闻的标题
     */
    private TextView mImageNewsTitle;

    /**
     * 记录用户上一次按下返回键的时间
     */
    private long lastClickTime;

    /**
     * 记录listView相对于当前滑动状态之前的状态，默认静止
     */
    private int preListViewScrollState = SCROLL_STATE_IDLE;
    /**
     * 相对于listView当前可见区域第一个Item之前的item位置，默认位置0
     */
    private int preFirstVisibleItem = 0;

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


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        Bmob.initialize (this, "28a02af8a997dc684014d173f98702bb");
        initView ();
        initData ();

    }

    /**
     * 初始化界面
     */
    private void initView () {
        mActionBar = getSupportActionBar ();
        mViewPager = (SmartViewPager) findViewById (R.id.vp_content);
        mIndicator = (TabPageIndicator) findViewById (R.id.vpi_indicator);
    }

    /**
     * 初始化数据
     */
    private void initData () {
        mActionBar.setTitle ("新闻");
        try {
            List<Topic> topics = GlobalParam.getTopics (this);
            initHomeViewPager (topics);
        } catch (JSONException e) {
            Log.e (GlobalParam.App_Log, e.getMessage ());
            finish ();
        }
    }

    /**
     * 准备界面初始ViewPager
     */
    private void initHomeViewPager (final List<Topic> topics) {
        /*该方法必须在mViewPager.setAdapter (mHomeViewPagerAdapter);之前调用，否则不执行onPageSelected
        因为在Adapter调用了mIndicator.onPageSelected (0);
        */
        mIndicator.setOnPageChangeListener (new AbsOnPageChangeListener () {
            @Override
            public void onPageSelected (int position) {
                initListView (position, topics);
            }
        });


        mHomeViewPagerAdapter = new HomeViewPagerAdapter (NewsColumnActivity.this, topics, mIndicator);
        mViewPager.setAdapter (mHomeViewPagerAdapter);
        //设置保留太多界面，内存消耗会很高，设置两个就还好，而且使与当前item相邻的左右两个Item都没注销
        mViewPager.setOffscreenPageLimit (2);
        mIndicator.setViewPager (mViewPager);
    }

    /**
     * 初始化ListView界面相关组件的设置
     */
    private void initListView (int position, List<Topic> topics) {
        getListViewObject (position);
        boolean isInit = mHomeViewPagerAdapter.getPullRefreshViewIsInit (position);
        if (isInit) {
            //当前ListView已经初始化过
            mListViewAdapter = (ListViewAdapter) mListView.getUserAdapter ();
            reGetImageNewsView (mListView.getHeaderView (1));
        } else {
            //开始准备界面和数据
            mErrorMessageRoot.setVisibility (View.VISIBLE);
            mErrorMessageRoot.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    mTvErrorMessage.setVisibility (View.INVISIBLE);
                    mPbErrorMessage.setVisibility (View.VISIBLE);
                    NewsDataDB.getNewsData (2, true, mHandler, GlobalParam.NewsData);
                }
            });
            initListViewListener ();
            //开始获取数据
            String pageTitle = mHomeViewPagerAdapter.getPageTitle (position);
            Integer topicKey = getTopicKey (topics, pageTitle);
            if (topicKey != -1) {
                //获取新闻数据
                NewsDataDB.getNewsData (2, true, mHandler, GlobalParam.NewsData);
            }
        }
        preFirstVisibleItem = 0;
        preListViewScrollState = SCROLL_STATE_IDLE;
        updateErrorMessageStatus (position);

    }

    /**
     * 更新当前item前后的Item错误信息布局的初始显示状态，
     * 只有当相邻的左右两个Item没有正确展示新闻数据的时候
     * 才会重置错误信息布局显示状态
     * @param position
     */
    private void updateErrorMessageStatus(int position){
        int pre = position - 1 < 0 ? 0 : position - 1;
        int next = mHomeViewPagerAdapter.getCount () - 1;
        next = position + 1 > next ? next : position + 1;
        if (pre < position) {
            //更新左边的
            PullRefreshView listView = mHomeViewPagerAdapter.getListView (pre);
            if (listView != null) {
                RelativeLayout root = (RelativeLayout) listView.getTag ();
                boolean isInit2 = mHomeViewPagerAdapter.getPullRefreshViewIsInit (pre);
                if (!isInit2) {
                    //当左边的item初始化展示数据失败
                    ((TextView) root.getTag (R.id.tv_error_message)).setVisibility (View.INVISIBLE);
                    ((ProgressBar) root.getTag (R.id.pb_error_message)).setVisibility (View.VISIBLE);
                    root.setVisibility (View.GONE);
                }
            }
        }
        if (next > position) {
            //更新右边的
            PullRefreshView listView = mHomeViewPagerAdapter.getListView (next);
            if (listView != null) {
                RelativeLayout root = (RelativeLayout) listView.getTag ();
                boolean isInit2 = mHomeViewPagerAdapter.getPullRefreshViewIsInit (next);
                if (!isInit2) {
                    //当右边的item初始化展示数据失败
                    ((TextView) root.getTag (R.id.tv_error_message)).setVisibility (View.INVISIBLE);
                    ((ProgressBar) root.getTag (R.id.pb_error_message)).setVisibility (View.VISIBLE);
                    root.setVisibility (View.GONE);
                }
            }
        }
    }


    /**
     * 设置新闻数据 用于ListView刷新,初始化
     *
     * @param newsDatas 新闻数据
     * @param useCache  是否优先采用缓存数据
     */
    private void showNewsData (final List<NewsData> newsDatas, boolean useCache) {
        if (mListView == null) {
            Log.e (GlobalParam.App_Log, "mListView is null");
            return;
        }
        int headerViewsCount = mListView.getHeaderViewsCount ();
        /*判断当前ListView是否已经添加过图片新闻的ViewPager
          headerViewsCount == 1 表示未添加过，因为PullRefreshView有一个用于刷新的头部
        */
        if (headerViewsCount == 1) {
            mListView.addHeaderView (initImageNewsView ());
        } else {
            View view = mListView.getHeaderView (1);
            reGetImageNewsView (view);
        }
        ImageNewsDB.getImageNews (newsDatas.get (0).getStopic (), useCache, mHandler, GlobalParam.imageNewsData);

        mListViewAdapter = new ListViewAdapter (NewsColumnActivity.this, newsDatas);
        mListView.setAdapter (mListViewAdapter);
    }

    /**
     * 初始化当前ViewPager的Item中包含的ListView的各种监听
     */
    private void initListViewListener () {
        mListView.setOnRefreshListener (new PullRefreshView.onRefreshListener () {
            @Override
            public void onRefreshing () {
                NewsDataDB.getNewsData (mListViewAdapter.getStopic (), false, mHandler, GlobalParam.listViewPullRefresh);
            }

            @Override
            public void onLoadMore () {
                String date = mListViewAdapter.getItem (mListViewAdapter.getCount () - 1).getCreatedAt ();
                NewsDataDB.getNewsData (mListViewAdapter.getStopic (), date, mHandler, GlobalParam.listViewLoadMore);
            }
        });

        mListView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                NewsData newsData = (NewsData) parent.getAdapter ().getItem (position);
                //更新数据库中该条新闻的被浏览量
                NewsDataDB.updateSeeNumber (newsData.getObjectId (),newsData.isVideo ());
                Intent intent = new Intent (NewsColumnActivity.this, TextNewsActivity.class);
                intent.putExtra (GlobalParam.NewsDataTag, newsData);
                startActivity (intent);
            }
        });
        //优化listView数据加载
        optimizeListViewDataLoad ();
    }

    /**
     * 优化listView item中新闻图片的加载，只有在用户滑动和快速滑动静止后才加载图片
     */
    private void optimizeListViewDataLoad () {
        //根据ListView的滑动确定图片的加载
        mListView.setOnScrollListener (new PullRefreshView.OnScrollListener () {
            @Override
            public void onScrollStateChanged (AbsListView view, int scrollState) {
                if (mListViewAdapter != null && preListViewScrollState == SCROLL_STATE_FLING && scrollState == SCROLL_STATE_IDLE) {
                    loadVisibleRegionNewsImage ();
                    preListViewScrollState = scrollState;
                } else if (scrollState == SCROLL_STATE_FLING) {
                    //记录当前状态，以备下次状态改变时用作判断
                    preListViewScrollState = SCROLL_STATE_FLING;
                }
            }

            @Override
            public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //在没设置ListViewAdapter之前是控指针的，所以要加判断
                if (mListViewAdapter == null) {
                    return;
                }
                if (preListViewScrollState == SCROLL_STATE_IDLE) {
                    //只有当用户没有快速滑动时才由该方法加载图片
                    Handler handler = mListViewAdapter.getHandler ();
                    int position;
                    if (preFirstVisibleItem < firstVisibleItem) {
                        //往上滑
                        //-2是因为有两个Header,而adapter中的count不包含header的数量
                        //-1 是根据当前firstVisibleItem和visibleItemCount推算最底下item的position得知的，不见就会逻辑错误
                        position = firstVisibleItem + visibleItemCount - 1 - 2;
                        if (position <= mListViewAdapter.getCount () - 1) {
                            NewsImageDB.getNewsImage (mListViewAdapter.getItem (position).getUrl (), handler);
                        }
                    } else if (preFirstVisibleItem > firstVisibleItem) {
                        //往下滑
                        if (firstVisibleItem > 1) {
                            //当当前firstVisibleItem不是header时加载图片
                            //-2是因为有两个Header,而adapter中的count不包含header的数量
                            position = firstVisibleItem - 2;
                            NewsImageDB.getNewsImage (mListViewAdapter.getItem (position).getUrl (), handler);
                        }
                    }
                }
                //保存当前firstVisibleItem的位置以备比较
                preFirstVisibleItem = firstVisibleItem;
            }
        });
    }

    /**
     * 加载ListView可见区域Item的新闻图片
     */
    private void loadVisibleRegionNewsImage () {
        //当mListView的状态从FLING到IDLE才会调用
        Handler handler = mListViewAdapter.getHandler ();
        //由于有两个头部，所以对position要做处理
        int firstVisibleItem = mListView.getFirstVisiblePosition ();
        firstVisibleItem = firstVisibleItem - 2 < 0 ? 0 : firstVisibleItem - 2;
        int lastVisibleItem = mListView.getLastVisiblePosition () - 2;
        //将当前界面可见条目的新闻Item的图片加载出来
        for (int i = firstVisibleItem; i <= lastVisibleItem; i++) {
            if (lastVisibleItem <= mListViewAdapter.getCount () - 1) {
                //当加载更多的item出现时我门不调用该方法
                NewsImageDB.getNewsImage (mListViewAdapter.getItem (i).getUrl (), handler);
            }
        }
    }

    /**
     * 得到当前ListView以及里面包含的Item中某些View的对象
     */
    private void getListViewObject (int position) {
        mListView = mHomeViewPagerAdapter.getListView (position);
        mErrorMessageRoot = (RelativeLayout) mListView.getTag ();
        mTvErrorMessage = (TextView) mErrorMessageRoot.getTag (R.id.tv_error_message);
        mPbErrorMessage = (ProgressBar) mErrorMessageRoot.getTag (R.id.pb_error_message);
    }

    /**
     * 初始化用于展示图片新闻的ViewPager
     */
    private View initImageNewsView () {
        View view = View.inflate (this, R.layout.view_lisview_viewpager_header, null);
        reGetImageNewsView (view);
        return view;
    }

    /**
     * 重新获取图片新闻各控件对象
     *
     * @param view 包裹这些控件的根布局对象
     */
    private void reGetImageNewsView (View view) {
        mImageNewsViewPager = (SmartViewPager) view.findViewById (R.id.vp_home);
        mImageNewsTitle = (TextView) view.findViewById (R.id.tv_title);
        mPoints = (LinearLayout) view.findViewById (R.id.ll_points);
        mPointSelect = (ImageView) view.findViewById (R.id.point_select);
    }


    /**
     * 解析拿到的图片新闻数据成bean
     */
    public void parseImageNewsJson (List<ImageNews> imageNewses) {
        if (imageNewses == null || imageNewses.size () < 1) {
            Log.e (GlobalParam.App_Log, "图片新闻数据获取失败");
            return;
        }
        List<ImageNewsToJson> imageNewsToJsons = new ArrayList<> ();
        Gson gson = new Gson ();
        for (ImageNews imageNews : imageNewses) {
            ImageNewsToJson imageNewsToJson = gson.fromJson (imageNews.getJsondata (), ImageNewsToJson.class);
            if (imageNewsToJson.info != null && imageNewsToJson.list != null) {
                //判断信息可用才添加
                imageNewsToJsons.add (imageNewsToJson);
                imageNewsToJson.url = imageNews.getUrl ();
                imageNewsToJson.stopic = imageNews.getStopic ();
            }
        }
        if (imageNewsToJsons == null || imageNewsToJsons.size () < 1) {
            Log.e (GlobalParam.App_Log, "解析新闻数据出错!");
            return;
        }

        //由于有时候获取的数据并不可用，所以根据可用数据动态展示
        mPoints.removeAllViews ();
        //固定每个板块图片新闻只展示最小的四条
        for (int i = 0; i < imageNewsToJsons.size (); i++) {
            ImageView imageView = new ImageView (this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                layoutParams.leftMargin = (int) GlobalParam.dp2px (this, 5);
            }
            imageView.setImageResource (R.drawable.viewpager_ovel_point_normal);
            imageView.setLayoutParams (layoutParams);
            mPoints.addView (imageView);
        }

        // 获得视图树观察者, 观察当整个布局的layout时的事件
        mPointSelect.getViewTreeObserver ().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener () {

            @Override
            public void onGlobalLayout () {
                //此方法只需要执行一次就可以: 把当前的监听事件从视图树中移除掉, 以后就不会在回调此事件了.
                mPointSelect.getViewTreeObserver ().removeGlobalOnLayoutListener (this);

                // 点的间距 = 第1个点的左边 - 第0个点的左边;不能直接通过下面的方法获取，否则值为0
                point2pointLength = mPoints.getChildAt (1).getLeft () - mPoints.getChildAt (0).getLeft ();
            }
        });
        initImageNewsData (imageNewsToJsons);
    }

    /**
     * 初始化图片新闻需要的数据
     */
    private void initImageNewsData (final List<ImageNewsToJson> datas) {
        if (mImageNewsViewPager == null) {
            return;
        }
        mImageNewsViewPager.setAdapter (new ImageNewsHeaderAdapter (this, datas));
        mImageNewsViewPager.addOnPageChangeListener (new AbsOnPageChangeListener () {
            @Override
            public void onPageSelected (int position) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPointSelect.getLayoutParams ();
                layoutParams.leftMargin = point2pointLength * (position % datas.size ());
                mPointSelect.setLayoutParams (layoutParams);
                //设置新闻的标题
                mImageNewsTitle.setText (datas.get (position % datas.size ()).info.setname);
            }
        });
        int temp = ImageNewsHeaderAdapter.maxCount / 2;
        mImageNewsViewPager.setCurrentItem (temp - temp % datas.size ());
    }

    /**
     * 获取专题对应的key
     */
    private Integer getTopicKey (List<Topic> topics, String value) {
        for (Topic topic : topics) {
            if (topic.getValue () == value) {
                return topic.getKey ();
            }
        }
        return -1;
    }


    private Handler mHandler = new Handler () {
        private List<NewsData> mNewsDatas;
        @Override
        public void handleMessage (Message msg) {
            switch (msg.what) {
                //获取要展示新闻数据的回调
                case GlobalParam.NewsData:
                    mNewsDatas = (List<NewsData>) msg.obj;
                    Message message = new Message ();
                    if (mNewsDatas != null && mNewsDatas.size () > 0) {
                        //拿到新闻数据
                        showNewsData (mNewsDatas, true);
                        mHomeViewPagerAdapter.setPullRefreshViewIsInit (mViewPager.getCurrentItem ());
                        //1秒以后显示ListView的内容
                        message.obj = true;
                    } else {
                        //没有拿到新闻数据
                        message.obj = false;
                    }
                    message.what = GlobalParam.NewsDataIsReady;
                    mHandler.sendMessageDelayed (message, 1000);
                    break;

                //加载更多后回调
                case GlobalParam.listViewLoadMore:
                    List<NewsData> list = (List<NewsData>) msg.obj;
                    if (list != null && list.size () > 0) {
                        //拿到新闻数据
                        mListViewAdapter.updateDataForLoadMore (list);
                        mListViewAdapter.notifyDataSetChanged ();
                        //加载好新数据后将该区域的新闻图片也加载出来
                        loadVisibleRegionNewsImage ();
                        preListViewScrollState = SCROLL_STATE_IDLE;
                    } else {
                        //没有拿到数据
                        XUtils.toastShort (NewsColumnActivity.this, "网络不给力");
                    }
                    mListView.resetRefreshState ();
                    break;

                //刷新后回调
                case GlobalParam.listViewPullRefresh:
                    if (mListView != null) {
                        List<NewsData> datas = (List<NewsData>) msg.obj;
                        if (datas != null && datas.size () > 0) {
                            //拿到新闻数据
                            showNewsData ((List<NewsData>) msg.obj, false);
                        } else {
                            //没有拿到新闻数据
                            XUtils.toastShort (NewsColumnActivity.this, "网络不给力");
                        }
                        mListView.resetRefreshState ();
                    }
                    break;

                //拿到图片新闻的数据
                case GlobalParam.imageNewsData:
                    //加载图片新闻调用
                    parseImageNewsJson ((List<ImageNews>) msg.obj);
                    break;

                //ListView数据是否拿到并填充好
                case GlobalParam.NewsDataIsReady:
                    boolean success = (boolean) msg.obj;
                    if (success) {
                        //当前ListView数据拿到并以填充完
                        mListView.setVisibility (View.VISIBLE);
                        mTvErrorMessage.setVisibility (View.INVISIBLE);
                        mPbErrorMessage.setVisibility (View.VISIBLE);
                        mErrorMessageRoot.setVisibility (View.GONE);
                    } else {
                        //当前ListView没有拿到数据
                        mTvErrorMessage.setVisibility (View.VISIBLE);
                        mPbErrorMessage.setVisibility (View.INVISIBLE);
                        XUtils.toastShort (NewsColumnActivity.this, "网络不给力");
                    }
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.main_view_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId () == R.id.menu_user) {
            Intent intent;
            if (BmobUser.getCurrentUser () == null) {
                intent = new Intent (this, LoginActivity.class);
            } else {
                intent = new Intent (this, UserActivity.class);
            }
            startActivity (intent);
        }
        return true;
    }

    /**
     * 处理用户按返回按钮，此处要用户在1s内按下两次返回键才能退出应用
     */
    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long l = System.currentTimeMillis ();
            if (l - lastClickTime > 2000) {
                lastClickTime = l;
                XUtils.toastShort (this, "再按一次退出程序");
            } else {
                finish ();
            }
        }
        return true;
    }
}
