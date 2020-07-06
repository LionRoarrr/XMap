package com.liangnie.xmap.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liangnie.xmap.R;
import com.liangnie.xmap.activities.LoginActivity;
import com.liangnie.xmap.bean.User;
import com.liangnie.xmap.utils.ToastUtil;

public class MeFragment extends Fragment implements View.OnClickListener {
    private TextView mUsername;
    private TextView mLoginText;
    private Button mAboutButton;
    private Button mLogoutButton;
    private User mLoginUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        mUsername = view.findViewById(R.id.username);
        mLoginText = view.findViewById(R.id.login_text);
        mAboutButton = view.findViewById(R.id.about_btn);
        mLogoutButton = view.findViewById(R.id.logout_btn);

        mLoginText.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mLogoutButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setData();
        }
    }

    private void setData() {
        mLoginUser = readUserInfo();
        if (mLoginUser != null) {
            mLoginText.setText(mLoginUser.getName());
            mLogoutButton.setVisibility(View.VISIBLE);
        }
    }

    private User readUserInfo() {
        User user = null;
        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
            String username = preferences.getString("username", "");
            if (!"".equals(username)) {
                user = new User(username, "");
            }
        }
        return user;
    }

    private boolean clearUserInfo() {
        if (getActivity() != null) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
            return true;
        }
        return false;
    }

    private void onAboutButtonClick() {
        if (getActivity() != null) {
            HintDialogFragment fragment = HintDialogFragment.newInstance("关于"
                    , "[项目]\n基于高德SDK的简单导航安卓软件XMap\n[成员]\n梁大勇 聂祥\n[Github地址]\nhttps://github.com/LionRoarrr/XMap");
            fragment.show(getActivity().getSupportFragmentManager(), "HintDialog");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_text:
                if (mLoginUser == null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.about_btn:
                onAboutButtonClick();
                break;
            case R.id.logout_btn:
                if (clearUserInfo()) {
                    mLoginUser = null;
                    mLoginText.setText("点击登录");
                    mLogoutButton.setVisibility(View.GONE);
                    ToastUtil.showToast(getActivity(), "已退出登录");
                }
                break;
        }
    }
}
