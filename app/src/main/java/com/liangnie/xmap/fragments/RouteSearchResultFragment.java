package com.liangnie.xmap.fragments;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.liangnie.xmap.R;
import com.liangnie.xmap.activities.MainMapActivity;
import com.liangnie.xmap.adapters.PoiItemsAdapter;
import com.liangnie.xmap.utils.StringUtil;
import com.liangnie.xmap.utils.ToastUtil;
import com.liangnie.xmap.views.LoadMoreListView;

public class RouteSearchResultFragment extends Fragment implements PoiSearch.OnPoiSearchListener,
        TextWatcher,
        LoadMoreListView.OnLoadMoreListener {
    private static final int SEARCH_PAGE_SIZE = 10;

    private LoadMoreListView mPoiItemListView;   // POI搜寻结果列表

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private PoiItemsAdapter mPoiItemsAdapter;   // 列表适配器
    private int mCurrentPageNum;   // PoiSearch页码
    private String mKeyWord;    // POI查询关键字

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resetPageNum();
        mPoiItemsAdapter = new PoiItemsAdapter(getActivity());
        mPoiItemsAdapter.setMyLocation(getMyLocation());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route_search_result, container, false);

        mPoiItemListView = view.findViewById(R.id.search_list);
        mPoiItemListView.setAdapter(mPoiItemsAdapter);
        mPoiItemListView.setOnLoadMoreListener(this);

        if (null != mOnItemClickListener) {
            mPoiItemListView.setOnItemClickListener(mOnItemClickListener);
        }

        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        mPoiItemsAdapter.clear();

        super.onViewStateRestored(savedInstanceState);
    }

    private Location getMyLocation() {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            return activity.getMyLocation();
        }
        return null;
    }

    public PoiItem getPoiItem(int position) {
        return (PoiItem) mPoiItemsAdapter.getItem(position);
    }

    public void resetPageNum() {
        mCurrentPageNum = 1;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void searchPoi(String keyword, int pageNum) {
        PoiSearch.Query query = new PoiSearch.Query(keyword, "");

        // 设置POI搜索位置原点
        Location location = getMyLocation();
        if (null != location) {
            LatLonPoint point = new LatLonPoint(location.getLatitude(), location.getLongitude());
            query.setLocation(point);
        }

        query.setPageSize(SEARCH_PAGE_SIZE);
        query.setPageNum(pageNum);
        query.setDistanceSort(false);

        PoiSearch search = new PoiSearch(getActivity(), query);
        search.setOnPoiSearchListener(this);
        search.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (!poiResult.getPois().isEmpty()) {
                if (!StringUtil.isEmptyOrNull(mKeyWord)) {
                    for (PoiItem item: poiResult.getPois()) {
                        mPoiItemsAdapter.addItem(item);
                    }
                    mPoiItemsAdapter.notifyDataSetChanged();
                }
            } else if (mCurrentPageNum > 1) {
                ToastUtil.showToast(getActivity(), "没有更多了~");
            }
            mPoiItemListView.loadMoreCompleted();
        } else {
            ToastUtil.showToast(getActivity(), "查询失败，请重试！");
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
        mPoiItemsAdapter.clear();
        mPoiItemsAdapter.notifyDataSetChanged();
        mKeyWord = s.toString();
        resetPageNum();
        if (!StringUtil.isEmptyOrNull(mKeyWord)) {
            searchPoi(mKeyWord, mCurrentPageNum);
        }
    }

    @Override
    public void onLoadMore() {
        mCurrentPageNum++;
        searchPoi(mKeyWord, mCurrentPageNum);
    }
}
