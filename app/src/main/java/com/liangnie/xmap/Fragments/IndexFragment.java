package com.liangnie.xmap.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.liangnie.xmap.R;

public class IndexFragment extends Fragment {

    private static IndexFragment indexFragment;    // 持有自己的一个引用，单例模式
    private MapView mMapView;

    private IndexFragment() {};

    public static IndexFragment getInstance() {
        if (null == indexFragment) {
            indexFragment = new IndexFragment();
        }
        return indexFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        mMapView = view.findViewById(R.id.map);
        configMap();    // 配置地图
        mMapView.onCreate(savedInstanceState);  // 创建地图
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void configMap() {
        AMap aMap = mMapView.getMap();
        UiSettings settings = aMap.getUiSettings();

        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));    // 调整视角缩放等级
        aMap.setMyLocationEnabled(true);    // 显示定位点
        settings.setCompassEnabled(true);   // 显示指南针
        settings.setMyLocationButtonEnabled(true); // 显示定位按钮
        settings.setScaleControlsEnabled(true); // 显示比例尺
    }
}
