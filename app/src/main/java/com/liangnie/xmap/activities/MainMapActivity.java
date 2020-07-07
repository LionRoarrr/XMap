package com.liangnie.xmap.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.liangnie.xmap.fragments.HintDialogFragment;
import com.liangnie.xmap.fragments.LoadingDialogFragment;
import com.liangnie.xmap.fragments.MainFragment;
import com.liangnie.xmap.fragments.PermissionFragment;
import com.liangnie.xmap.fragments.RouteFragment;
import com.liangnie.xmap.fragments.SearchFragment;
import com.liangnie.xmap.overlays.DrivingRouteOverlay;
import com.liangnie.xmap.utils.MapUtil;
import com.liangnie.xmap.utils.ToastUtil;

import java.util.Stack;

public class MainMapActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener
        , AMapGestureListener {

    public static final int TAG_MAIN_FRAGMENT = 1;
    public static final int TAG_ROUTE_FRAGMENT = 2;
    public static final int TAG_SEARCH_FRAGMENT = 3;
    public static final int CODE_REQUEST_LOCATION = 100;

    private static final int DEFAULT_ZOOM = 16; // 地图默认缩放等级

    private MapView mMapView;   // 地图组件
    private AMap mMap;  // 地图控制器
    private MyLocationStyle mMyLocationStyle;   // 定位样式
    private Location mMyLocation;
    private Boolean mIsMapRotateMode = false;   // 定位模式是否是地图跟随模式
    private long mLastBackTime = 0;

    private Fragment mCurrentFragment;
    private MainFragment mMainFragment; // 主要Fragment
    private RouteFragment mRouteFragment; // 路线Fragment
    private SearchFragment mSearchFragment;  // 搜索界面
    private LoadingDialogFragment mLoadingDialogFragment;   // 加载弹窗
    private Stack<Fragment> mFragmentBackStack; // Fragment回退栈

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        mFragmentBackStack = new Stack<>();
        initView(savedInstanceState);   // 初始化视图
        initFragment();
        initPermission();
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
        settings.setLogoBottomMargin(220);  // 下偏移高德logo以隐藏
        settings.setZoomControlsEnabled(false); // 不显示缩放控件
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isRequestPermission()) {
            PermissionFragment fragment = new PermissionFragment();
            switchFragment(fragment, false);
        } else {
            switchFragment(mMainFragment, true);
        }
    }

    private boolean isRequestPermission() {
        SharedPreferences sp = getSharedPreferences("launchData", MODE_PRIVATE);
        return sp.getBoolean("isRequestPermission", false);
    }

    public void initFragment() {
        mMainFragment = new MainFragment();
        mRouteFragment = new RouteFragment();
        mSearchFragment = new SearchFragment();
    }

    private void switchFragment(Fragment target, boolean pushStack) {
        // 当前Fragment是目标Fragment不作切换
        if (mCurrentFragment == target) {
            return;
        }

        mMap.clear();   // 清理地图
        resetMyLocationMap();    // 自动定位到我的位置
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 在主Fragment中显示比例尺，其他界面隐藏
        UiSettings settings = mMap.getUiSettings();
        if (target instanceof MainFragment) {
            settings.setScaleControlsEnabled(true);
        } else {
            settings.setScaleControlsEnabled(false);
        }

        transaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter,
                R.anim.fragment_slide_right_exit);
        if (!target.isAdded()) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            transaction.add(R.id.fragment_container, target).commit();
        } else {
            transaction.hide(mCurrentFragment).show(target).commit();
        }

        if (pushStack) {
            mFragmentBackStack.push(target);
        }
        mCurrentFragment = target;
    }

    public boolean hasLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    HintDialogFragment dialog = HintDialogFragment.newInstance("需要定位权限"
                            , "确定您的位置需要打开定位权限，是否打开？");
                    dialog.setPositiveButton("确定", v -> {
                        requestPermissions(new String[]{permission}, CODE_REQUEST_LOCATION);
                        dialog.dismiss();
                    }).show(getSupportFragmentManager(), "HintDialog");
                } else {
                    requestPermissions(new String[]{permission}, CODE_REQUEST_LOCATION);
                }
                return false;
            }
        }
        return true;
    }

    /*
    * 定位按钮点击时切换定位模式
    * */
    public void toggleMyLocationType() {
        // 检查定位权限
        if (!hasLocationPermission()) {
            return;
        }

        if (mMyLocation == null || !MapUtil.isOPenGps(getApplicationContext())) {
            showHintNeedLocation();
            return;
        }

        LatLng position = mMap.getCameraPosition().target;
        // 坐标精度
        int accuracy = 10000;
        long pLatitude = (long) (position.latitude * accuracy);
        long pLongitude = (long) (position.longitude * accuracy);
        long latitude = (long) (mMyLocation.getLatitude() * accuracy);
        long longitude = (long) (mMyLocation.getLongitude() * accuracy);

        // 判断相机和定位位置是否相同，若不同，相机对准定位点，不切换模式
        if (pLatitude == latitude && pLongitude == longitude) {
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

    public void showHintNeedLocation() {
        HintDialogFragment fragment = HintDialogFragment.newInstance("获取位置失败", "建议打开系统位置服务以提供精确的定位和导航服务");
        fragment.show(getSupportFragmentManager(), "HintDialog");
    }

    /*
    * 修改地图定位模式
    * */
    private void changeMyLocationType(int mode) {
        mMyLocationStyle.myLocationType(mode);
        mMap.setMyLocationStyle(mMyLocationStyle);
    }

    public void gotoFragment(int tag, Bundle data) {
        switch (tag) {
            case TAG_MAIN_FRAGMENT:
                switchFragment(mMainFragment, true);
                break;
            case TAG_ROUTE_FRAGMENT:
                mRouteFragment.setArguments(data);
                switchFragment(mRouteFragment, true);
                break;
            case TAG_SEARCH_FRAGMENT:
                switchFragment(mSearchFragment, true);
                break;
        }
    }

    public Location getMyLocation() {
        return mMyLocation;
    }

    /*
    * 地图重置定位
    * */
    public void resetMyLocationMap() {
        changeMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM)); // 相机视角缩放
    }

    public void showDrivingRoute(DrivePath path, LatLonPoint startPos, LatLonPoint endPos) {
        mMap.clear();
        changeMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        DrivingRouteOverlay overlay = new DrivingRouteOverlay(getApplicationContext(),
                mMap, path, startPos, endPos, null);
        overlay.setNodeIconVisibility(false);   // 不显示节点Marker
        overlay.setIsColorfulline(true);    // 以颜色展示交通拥堵情况
        overlay.removeFromMap();
        overlay.addToMap();
        overlay.zoomToSpan();
    }

    public void showLoading(String content) {
        mLoadingDialogFragment = LoadingDialogFragment.newInstance(content);
        mLoadingDialogFragment.show(getSupportFragmentManager(), "Loading");
    }

    public void dismissLoading() {
        mLoadingDialogFragment.dismiss();
    }

    public void back() {
        // 顶层Fragment出栈
        mFragmentBackStack.pop();
        switchFragment(mFragmentBackStack.peek(), false);
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (location.getExtras().getInt("errorCode") == 0) {
            if (mMyLocation == null) {
                resetMyLocationMap();
                mMyLocationStyle.showMyLocation(true);
            }
            mMyLocation = location;
        } else {
            if (mMyLocation != null) {
                mMyLocationStyle.showMyLocation(false);
            }
            mMyLocation = null;
        }
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
        if (mMyLocationStyle.getMyLocationType() != MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER) {
            changeMyLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        }
    }

    @Override
    public void onUp(float v, float v1) {

    }

    @Override
    public void onMapStable() {

    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - mLastBackTime < 2000) {
            super.onBackPressed();
        } else {
            if (mFragmentBackStack.size() == 1) {
                ToastUtil.showToast(this, "再按一次退出XMap");
                mLastBackTime = now;
            } else {
                back();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        HintDialogFragment dialog = HintDialogFragment.newInstance("需要定位权限"
                                , "请在[系统设置 > 应用 > XMap > 权限]中打开定位权限");
                        dialog.show(getSupportFragmentManager(), "HintDialog");
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}