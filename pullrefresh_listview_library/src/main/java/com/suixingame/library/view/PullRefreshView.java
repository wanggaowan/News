package com.suixingame.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.suixingame.library.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * <p/>
 * 版 权 ： (c) 2016
 * <p/>
 * 作 者 : 汪高皖
 * <p/>
 * 版 本 ： 1.0
 * <p/>
 * 创建日期 ： 2016/3/16 23:20
 * <p/>
 * 描 述 ：自定义ListView 用于下拉刷新和上拉加载更多内容
 * <p/>
 * 修订历史 ：
 * <p/>
 * ============================================================
 **/
public class PullRefreshView extends ListView implements AbsListView.OnScrollListener {

    private View headerView;//当前View最上面用于显示刷新相关状态的子view
    private View footView;//当前View最下面用于显示加载更多相关状态的子view
    private ImageView mIvArrow; //上拉刷新的箭头
    private ProgressBar mPbRotate;//正在刷新的旋转动画
    private TextView mTvState;//显示当前刷新的状态文本（上拉刷新，松开刷新，正在刷新）
    private TextView mTvTime;//显示上一次刷新的时间

    private int headerViewHeight;//headerView布局的高度
    private int footViewHeight;//footView布局的高度
    private float downY;//记录用户按下时的Y坐标
    //上拉下拉的箭头动画
    private RotateAnimation upRotate;
    private RotateAnimation downRotate;
    //headerView的三种状态
    private final int PULL_REFRESH = 0;//下拉刷新的状态
    private final int RELEASE_REFRESH = 1;//松开刷新的状态
    private final int REFRESHING = 2;//正在刷新的状态
    private int currentState = PULL_REFRESH;//记录当前的状态

    private boolean isLoadMore = false;//记录当前是否在加载更多

    private Map<Integer,View> headerViewMap = new HashMap<> ();


    /**
     * 下拉力度(0~1)，默认0.5
     * 数值越大，手指下拉界面移动幅度就越大
     */
    private float downPower = 0.5f;

    /**
     * 下拉刷新是否可用
     */
    private boolean pullRefreshEnable = true;

    /**
     * 加载更多是否可用
     */
    private boolean loadMoreEnable = true;

    /**
     * 用户自己传入的Adapter
     */
    private ListAdapter userAdapter;


    public PullRefreshView (Context context) {
        super (context);
        init ();
    }

    public PullRefreshView (Context context, AttributeSet attrs) {
        super (context, attrs);
        init ();
    }

    private void init () {
        setOnScrollListener (this);//不设置就无法监听
        initHeaderView ();
        initFootView ();
        initRotate ();
    }


    private void initHeaderView () {
        headerView = View.inflate (getContext (), R.layout.view_pullrefresh_header, null);
        mIvArrow = (ImageView) headerView.findViewById (R.id.iv_arrow);
        mPbRotate = (ProgressBar) headerView.findViewById (R.id.pb_rotate);
        mTvState = (TextView) headerView.findViewById (R.id.tv_state);
        mTvTime = (TextView) headerView.findViewById (R.id.tv_time);

        headerView.measure (0, 0);//主动通知系统去测量headerView的宽高
        headerViewHeight = headerView.getMeasuredHeight ();//这个在measure方法执行完就可以得到
        headerView.setPadding (0, -headerViewHeight, 0, 0);
        addHeaderView (headerView);
    }

    private void initFootView () {
        footView = View.inflate (getContext (), R.layout.view_pullrefresh_foot, null);
        footView.measure (0, 0);
        footViewHeight = footView.getMeasuredHeight ();
        footView.setPadding (0, 0, 0, -footViewHeight);
        addFooterView (footView);
    }


    private void initRotate () {
        upRotate = new RotateAnimation (0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upRotate.setDuration (500);
        upRotate.setFillAfter (true);

        downRotate = new RotateAnimation (-180, -360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downRotate.setDuration (500);
        downRotate.setFillAfter (true);
    }

    @Override
    public void setAdapter (ListAdapter adapter) {
        super.setAdapter (adapter);
        userAdapter = adapter;
    }

    /**
     * 获得传入的Adapter,可以强转
     * @return
     */
    public ListAdapter getUserAdapter () {
        return userAdapter;
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent ev) {
        if (!pullRefreshEnable) {
            //下拉刷新模式未开启
            return super.dispatchTouchEvent (ev);
        }
        switch (ev.getAction ()) {
            case MotionEvent.ACTION_DOWN:
                //当用户只是单纯的点击或者上划并且第一个可见项不是0，ListView要允许滑动
                setEnabled (true);
                downY = ev.getRawY ();
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentState == REFRESHING) {
                    break;//如果当前正在刷新，就不允许再次往下拉
                }
                int moveLength = (int) (ev.getRawY () - downY);
                int currentPadding = (int) (-headerViewHeight + moveLength * downPower);
                if (currentPadding > -headerViewHeight && getFirstVisiblePosition () == 0) {
                    if (currentPadding >= 0 && currentState == PULL_REFRESH) {
                        currentState = RELEASE_REFRESH;
                        refreshHearView ();
                    } else if (currentPadding < 0 && currentState == RELEASE_REFRESH) {
                        currentState = PULL_REFRESH;
                        refreshHearView ();
                    }
                    //设置下拉刷新的最大拉动距离
                    if (currentPadding > 100) {
                        headerView.setPadding (0, 100, 0, 0);
                    } else {
                        headerView.setPadding (0, currentPadding, 0, 0);
                    }
                    /*这样在下拉刷新的时候松开就不会触发点击事件
                    * 但是这样ListView就不能够滑动，但是可以下拉是因为我们动态设置了header的Padding
                    */
                    setEnabled (false);
                    //不返回true，那么在往回滑动时ListView会有自己的惯性，滑动距离实际比自己手指滑动的长
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentState == PULL_REFRESH) {
                    headerView.setPadding (0, -headerViewHeight, 0, 0);
                } else if (currentState == RELEASE_REFRESH) {
                    headerView.setPadding (0, 0, 0, 0);
                    currentState = REFRESHING;
                    refreshHearView ();
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefreshing ();
                    }
                }
                break;
        }
        return super.dispatchTouchEvent (ev);
    }

