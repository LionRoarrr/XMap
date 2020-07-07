package com.liangnie.xmap.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.liangnie.xmap.R;
import com.liangnie.xmap.bean.User;
import com.liangnie.xmap.dbhelper.UserDBHelper;
import com.liangnie.xmap.utils.ToastUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private UserDBHelper userDB;
    private TextView registerTV;
    private RelativeLayout topRL;
    private EditText usernameET;
    private EditText passwordET;
    private LinearLayout twoLL;
    private Button loginBT;
    private ImageButton backBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        userDB = new UserDBHelper(this);
    }

    private void initView() {
        //初始化控件
        registerTV = findViewById(R.id.tv_actlogin_register);
        topRL = findViewById(R.id.rl_actlogin_top);
        usernameET = findViewById(R.id.et_actlogin_username);
        passwordET = findViewById(R.id.et_actlogin_password);
        twoLL = findViewById(R.id.ll_actlogin_two);
        loginBT = findViewById(R.id.bt_actlogin_login);
        backBT = findViewById(R.id.iv_actlogin_back);

        //设置事件监听器
        loginBT.setOnClickListener(this);
        registerTV.setOnClickListener(this);
        backBT.setOnClickListener(this);
    }

    private void saveLoginUser(User user) {
        SharedPreferences.Editor editor = getSharedPreferences("user_info", MODE_PRIVATE).edit();
        editor.putString("username", user.getName());
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //跳转到注册页面
            case R.id.tv_actlogin_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.bt_actlogin_login:
                String username = usernameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    int event = userDB.isExist(username, password);
                    if(event == 1) {
                        User user = new User(username, password);
                        saveLoginUser(user);
                        finish();
                        ToastUtil.showToast(this, "登录成功");
                    } else {
                        ToastUtil.showToast(this, "账号或密码错误，请重新输入");
                    }
                } else {
                    ToastUtil.showToast(this, "请输入用户名和密码");
                }
                break;

            case R.id.iv_actlogin_back:
                finish();
                break;
        }
    }

}
