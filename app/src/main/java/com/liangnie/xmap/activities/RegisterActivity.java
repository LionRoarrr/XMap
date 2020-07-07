package com.liangnie.xmap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.liangnie.xmap.R;
import com.liangnie.xmap.dbhelper.UserDBHelper;
import com.liangnie.xmap.utils.CheckCode;
import com.liangnie.xmap.utils.ToastUtil;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private UserDBHelper userDB;
    private ImageView mBack;
    private EditText mUsername;
    private EditText mPassword1;
    private EditText mPassword2;
    private Button mRegisterButton;
    private EditText mCode;
    private ImageView mShowCode;
    private String checkcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        userDB = new UserDBHelper(this);

        //将验证码用图片的形式显示出来
        mShowCode.setImageBitmap(CheckCode.getInstance().createBitmap());
        checkcode = CheckCode.getInstance().getCode().toLowerCase();
    }

    private void initView() {
        //初始化控件
        mBack = findViewById(R.id.iv_actregister_back);
        mUsername = findViewById(R.id.et_actregister_username);
        mPassword1 = findViewById(R.id.et_actregister_password1);
        mPassword2 = findViewById(R.id.et_actregister_password2);
        mRegisterButton = findViewById(R.id.bt_actregister_register);
        mCode = findViewById(R.id.et_actregister_code);
        mShowCode = findViewById(R.id.iv_actregister_showcode);

        //设置事件监听器
        mBack.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mShowCode.setOnClickListener(this);
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
                mShowCode.setImageBitmap(CheckCode.getInstance().createBitmap());
                checkcode = CheckCode.getInstance().getCode().toLowerCase();
                break;

            case R.id.bt_actregister_register:
                String username = mUsername.getText().toString().trim();
                String password1 = mPassword1.getText().toString().trim();
                String password2 = mPassword2.getText().toString().trim();
                String code = mCode.getText().toString().toLowerCase();

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2) && !TextUtils.isEmpty(code)) {    //判断输入框是否为空
                    if(password1.equals(password2)) {    //判断两次输入的密码是否一致
                        if(code.equals(checkcode)) {
                            int event = userDB.isExist(username, password2);
                            if(event == 3) {    //表中不存在数据
                                userDB.addUser(username, password2);
                                finish();
                                ToastUtil.showToast(this, "注册成功");
                            } else if(event == 0) {
                                userDB.addUser(username, password2);
                                finish();
                                ToastUtil.showToast(this, "注册成功");
                            } else {
                                ToastUtil.showToast(this, "用户名已存在，请更改用户名");
                            }
                        } else {
                            ToastUtil.showToast(this, "验证码错误，请重新输入");
                        }
                    } else {
                        ToastUtil.showToast(this, "两次输入的密码不一致，请重新输入");
                    }
                } else {
                    ToastUtil.showToast(this, "请填写完全注册信息");
                }
            break;
        }
    }
}
