package com.liangnie.xmap.activities;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.AMapGestureListener;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.liangnie.xmap.R;
import com.liangnie.xmap.fragments.MainFragment;
import com.liangnie.xmap.fragments.RouteFragment;
import com.liangnie.xmap.listeners.OnPageNaviCheckedListener;
import com.liangnie.xmap.overlays.DrivingRouteOverlay;

public class MainMapActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener
        , AMapGestureListener
        , OnPageNaviCheckedListener {

    public static final int TAG_MAIN_FRAGMENT = 1;
    public static final int TAG_ROUTE_FRAGMENT = 2;

    private static final int DEFAULT_ZOOM = 16;

    private AMap mMap;  // 地图控制器
    private MyLocationStyle mMyLocationStyle;   // 定位样式
    private Location mMyLocation;
    private Boolean mIsMapRotateMode = false;   // 定位模式是否是地图跟随模式
    private Fragment mCurrentFragment;
    private MainFragment mMainFragment; // 主要Fragment
    private RouteFragment mRouteFragment; // 路线Fragment

    private MapView mMapView;   // 地图组件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        initView(savedInstanceState);   // 初始化视图
        initFragment();
    }

    public void initView(Bundle savedInstanceState) {
//        mFabLocation = findViewById(R.id.fab_location);
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);  // 创建地图

        // 定位样式设置
        mMyLocationStyle = new MyLocationStyle();  // 定位点样式
        mMyLocationStyle.strokeColor(Color.TRANSPARENT);   // 隐藏精度圈
        mMyLocationStyle.radiusFillColor(Color.TRANSPARENT);
        mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);

        mMap = mMapView.getMap();
        mMap.setOnMyLocationChangeListener(this);   // 设置位置监听
        mMap.setAMapGestureListener(this);  // 设置手势监听
        mMap.setMyLocationStyle(mMyLocationStyle); // 设置定位点样式
        mMap.setMyLocationEnabled(true);    // 显示定位点
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM)); // 相机视角缩放

        // 地图UI设置
        UiSettings settings = mMap.getUiSettings(); // 获取地图控制器UI设置
        settings.setLogoBottomMargin(-50);  // 下偏移高德logo以隐藏
        settings.setZoomControlsEnabled(false); // 不显示缩放控件
    }

    public void initFragment() {
        mMainFragment = new MainFragment();
        mRouteFragment = new RouteFragment();

        switchFragment(mMainFragment);  // 显示首页
    }

    private void switchFragment(Fragment target) {
        mMap.clear();
        changeMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);    // 自动定位到我的位置
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

//        if (target.isAdded()) {
//            transaction.hide(mCurrentFragment).show(target).commit();
//        } else if (null != mCurrentFragment) {
//            transaction.hide(mCurrentFragment).add(R.id.fragment_container, target).commit();
//        } else {
//            transaction.add(R.id.fragment_container, target).commit();
//        }
//        mCurrentFragment = target;

        // 有Fragment重新创建视图的需求，故使用以下Fragment切换逻辑
        if (!target.equals(mCurrentFragment)) {
            transaction.replace(R.id.fragment_container, target);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        mCurrentFragment = target;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (null != location) {
            mMyLocation = location;
        }
    }

    private void toggleMyLocationType() {
        LatLng position = mMap.getCameraPosition().target;

        // 坐标精度
        int accuracy = 10000;
        long pLatitude = (long) (position.latitude * accuracy);
        long pLongitude = (long) (position.longitude * accuracy);
        long mLatitude = (long) (mMyLocation.getLatitude() * accuracy);
        long mLongitude = (long) (mMyLocation.getLongitude() * accuracy);

        // 判断相机和定位位置是否相同，若不同，相机对准定位点，不切换模式
        if (pLatitude == mLatitude && pLongitude == mLongitude) {
            // 模式切换
            if (mIsMapRotateMode) {
                changeMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);    // 切换模式
                LatLng latLng = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, DEFAULT_ZOOM, 0, 0));
                mMap.animateCamera(update); // 该模式下的相机视角
            } else
                changeMyLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);

            mIsMapRotateMode = !mIsMapRotateMode;
        } else {
            LatLng latLng = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
        }
    }

    private void changeMyLocationType(int mode) {
        mMyLocationStyle.myLocationType(mode);
        mMap.setMyLocationStyle(mMyLocationStyle);
    }

    @Override
    public void onDoubleTap(float v, float v1) {

    }

    @Override
    public void onSingleTap(float v, float v1) {

    }

    @Override
    public void onFling(float v, float v1) {

    }

    @Override
    public void onScroll(float v, float v1) {

    }

    @Override
    public void onLongPress(float v, float v1) {

    }

    @Override
    public void onDown(float v, float v1) {
        // 解决手势触发后，定位跟随地图显示不正确的问题
        changeMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
    }

    @Override
    public void onUp(float v, float v1) {

    }

    @Override
    public void onMapStable() {

    }

    @Override
    public void onChecked(int id) {
        Log.i("TAG", "onChecked: " + id);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment.equals(mRouteFragment)) {
            switchFragment(mMainFragment);
        } else {
            moveTaskToBack(false);
        }
    }

    public void locateMe() {
        toggleMyLocationType();
    }

    public void gotoFragment(int tag) {
        switch (tag) {
            case TAG_MAIN_FRAGMENT:
                switchFragment(mMainFragment);
                break;
            case TAG_ROUTE_FRAGMENT:
                switchFragment(mRouteFragment);
                break;
        }
    }

    public Location getMyLocation() {
        return mMyLocation;
    }

    /*
    * 地图重置到我的定位
    * */
    public void resetMyLocationMap() {
        mMap.setMyLocationStyle(mMyLocationStyle); // 设置定位点样式
        mMap.setMyLocationEnabled(true);    // 显示定位点
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM)); // 相机视角缩放
    }

    public void showDrivingRoute(DrivePath path, LatLonPoint startPos, LatLonPoint endPos) {
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(getApplicationContext(),
                mMap, path, startPos, endPos, null);
        overlay.setNodeIconVisibility(false);   // 不显示节点Marker
        overlay.setIsColorfulline(true);    // 以颜色展示交通拥堵情况
        overlay.removeFromMap();
        overlay.addToMap();
        overlay.zoomToSpan();
    }
}