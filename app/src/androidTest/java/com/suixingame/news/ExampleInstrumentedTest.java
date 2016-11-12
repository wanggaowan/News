package com.suixingame.news;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.suixingame.news.bean.NewsData;
import com.suixingame.news.bean.Topic;
import com.suixingame.news.util.GlobalParam;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext () throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext ();

        BmobQuery<NewsData> query = new BmobQuery<> ();
        query.addWhereEqualTo ("stopic", 2);
        query.setLimit (10);
        query.findObjects (new FindListener<NewsData> () {
            @Override
            public void done (List<NewsData> list, BmobException e) {
                if (list!=null){
                    System.out.println (list.size ());
                }else {
                    System.out.println ("query failed-------------");
                }
            }
        });
    }

    @Test
    public void testJson(){
        try {
            List<Topic> topics = GlobalParam.getTopics (InstrumentationRegistry.getTargetContext ());
            for(Topic topic:topics){
                System.out.println (topic.getKey ()+":"+topic.getValue ());
            }
        } catch (JSONException e) {
            e.printStackTrace ();
        }
    }
}
