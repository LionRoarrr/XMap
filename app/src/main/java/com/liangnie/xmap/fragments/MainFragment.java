package com.liangnie.xmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.liangnie.xmap.R;

public class MainFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private Fragment mCurrentFragment;  // 当前显示Fragment
    private IndexFragment mIndexFragment;   // 首页Fragment
    private MeFragment mMeFragment; // 我的Fragment

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.nav_rb_group);
        radioGroup.setOnCheckedChangeListener(this);    // 导航栏监听

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFragment();
    }

    public void initFragment() {
        mIndexFragment = new IndexFragment();
        mMeFragment = new MeFragment();
        switchFragment(mIndexFragment);
    }

    private void switchFragment(Fragment target) {
        if (mCurrentFragment == target) {
            return;
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter,
                R.anim.fragment_slide_right_exit);

        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (!target.isAdded()) {
            transaction.add(R.id.fragment_container, target).commit();
        } else {
            transaction.hide(mCurrentFragment).show(target).commit();
        }

        mCurrentFragment = target;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_index:
                switchFragment(mIndexFragment);
                break;
            case R.id.rb_me:
                switchFragment(mMeFragment);
                break;
        }
    }
}
