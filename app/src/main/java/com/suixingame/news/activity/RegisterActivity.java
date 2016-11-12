package com.suixingame.news.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
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
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher,TextView.OnEditorActionListener{

    private EditText mUserName;
    private EditText mPassword;
    private ImageView mDeleteUser;
    private TextView mShowPass;
    private Button mRegister;

    /**
     * 是否显示密码
     */
    private boolean isShowPass = false;
    private ImageView mDeletePass;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_register);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView () {
        ActionBar actionBar = getSupportActionBar ();
        mUserName = (EditText) findViewById (R.id.et_username);
        mPassword = (EditText) findViewById (R.id.et_password);
        mDeleteUser = (ImageView) findViewById (R.id.iv_delete_username);
        mShowPass = (TextView) findViewById (R.id.tv_showPass);
        mDeletePass = (ImageView) findViewById (R.id.iv_delete_password);
        mRegister = (Button) findViewById (R.id.bt_register);

        actionBar.setTitle ("注册");
        actionBar.setDisplayHomeAsUpEnabled (true);
        mDeleteUser.setOnClickListener (this);
        mShowPass.setOnClickListener (this);
        mDeletePass.setOnClickListener (this);
        mRegister.setOnClickListener (this);
        mUserName.addTextChangedListener (this);
        mPassword.addTextChangedListener (this);
        mPassword.setOnEditorActionListener (this);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId ()){
            case R.id.iv_delete_username:
                mUserName.setText ("");
                break;
            case R.id.tv_showPass:
                if (isShowPass){
                    //一定要设置括号里的两个属性，否则不起作用
                    mPassword.setInputType (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPassword.setSelection (mPassword.getText ().toString ().length ());
                    mShowPass.setText ("显示密码");
                    isShowPass = false;
                }else {
                    mPassword.setInputType (InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPassword.setSelection (mPassword.getText ().toString ().length ());
                    mShowPass.setText ("隐藏密码");
                    isShowPass = true;
                }
                break;
            case R.id.iv_delete_password:
                mPassword.setText ("");
                break;
            case R.id.bt_register:
                validateAccount ();
                break;
        }
    }

    /**
     * 验证账户的正确性
     */
    private void validateAccount () {
        String username = mUserName.getText ().toString ().trim ();
        //匹配邮箱格式
        String check = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        boolean userAvailable = Pattern.compile(check).matcher (username).matches ();

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
            BmobUser user = new BmobUser ();
            user.setUsername ("user_"+System.currentTimeMillis ());
            user.setEmail (username);
            user.setPassword (XUtils.MD5 (pass));
            user.signUp (new SaveListener<BmobUser> () {
                @Override
                public void done (BmobUser bmobUser, BmobException e) {
                    if (e!=null){
                        Toast.makeText (RegisterActivity.this, "注册失败!", Toast.LENGTH_SHORT).show ();
                        return;
                    }
                    startActivity (new Intent (RegisterActivity.this,UserActivity.class));
                    finish ();
                }
            });
        }else {
            Toast.makeText (this, "请检查用户名或者密码格式!", Toast.LENGTH_SHORT).show ();
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
            mRegister.setTextColor (getResources ().getColor (R.color.white));
            mRegister.setBackgroundResource (R.drawable.login_text_color_select);
            mRegister.setClickable (true);
        }else {
            mRegister.setTextColor (getResources ().getColor (R.color.gray));
            mRegister.setBackgroundColor (getResources ().getColor (R.color.transparent));
            mRegister.setClickable (false);
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
        //返回true，就自己手动隐藏软键盘，false系统会帮你处理
        return false;
    }
}
