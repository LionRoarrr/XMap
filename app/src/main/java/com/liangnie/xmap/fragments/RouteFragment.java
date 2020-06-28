package com.liangnie.xmap.fragments;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.liangnie.xmap.R;
import com.liangnie.xmap.activities.MainMapActivity;

public class RouteFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemClickListener,
        RouteSearch.OnRouteSearchListener,
        View.OnFocusChangeListener {

    private static final int MODE_DRIVE = 100;
    private static final int MODE_BUS = 101;

    private int mPlanMode;
    private RouteSearch mRouteSearch;
    private PoiItem mStartPoi;  // 起点
    private PoiItem mEndPoi;    // 终点
    private Fragment mCurrentFragment;  // 当前显示的Fragment
    private RouteSearchResultFragment mRouteResultFragment;
    private DriveRouteFragment mDriveRouteFragment;

    private EditText mInputOrigin;  // 起点输入
    private EditText mInputDestination;  // 终点输入

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlanMode = MODE_DRIVE;
        mRouteSearch = new RouteSearch(getActivity());
        mRouteSearch.setRouteSearchListener(this);

        initFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        mInputOrigin = view.findViewById(R.id.input_origin);
        mInputDestination = view.findViewById(R.id.input_destination);

        RadioGroup radioGroup = view.findViewById(R.id.plan_mode_rb_group);
        ImageButton btnBack = view.findViewById(R.id.btn_back); // 返回按钮
        ImageButton btnSwap = view.findViewById(R.id.btn_swap_route);   // 起点-终点交换按钮

        radioGroup.setOnCheckedChangeListener(this);
        btnBack.setOnClickListener(this);
        btnSwap.setOnClickListener(this);
        mInputOrigin.setOnClickListener(this);
        mInputDestination.setOnClickListener(this);
        mInputOrigin.setOnFocusChangeListener(this);
        mInputDestination.setOnFocusChangeListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        switchFragment(mRouteResultFragment);
        mInputOrigin.addTextChangedListener(mRouteResultFragment);
        mInputDestination.addTextChangedListener(mRouteResultFragment);

        MainMapActivity activity = (MainMapActivity) getActivity();
        if (null != activity) {
            Location myLocation = activity.getMyLocation();
            LatLonPoint point = new LatLonPoint(myLocation.getLatitude(), myLocation.getLongitude());
            setStartPoi(new PoiItem("", point, "我的位置", ""));
        }

        autoRequestFocus();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        setStartPoi(null);
        setEndPoi(null);
        mInputOrigin.removeTextChangedListener(mRouteResultFragment);
        mInputDestination.removeTextChangedListener(mRouteResultFragment);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mInputOrigin.removeTextChangedListener(mRouteResultFragment);
        mInputDestination.removeTextChangedListener(mRouteResultFragment);
    }

    private void initFragment() {
        mRouteResultFragment = new RouteSearchResultFragment();
        mRouteResultFragment.setOnItemClickListener(this);

        mDriveRouteFragment = new DriveRouteFragment();
    }

    private void switchFragment(Fragment target) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        if (target.isAdded()) {
            transaction.hide(mCurrentFragment).show(target).commit();
        } else if (null != mCurrentFragment) {
            transaction.hide(mCurrentFragment).add(R.id.fragment_container, target).commit();
        } else {
            transaction.add(R.id.fragment_container, target).commit();
        }

        mCurrentFragment = target;
    }

    public void backMainFragment() {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (null != activity) {
            activity.gotoFragment(MainMapActivity.TAG_MAIN_FRAGMENT);
        }
    }

    private void routeReverse() {
        // 起点-终点交换，若起点或终点未设置，则交换输入框文本
        if (null != mStartPoi && null != mEndPoi) {
            PoiItem temp = mStartPoi;
            setStartPoi(mEndPoi);
            setEndPoi(temp);
        } else if (null != mStartPoi) {
            PoiItem temp = mStartPoi;
            setStartPoi(null);  // 要把原交换点置null
            setInputOriginText(mInputDestination.getText().toString());
            setEndPoi(temp);
        } else if (null != mEndPoi) {
            PoiItem temp = mEndPoi;
            setEndPoi(null);    // 要把原交换点置null
            setInputDestText(mInputOrigin.getText().toString());
            setStartPoi(temp);
        }

        // 输入框焦点交换，并且光标移动到文本最后
        if (mInputOrigin.hasFocus()) {
            mInputOrigin.clearFocus();
            mInputDestination.requestFocus();
            moveInputCursorToEnd();
        } else if (mInputDestination.hasFocus()) {
            mInputDestination.clearFocus();
            mInputOrigin.requestFocus();
            moveInputCursorToEnd();
        }
    }

    /*
    * 输入框自动获取焦点
    * */
    private void autoRequestFocus() {
        if (mStartPoi == null) {
            mInputOrigin.requestFocus();
            moveInputCursorToEnd();
        } else if (mEndPoi == null) {
            mInputDestination.requestFocus();
            moveInputCursorToEnd();
        }
    }

    private void removeInputFocus() {
        mInputOrigin.clearFocus();
        mInputDestination.clearFocus();
    }

    private void setStartPoi(PoiItem item) {
        if (null != item) {
            setInputOriginText(item.getTitle());
        } else {
            setInputOriginText("");
        }
        mStartPoi = item;
    }

    private void setEndPoi(PoiItem item) {
        if (null != item) {
            setInputDestText(item.getTitle());
        } else {
            setInputDestText("");
        }
        mEndPoi = item;
    }

    private void setInputOriginText(String text) {
        mInputOrigin.removeTextChangedListener(mRouteResultFragment);
        mInputOrigin.setText(text);
        mInputOrigin.addTextChangedListener(mRouteResultFragment);
    }

    private void setInputDestText(String text) {
        mInputDestination.removeTextChangedListener(mRouteResultFragment);
        mInputDestination.setText(text);
        mInputDestination.addTextChangedListener(mRouteResultFragment);
    }

    private void moveInputCursorToEnd() {
        if (mInputOrigin.hasFocus()) {
            mInputOrigin.setSelection(mInputOrigin.length());
        } else {
            mInputDestination.setSelection(mInputDestination.length());
        }
    }

    private void startPlanDrive() {
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoi.getLatLonPoint(), mEndPoi.getLatLonPoint());
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.
                DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION, null, null, "");
        mRouteSearch.calculateDriveRouteAsyn(query);

        switchFragment(mDriveRouteFragment);
    }

    private void tryPlanRoute() {
        if (mStartPoi != null && mEndPoi != null) {
            switch (mPlanMode) {
                case MODE_DRIVE:
                    startPlanDrive();
                    break;
                case MODE_BUS:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                backMainFragment();
                break;
//            case R.id.btn_add_route:
//                addRoute();
//                break;
            case R.id.btn_swap_route:
                routeReverse();
                break;
            case R.id.input_origin:
            case R.id.input_destination:
                mRouteResultFragment.resetPageNum();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_plan_drive:
                mPlanMode = MODE_DRIVE;
                break;
            case R.id.rb_plan_bus:
                mPlanMode = MODE_BUS;
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mInputOrigin.hasFocus()) {
            setStartPoi(mRouteResultFragment.getPoiItem(position));
        } else if (mInputDestination.hasFocus()) {
            setEndPoi(mRouteResultFragment.getPoiItem(position));
        }
        removeInputFocus();
        autoRequestFocus();
        tryPlanRoute();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        if (i == 1000) {
            MainMapActivity activity = (MainMapActivity) getActivity();
            if (driveRouteResult != null && driveRouteResult.getPaths() != null && activity != null) {
                if (driveRouteResult.getPaths().size() > 0) {
                    LatLonPoint startPos = driveRouteResult.getStartPos();
                    LatLonPoint endPos = driveRouteResult.getTargetPos();
                    activity.showDrivingRoute(driveRouteResult.getPaths().get(0), startPos, endPos);
                }
            }
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // 输入框获得焦点
        if (hasFocus) {
            switchFragment(mRouteResultFragment);
        }
    }
}