    @Override
    public void onScrollStateChanged (AbsListView view, int scrollState) {
        //由于我重写了该方法，所以要对外暴露自己定义的接口
        if (mOnScrollListener!=null){
            mOnScrollListener.onScrollStateChanged (view,scrollState);
        }

        if (!loadMoreEnable) {
            //加载更多模式没开启
            return;
        }
        if (scrollState == SCROLL_STATE_IDLE && getLastVisiblePosition () == (getCount () - 1) && !isLoadMore) {
            isLoadMore = true;
            footView.setPadding (0, 0, 0, 0);
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onLoadMore ();
            }
                 /*
                 让getCount()位置的View显示在该ListView可见的第一个位置
                 但是如果当前位置的View下面没有更多的其它View用以填充整个手机屏幕可见范围，
                 那么getCount()位置的View优先显示在以填充完整个屏幕为主
                 比如：getCount()处的View为最后一个，那么它依旧显示在屏幕最下面而不会在屏幕最上面显示
                  */
            setSelection (getCount ());
        }
    }

    @Override
    public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener!=null){
            mOnScrollListener.onScroll (view,firstVisibleItem,visibleItemCount,totalItemCount);
        }
    }

    @Override
    public void addHeaderView (View v) {
        super.addHeaderView (v);
        headerViewMap.put (getHeaderViewsCount () - 1,v);
    }

    /**
     * 获取添加的HeaderView
     * @param position
     * @return
     */
    public View getHeaderView(int position){
        return headerViewMap.get (position);
    }

    public void resetRefreshState () {
        if (isLoadMore) {
            isLoadMore = !isLoadMore;
            footView.setPadding (0, 0, 0, -footViewHeight);
        } else {
            mTvState.setText ("下拉刷新");
            mIvArrow.setVisibility (ImageView.VISIBLE);
            mPbRotate.setVisibility (ProgressBar.INVISIBLE);
            currentState = PULL_REFRESH;
            mTvTime.setText ("最后刷新时间:" + getDate ());
            headerView.setPadding (0, -headerViewHeight, 0, 0);
        }

    }

    private String getDate () {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yy-MM-dd HH:mm:ss");
        return simpleDateFormat.format (new Date ());
    }

    private void refreshHearView () {
        switch (currentState) {
            case PULL_REFRESH:
                mTvState.setText ("下拉刷新");
                mIvArrow.setVisibility (ImageView.VISIBLE);
                mPbRotate.setVisibility (ProgressBar.INVISIBLE);
                mIvArrow.startAnimation (upRotate);
                break;
            case RELEASE_REFRESH:
                mTvState.setText ("松开刷新");
                mIvArrow.startAnimation (downRotate);
                break;
            case REFRESHING:
                mTvState.setText ("正在刷新...");
                mIvArrow.clearAnimation ();//防止动画没完成就隐藏会影响显示效果
                mIvArrow.setVisibility (ImageView.INVISIBLE);
                mPbRotate.setVisibility (ProgressBar.VISIBLE);
                break;
        }
    }

    /**
     * 设置下拉力度
     *
     * @param downPower 0~1 值越大，组件移动幅度越大
     *                  当设置为0时，相当于调用{@link #setPullRefreshEnable(boolean)  setPullRefreshEnable(boolean)参数传入false}
     *                  此时下拉刷新不可用
     */
    public void setDownPower (float downPower) {
        if (downPower < 0) {
            this.downPower = 0;
        } else if (downPower > 1) {
            this.downPower = 1;
        } else {
            this.downPower = downPower;
        }

    }

    /**
     * 设置是否启用下拉刷新
     *
     * @param pullRefreshEnable true 启用下拉刷新
     */
    public void setPullRefreshEnable (boolean pullRefreshEnable) {
        this.pullRefreshEnable = pullRefreshEnable;
    }

    /**
     * 设置是否启用加载更多
     *
     * @param loadMoreEnable true 启用加载更多
     */
    public void setLoadMoreEnable (boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }

    /**
     * 判断加载更多模式是否开启
     */
    public boolean isLoadMoreEnable () {
        return loadMoreEnable;
    }

    /**
     * 判断下拉刷新模式是否开启
     */
    public boolean isPullRefreshEnable () {
        return pullRefreshEnable;
    }

    private onRefreshListener mOnRefreshListener;

    private OnScrollListener mOnScrollListener;

    public void setOnRefreshListener (onRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public void setOnScrollListener (OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    /**
     * 滑动监听接口
     */
    public interface OnScrollListener{

        void onScrollStateChanged (AbsListView view, int scrollState);

        void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

    }

    /**
     * 下拉刷新，上拉加载更多接口
     */
    public interface onRefreshListener {
        void onRefreshing ();

        void onLoadMore ();
    }
}

