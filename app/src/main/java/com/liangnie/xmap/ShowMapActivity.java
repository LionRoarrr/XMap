package com.liangnie.xmap;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.liangnie.xmap.Fragments.IndexFragment;
import com.liangnie.xmap.Fragments.MeFragment;

public class ShowMapActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        // 使用按钮组实现底部菜单栏
        RadioGroup radioGroup = findViewById(R.id.bot_rb_group);
        radioGroup.setOnCheckedChangeListener(this);
        replaceFragment(IndexFragment.getInstance());
        initFragement();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_index:
                replaceFragment(IndexFragment.getInstance());
                break;
            case R.id.rb_me:
                replaceFragment(MeFragment.getInstance());
                break;
        }
    }

    private void initFragement() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.content, IndexFragment.getInstance());
        transaction.add(R.id.content, MeFragment.getInstance());
        transaction.commit();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (fragment instanceof IndexFragment) {
            transaction.hide(MeFragment.getInstance());
        } else {
            transaction.hide(IndexFragment.getInstance());
        }
        transaction.show(fragment);
        transaction.commit();
    }
}