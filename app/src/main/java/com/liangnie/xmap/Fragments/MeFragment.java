package com.liangnie.xmap.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liangnie.xmap.R;

public class MeFragment extends Fragment {
    private static MeFragment meFragment;

    private MeFragment() {};

    public static MeFragment getInstance() {
        if (null == meFragment) {
            meFragment = new MeFragment();
        }
        return meFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        return view;
    }
}
