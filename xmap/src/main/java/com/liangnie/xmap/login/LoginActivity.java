package com.liangnie.xmap.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.liangnie.xmap.MainActivity;
import com.liangnie.xmap.R;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private UserDBHelper userDB;
    private TextView registerTV;
    private RelativeLayout topRL;
    private EditText usernameET;
    private EditText passwordET;
    private LinearLayout twoLL;
    private Button loginBT;


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

        //设置事件监听器
        loginBT.setOnClickListener(this);
        registerTV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //跳转到注册页面
            case R.id.tv_actlogin_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;

            case R.id.bt_actlogin_login:
                String username = usernameET.getText().toString().trim();
                String password = passwordET.getText().toString().trim();
                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    int event = userDB.isExist(username, password);
                    if(event == 1) {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "账号或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    }
//                    ArrayList<User> data = userDB.getAllData();
//                    boolean match = false;
//                    for(int i = 0; i < data.size(); i++) {
//                        User user = data.get(i);
//                        if(username.equals(user.getName()) && password.equals(user.getPassword())) {
//                            match = true;
//                            break;
//                        } else {
//                            match = false;
//                        }
////                        System.out.println(user.getName());
////                        System.out.println(user.getPassword());
//                    }
//                    if(match) {
//                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(this, "账号或密码错误，请重新输入", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(this, "请输入您的账号和密码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
