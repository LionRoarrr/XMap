package com.liangnie.xmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.navi.AMapNaviView;
import com.liangnie.xmap.R;

public class NaviFragment extends Fragment {

    private AMapNaviView mMapNaviView;  // 导航地图控件

    private AMap mMap;  // 地图控制器

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navi, container, false);

        mMapNaviView = view.findViewById(R.id.navi_view);
        if (null == mMap) {
            mMap = mMapNaviView.getMap();
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapNaviView.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapNaviView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapNaviView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapNaviView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapNaviView.onSaveInstanceState(outState);
    }


}
