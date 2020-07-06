package com.liangnie.xmap.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.liangnie.xmap.MainActivity;
import com.liangnie.xmap.R;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private UserDBHelper userDB;
    private ImageView backIV;
    private EditText usernameET;
    private EditText password1ET;
    private EditText password2ET;
    private Button registerBT;
    private EditText codeET;
    private ImageView showcodeIV;
    private String checkcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        userDB = new UserDBHelper(this);

        //将验证码用图片的形式显示出来
        showcodeIV.setImageBitmap(CheckCode.getInstance().createBitmap());
        checkcode = CheckCode.getInstance().getCode().toLowerCase();
    }

    private void initView() {
        //初始化控件
        backIV = findViewById(R.id.iv_actregister_back);
        usernameET = findViewById(R.id.et_actregister_username);
        password1ET = findViewById(R.id.et_actregister_password1);
        password2ET = findViewById(R.id.et_actregister_password2);
        registerBT = findViewById(R.id.bt_actregister_register);
        codeET = findViewById(R.id.et_actregister_code);
        showcodeIV = findViewById(R.id.iv_actregister_showcode);

        //设置事件监听器
        backIV.setOnClickListener(this);
        registerBT.setOnClickListener(this);
        showcodeIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_actregister_back:
                //返回登录界面
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            case R.id.iv_actregister_showcode:  //更换验证码
                showcodeIV.setImageBitmap(CheckCode.getInstance().createBitmap());
                checkcode = CheckCode.getInstance().getCode().toLowerCase();
                break;

            case R.id.bt_actregister_register:
                String username = usernameET.getText().toString().trim();
                String password1 = password1ET.getText().toString().trim();
                String password2 = password2ET.getText().toString().trim();
                String code = codeET.getText().toString().toLowerCase();

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2) && !TextUtils.isEmpty(code)) {    //判断输入框是否为空
                    if(password1.equals(password2)) {    //判断两次输入的密码是否一致
                        if(code.equals(checkcode)) {
                            int event = userDB.isExist(username, password2);
                            if(event == 3) {    //表中不存在数据
                                userDB.addUser(username, password2);
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                            } else if(event == 0) {
                                userDB.addUser(username, password2);
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "用户名已存在，请更改用户名", Toast.LENGTH_SHORT).show();
                            }
    //                        ArrayList<User> data = userDB.getAllData();
    //                        boolean match = true;
    //                        if(data.size() != 0) {  //判断用户表中的数据是否为空
    //                            for(int i = 0; i < data.size(); i++) {
    //                                User user = data.get(i);
    //                                if(username.equals(user.getName())) {  //判断用户名是否存在
    //                                    match = false;
    //                                    break;
    //                                }
    //                            }
    //                            if(match) {
    //                                userDB.addUser(username, password2);
    //                                Intent intent = new Intent(this, MainActivity.class);
    //                                startActivity(intent);
    //                                finish();
    //                                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
    //                            } else {
    //                                Toast.makeText(this, "用户名已存在，请更改用户名", Toast.LENGTH_SHORT).show();
    //                            }
    //                        } else {
    //                            userDB.addUser(username, password2);
    //                            Intent intent = new Intent(this, MainActivity.class);
    //                            startActivity(intent);
    //                            finish();
    //                            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
    //                        }
                        } else {
                            Toast.makeText(this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "未完善信息，注册失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
