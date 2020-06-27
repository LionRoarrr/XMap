package com.liangnie.xmap.fragments;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.liangnie.xmap.R;
import com.liangnie.xmap.activities.MainMapActivity;
import com.liangnie.xmap.adapters.PoiItemsAdapter;
import com.liangnie.xmap.listeners.OnLoadMoreListener;
import com.liangnie.xmap.views.LoadMoreListView;

public class RouteFragment extends Fragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        PoiSearch.OnPoiSearchListener,
        TextWatcher,
        OnLoadMoreListener,
        AdapterView.OnItemClickListener {
    private static final int SEARCH_PAGE_SIZE = 10;
    private static final int MODE_DRIVE = 100;
    private static final int MODE_BUS = 101;

//    private List<String> mRouteNames;   // 路径点名称列表
//    private List<LatLonPoint> mRoutePoint;   // 路径点经纬度列表
//    private RouteListViewAdapter mAdapter;   // ListView适配器
    private int mPlanMode;
    private Location mMyLocation;
    private PoiItem mStartPoi;  // 起点
    private PoiItem mEndPoi;    // 终点
    private int mCurrentPageNum;   // PoiSearch页码
    private PoiItemsAdapter mPoiItemsAdapter;
    private String mKeyWord;    // POI查询关键字

    private EditText mInputOrigin;  // 起点输入
    private EditText mInputDestination;  // 终点输入
    private LoadMoreListView mPoiItemListView;   // POI搜寻结果列表

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        mInputOrigin = view.findViewById(R.id.input_origin);
        mInputDestination = view.findViewById(R.id.input_destination);
//        mAddButton = view.findViewById(R.id.btn_add_route); // 添加路径点按钮
//        mRouteDivider = view.findViewById(R.id.route_divider);

        RadioGroup radioGroup = view.findViewById(R.id.plan_mode_rb_group);
        ImageButton btnBack = view.findViewById(R.id.btn_back); // 返回按钮
        ImageButton btnSwap = view.findViewById(R.id.btn_swap_route);   // 起点-终点交换按钮
        ImageButton btnFinish = view.findViewById(R.id.btn_finish);   // 路线规划按钮
        //    private ImageButton mAddButton; // 途径点添加按钮
        //    private LinearLayout mRouteDivider;
        //
        mPoiItemListView = view.findViewById(R.id.search_poi_item);
//        ListView listView = view.findViewById(R.id.route_list); // 途径点列表

//        mRouteNames = new LinkedList<>();
//        mAdapter = new RouteListViewAdapter(getActivity(), mRouteNames);
//        mAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                mAddButton.setEnabled(mAdapter.getCount() < 3);
//                mRouteDivider.setVisibility(mAdapter.getCount() > 0? View.GONE: View.VISIBLE);
//            }
//        });
        mPoiItemsAdapter = new PoiItemsAdapter(getActivity());
        mPlanMode = MODE_DRIVE;

        radioGroup.setOnCheckedChangeListener(this);
//        mAddButton.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSwap.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        mInputOrigin.setOnClickListener(this);
        mInputDestination.setOnClickListener(this);
//        listView.setAdapter(mAdapter);
        mPoiItemListView.setAdapter(mPoiItemsAdapter);
        mPoiItemListView.setOnLoadMoreListener(this);
        mPoiItemListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mCurrentPageNum = 1;
        mInputOrigin.addTextChangedListener(this);
        mInputDestination.addTextChangedListener(this);

        Bundle bundle = getArguments();
        if (null != bundle) {
            mMyLocation = bundle.getParcelable("MyLocation");
            mPoiItemsAdapter.setMyLocation(mMyLocation);
            LatLonPoint point = new LatLonPoint(mMyLocation.getLatitude(), mMyLocation.getLongitude());
            setStartPoi(new PoiItem("", point, "我的位置", ""));
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        setStartPoi(null);
        setEndPoi(null);
        mInputOrigin.removeTextChangedListener(this);
        mInputDestination.removeTextChangedListener(this);
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mInputOrigin.removeTextChangedListener(this);
        mInputDestination.removeTextChangedListener(this);
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
            case R.id.btn_finish:
                onButtonFinishClick();
                break;
            case R.id.input_origin:
            case R.id.input_destination:
                mCurrentPageNum = 1;
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_plan_drive:
//                mAddButton.setVisibility(View.VISIBLE);
                mPlanMode = MODE_DRIVE;
                break;
            case R.id.rb_plan_bus:
                // 清空途径点，隐藏途径点添加按钮
//                mRouteNames.clear();
//                mAdapter.notifyDataSetChanged();
//                mAddButton.setVisibility(View.GONE);
                mPlanMode = MODE_BUS;
                break;
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (poiResult.getPois().size() > 0) {
                for (PoiItem item: poiResult.getPois()) {
                    mPoiItemsAdapter.addItem(item);
                }
                mPoiItemsAdapter.notifyDataSetChanged();
            } else {
                Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
                toast.setText("没有更多了~");    // 解决带APP前缀的问题
                toast.show();
            }
            mPoiItemListView.loadMoreCompleted();
        } else {
            Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
            toast.setText("查询失败，请重试！");    // 解决带APP前缀的问题
            toast.show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mKeyWord = s.toString();
            mCurrentPageNum = 1;
            mPoiItemsAdapter.clear();
            mPoiItemsAdapter.notifyDataSetChanged();
            searchPoi(mKeyWord, mCurrentPageNum);
        }
    }

    @Override
    public void onLoadMore() {
        mCurrentPageNum++;
        searchPoi(mKeyWord, mCurrentPageNum);
    }

    private void onButtonFinishClick() {
        if (null != mStartPoi && null != mEndPoi) {
            switch (mPlanMode) {
                case MODE_DRIVE:
                    startPlanDrive();
                    break;
                case MODE_BUS:
                    break;
            }
        } else {
            Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
            toast.setText("无效的起点和终点");
            toast.show();
        }
    }

    private void startPlanDrive() {
        Poi start = new Poi(mStartPoi.getTitle()
                , new LatLng(mStartPoi.getLatLonPoint().getLatitude()
                , mStartPoi.getLatLonPoint().getLongitude())
                , mStartPoi.getPoiId());
        Poi end = new Poi(mEndPoi.getTitle()
                , new LatLng(mEndPoi.getLatLonPoint().getLatitude()
                , mEndPoi.getLatLonPoint().getLongitude())
                , mEndPoi.getPoiId());

        AmapNaviPage.getInstance().showRouteActivity(getActivity(), new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.ROUTE), null);
    }


    private void searchPoi(String keyword, int pageNum) {
        PoiSearch.Query query = new PoiSearch.Query(keyword, "");
        if (null != mMyLocation) {
            LatLonPoint point = new LatLonPoint(mMyLocation.getLatitude(), mMyLocation.getLongitude());
            query.setLocation(point);
        }

        query.setPageSize(SEARCH_PAGE_SIZE);
        query.setPageNum(pageNum);
        query.setDistanceSort(false);

        PoiSearch search = new PoiSearch(getActivity(), query);
        search.setOnPoiSearchListener(this);
        search.searchPOIAsyn();
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
        mInputOrigin.removeTextChangedListener(this);
        mInputOrigin.setText(text);
        mInputOrigin.addTextChangedListener(this);
    }

    private void setInputDestText(String text) {
        mInputDestination.removeTextChangedListener(this);
        mInputDestination.setText(text);
        mInputDestination.addTextChangedListener(this);
    }

    private void moveInputCursorToEnd() {
        if (mInputOrigin.hasFocus()) {
            mInputOrigin.setSelection(mInputOrigin.length());
        } else {
            mInputDestination.setSelection(mInputDestination.length());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mInputOrigin.hasFocus()) {
            setStartPoi((PoiItem) mPoiItemsAdapter.getItem(position));
            moveInputCursorToEnd();
        } else if (mInputDestination.hasFocus()) {
            setEndPoi((PoiItem) mPoiItemsAdapter.getItem(position));
            moveInputCursorToEnd();
        }
    }
}