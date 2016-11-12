package com.suixingame.news.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suixingame.library.view.PullRefreshView;
import com.suixingame.news.R;
import com.suixingame.news.bean.Topic;
import com.viewpagerindicator.TabPageIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 *
 * 版 权 ： (c) 2016
 *
 * 作 者 : 汪高皖
 *
 * 版 本 ： 1.0
 *
 * 创建日期 ： 2016/10/10 12:10
 *
 * 描 述 ：主页展示各新闻类型的ViewPager适配器
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

public class HomeViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    /**
     * 数据
     */
    private List<Topic> mDatas;

    /**
     * 记录position位置的PullRefreshView对象，用于返回给需要调用的地方
     */
    private Map<Integer,PullRefreshView> mListViews = new HashMap<> ();

    /**
     * 记录position位置的PullRefreshView是否初始化过
     */
    private Map<Integer,Boolean> mListViewIsInit = new HashMap<> ();

    private TabPageIndicator mIndicator;
    /**
     * 是否是第一次初始化该Adapter对应的Viewpager
     */
    private boolean firstInit = true;

    public HomeViewPagerAdapter (Context context, List<Topic> datas, TabPageIndicator indicator) {
        this.mContext = context;
        this.mDatas = datas;
        mIndicator = indicator;
    }

    @Override
    public int getCount () {
        return mDatas.size ();
    }

    @Override
    public boolean isViewFromObject (View view, Object object) {
        return view == object;
    }

    @Override
    public String getPageTitle (int position) {
        return mDatas.get (position).getValue ();
    }


    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        mListViews.remove (position);
        mListViewIsInit.remove (position);
        container.removeView ((View) object);
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        View inflate = View.inflate (mContext, R.layout.view_news_listview, null);
        PullRefreshView listView = (PullRefreshView) inflate.findViewById (R.id.lv_content);
        //错误消息布局
        RelativeLayout errorMessageRoot = (RelativeLayout) inflate.findViewById (R.id.rl_progressBar);
        TextView tvErrorMessage = (TextView) inflate.findViewById (R.id.tv_error_message);
        ProgressBar pbErrorMessage = (ProgressBar) inflate.findViewById (R.id.pb_error_message);
        errorMessageRoot.setTag (R.id.tv_error_message,tvErrorMessage);
        errorMessageRoot.setTag (R.id.pb_error_message,pbErrorMessage);
        //listView携带错误布局对象以备使用
        listView.setTag (errorMessageRoot);

        listView.setDownPower (0.4f);
        mListViews.put (position, listView);
        container.addView (inflate);
        //不知道为什么当我往回翻还没到position的位置，position == 0就成立了，所以外加一个是否第一次初始化标志
        if (firstInit && position == 0){
            mIndicator.onPageSelected (0);
            firstInit = false;
        }
        return inflate;
    }

    /**
     * 返回用于展示数据的ListView
     * @return
     */
    public PullRefreshView getListView(int position){
        return mListViews.get (position);
    }

    /**
     * 设置position位置的PullRefreshView初始化完成
     * @param position
     */
    public void setPullRefreshViewIsInit(int position){
        mListViewIsInit.put (position,true);
    }

    /**
     * 得到position位置的PullRefreshView是否初始化完成,如果得到的对象为空,就返回false
     * @param position
     * @return
     */
    public boolean getPullRefreshViewIsInit(int position){
        Boolean isInit = mListViewIsInit.get (position);
        if (isInit == null){
            return false;
        }else {
            return isInit;
        }
    }

    private void initListView(PullRefreshView listView){

    }
}
