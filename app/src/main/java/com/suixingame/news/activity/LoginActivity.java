package com.suixingame.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.suixingame.news.R;
import com.suixingame.news.util.XUtils;

import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher,TextView.OnEditorActionListener{

    /**
     * 用户名
     */
    private EditText mUserName;

    /**
     * 删除用户名EditText的内容
     */
    private ImageView mDeleteUser;

    /**
     * 密码
     */
    private EditText mPassword;

    /**
     * 忘记密码TextView
     */
    private TextView mForgetPass;

    /**
     * 登陆
     */
    private Button mLogin;

    /**
     * 跳转到注册界面
     */
    private TextView mRegist;
    private ImageView mDeletePass;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        initView();
        initData();
    }
    /**
     * 初始化界面控件对象
     */
    private void initView () {
        ActionBar actionBar = getSupportActionBar ();
        actionBar.setTitle ("登录");
        actionBar.setDisplayHomeAsUpEnabled (true);

        mUserName = (EditText) findViewById (R.id.et_username);
        mDeleteUser = (ImageView) findViewById (R.id.iv_delete_username);
        mPassword = (EditText) findViewById (R.id.et_password);
        mDeletePass = (ImageView) findViewById (R.id.iv_delete_password);
        mForgetPass = (TextView) findViewById (R.id.tv_forgetPass);
        mLogin = (Button) findViewById (R.id.bt_login);
        mRegist = (TextView) findViewById (R.id.tv_regist);
    }

    /**
     * 处理界面控件对象的逻辑和数据展示
     */
    private void initData () {
        mDeleteUser.setOnClickListener (this);
        mDeletePass.setOnClickListener (this);
        mForgetPass.setOnClickListener (this);
        mLogin.setOnClickListener (this);
        mRegist.setOnClickListener (this);
        mPassword.addTextChangedListener (this);
        mUserName.addTextChangedListener (this);
        mPassword.setOnEditorActionListener (this);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()){
            case R.id.iv_delete_username:
                mUserName.setText ("");
                break;

            case R.id.iv_delete_password:
                mPassword.setText ("");
                break;

            case R.id.tv_forgetPass:
                Toast.makeText (this, "忘记密码", Toast.LENGTH_SHORT).show ();
                break;
            case R.id.bt_login:
                validateAccount();
                break;
            case R.id.tv_regist:
                startActivity (new Intent (this,RegisterActivity.class));
                break;
        }
    }

    /**
     * 验证账户的正确性
     */
    private void validateAccount () {
        String user = mUserName.getText ().toString ().trim ();
        //匹配邮箱格式
        String check = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        boolean userAvailable = Pattern.compile(check).matcher (user).matches ();

        String pass = mPassword.getText ().toString ().trim ();
        //匹配中文字符
        String check2 = "[\\u4e00-\\u9fa5]";
        //匹配任意字符且长度在6~22位,字母开头
        String check3 = "^[a-zA-Z](\\w|\\W){5,21}$";
        boolean passAvailable;
        if (Pattern.compile(check2).matcher (pass).find ()){
            passAvailable = false;
        }else {
            passAvailable = Pattern.compile(check3).matcher (pass).matches ();
        }
        if (userAvailable && passAvailable){
            BmobUser.loginByAccount (user, XUtils.MD5 (pass), new LogInListener<BmobUser> () {
                @Override
                public void done (BmobUser bmobUser, BmobException e) {
                   if (e!=null){
                       Toast.makeText (LoginActivity.this, "登陆失败!", Toast.LENGTH_SHORT).show ();
                       return;
                   }
                    startActivity (new Intent (LoginActivity.this,UserActivity.class));
                    finish ();
                }
            });
        }else {
            Toast.makeText (this, "用户名或者密码出错!", Toast.LENGTH_SHORT).show ();
        }
    }

    @Override
    public void beforeTextChanged (CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged (CharSequence s, int start, int before, int count) {
        if (!"".equals (mUserName.getText ().toString ().trim ())){
            mDeleteUser.setVisibility (View.VISIBLE);
        }else {
            mDeleteUser.setVisibility (View.INVISIBLE);
        }

        if (!"".equals (mPassword.getText ().toString ().trim ())){
            mDeletePass.setVisibility (View.VISIBLE);
        }else {
            mDeletePass.setVisibility (View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged (Editable s) {
        if (!"".equals (mUserName.getText ().toString ().trim ()) && !"".equals (mPassword.getText ().toString ().trim ())){
            mLogin.setTextColor (getResources ().getColor (R.color.white));
            mLogin.setBackgroundResource (R.drawable.login_text_color_select);
            mLogin.setClickable (true);
        }else {
            mLogin.setTextColor (getResources ().getColor (R.color.gray));
            mLogin.setBackgroundColor (getResources ().getColor (R.color.transparent));
            mLogin.setClickable (false);
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId () == android.R.id.home){
            finish ();
        }
        return true;
    }

    @Override
    public boolean onEditorAction (TextView v, int actionId, KeyEvent event) {
        switch (v.getId ()){
            case R.id.et_password:
                if (actionId == EditorInfo.IME_ACTION_GO){
                    validateAccount ();
                }
                break;
        }
        return false;
    }
}
