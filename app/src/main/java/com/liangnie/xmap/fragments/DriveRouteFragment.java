package com.liangnie.xmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.liangnie.xmap.R;
import com.liangnie.xmap.adapters.DrivingDetailListAdapter;
import com.liangnie.xmap.utils.MapUtil;

public class DriveRouteFragment extends Fragment implements View.OnClickListener,
        INaviInfoCallback {

    private DriveRouteResult mRouteResult;
    private PoiItem mStartPoi;
    private PoiItem mEndPoi;
    private DrivingDetailListAdapter mDrivingDetailListAdapter;

    private TextView mPlanTime;
    private TextView mPlanDistance;
    private TextView mTaxiCost;
    private ListView mDetailList;
    LinearLayout mViewDetailButton;
    LinearLayout mFoldDetailButton;
    LinearLayout mDetailBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drive_route, container, false);

        mPlanTime = view.findViewById(R.id.plan_time);
        mPlanDistance = view.findViewById(R.id.plan_distance);
        mTaxiCost = view.findViewById(R.id.taxi_cost);
        mDetailList = view.findViewById(R.id.detail_list);
        LinearLayout startNaviButton = view.findViewById(R.id.start_navi);
        mViewDetailButton = view.findViewById(R.id.view_detail);
        mFoldDetailButton = view.findViewById(R.id.fold_detail);
        mDetailBar = view.findViewById(R.id.route_detail_bar);

        startNaviButton.setOnClickListener(this);
        mViewDetailButton.setOnClickListener(this);
        mFoldDetailButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setData();
    }

    private void setData() {
        Bundle data = getArguments();
        if (data != null) {
            mRouteResult = data.getParcelable("DriveRouteResult");
            mStartPoi = data.getParcelable("StartPoi");
            mEndPoi = data.getParcelable("EndPoi");

            DrivePath path = mRouteResult.getPaths().get(0);
            mPlanTime.setText(MapUtil.getFriendlyTime((int) path.getDuration()));
            mPlanDistance.setText(MapUtil.getFriendlyLength((int) path.getDistance()));

            String costStr = "打车约" + mRouteResult.getTaxiCost() + "元";
            mTaxiCost.setText(costStr);

            mDrivingDetailListAdapter = new DrivingDetailListAdapter(getActivity(), mRouteResult.getPaths().get(0).getSteps());
            mDetailList.setAdapter(mDrivingDetailListAdapter);
        }
    }

    private void startNavigation() {
        Poi start = new Poi(mStartPoi.getTitle(), MapUtil.convertToLatLng(mStartPoi.getLatLonPoint()), mStartPoi.getPoiId());
        Poi end = new Poi(mEndPoi.getTitle(), MapUtil.convertToLatLng(mEndPoi.getLatLonPoint()), mEndPoi.getPoiId());

        AmapNaviPage.getInstance().showRouteActivity(getActivity(), new AmapNaviParams(start, null ,end, AmapNaviType.DRIVER, AmapPageType.NAVI), this);
    }

    private void viewRouteDetail() {
        hideSearchBar();
        showDetailList();
    }

    private void foldRouteDetail() {
        hideDetailList();
        showSearchBar();
    }

    private void showDetailList() {
        mDetailList.setVisibility(View.VISIBLE);    // 显示路径详情
        mViewDetailButton.setVisibility(View.GONE); // 隐藏显示详情按钮
        mFoldDetailButton.setVisibility(View.VISIBLE);  // 显示显示详情按钮
        mDetailBar.setBackgroundResource(R.color.white);    // RouteDetailBar上边圆角变直角
    }

    private void hideDetailList() {
        mDetailList.setVisibility(View.GONE);
        mViewDetailButton.setVisibility(View.VISIBLE);
        mFoldDetailButton.setVisibility(View.GONE);
        mDetailBar.setBackgroundResource(R.drawable.lrt_corner_shape);
    }

    private void showSearchBar() {
        RouteFragment fragment = (RouteFragment) getParentFragment();
        if (fragment != null) {
            fragment.showSearchBar();
        }
    }

    private void hideSearchBar() {
        RouteFragment fragment = (RouteFragment) getParentFragment();
        if (fragment != null) {
            fragment.hideSearchBar();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            setData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_navi:
                startNavigation();
                break;
            case R.id.view_detail:
                viewRouteDetail();
                break;
            case R.id.fold_detail:
                foldRouteDetail();
                break;
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }

    @Override
    public void onReCalculateRoute(int i) {

    }

    @Override
    public void onExitPage(int i) {

    }

    @Override
    public void onStrategyChanged(int i) {

    }

    @Override
    public View getCustomNaviBottomView() {
        return null;
    }

    @Override
    public View getCustomNaviView() {
        return null;
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onMapTypeChanged(int i) {

    }

    @Override
    public View getCustomMiddleView() {
        return null;
    }

    @Override
    public void onNaviDirectionChanged(int i) {

    }

    @Override
    public void onDayAndNightModeChanged(int i) {

    }

    @Override
    public void onBroadcastModeChanged(int i) {

    }

    @Override
    public void onScaleAutoChanged(boolean b) {

    }
}
