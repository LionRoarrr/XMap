package com.liangnie.xmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.liangnie.xmap.R;
import com.liangnie.xmap.adapters.BusDetailListAdapter;
import com.liangnie.xmap.adapters.BusPathListAdapter;

public class BusRouteFragment extends Fragment implements ListView.OnItemClickListener {

    private BusRouteResult mResult;
    private BusPathListAdapter mPathAdapter;

    private FrameLayout mDetailView;
    private ListView mBusPathList;
    private ListView mBusDetailList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_route, container, false);

        mDetailView = view.findViewById(R.id.bus_path_detail_view);
        LinearLayout backButton = view.findViewById(R.id.back_button);
        mBusPathList = view.findViewById(R.id.bus_path_list);
        mBusDetailList = view.findViewById(R.id.bus_path_detail_list);

        mBusPathList.setOnItemClickListener(this);
        backButton.setOnClickListener(v -> hideBusDetail());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        setData();  // 每次显示该Fragment都设置数据并重新显示
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            setData();
        }
    }

    private void setData() {
        Bundle data = getArguments();
        if (data != null) {
            mResult = data.getParcelable("BusRouteResult");
            mPathAdapter = new BusPathListAdapter(getActivity(), mResult.getPaths());
            mBusPathList.setAdapter(mPathAdapter);
        }
    }

    private void showBusDetail(int position) {
        hideSearchBar();
        mDetailView.setVisibility(View.VISIBLE);
        BusPath path = (BusPath) mPathAdapter.getItem(position);
        BusDetailListAdapter adapter = new BusDetailListAdapter(getActivity(), path.getSteps());
        mBusDetailList.setAdapter(adapter);
    }

    private void hideBusDetail() {
        showSearchBar();
        mDetailView.setVisibility(View.GONE);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showBusDetail(position);
    }
}
