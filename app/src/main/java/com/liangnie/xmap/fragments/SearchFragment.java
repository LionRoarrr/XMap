package com.liangnie.xmap.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.liangnie.xmap.R;
import com.liangnie.xmap.activities.MainMapActivity;
import com.liangnie.xmap.adapters.HistorySearchListAdapter;
import com.liangnie.xmap.adapters.SearchListAdapter;
import com.liangnie.xmap.bean.HistorySearch;
import com.liangnie.xmap.utils.JsonUtil;
import com.liangnie.xmap.utils.StringUtil;
import com.liangnie.xmap.utils.ToastUtil;
import com.liangnie.xmap.views.LoadMoreListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener
        , PoiSearch.OnPoiSearchListener
        , SearchView.OnFocusChangeListener
        , LoadMoreListView.OnLoadMoreListener
        , View.OnClickListener {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int TYPE_HOTEL = 100;
    private static final int TYPE_MARKET = 101;
    private static final int TYPE_BANK = 102;

    private LoadMoreListView mResultListView;
    private SearchView mSearchView;
    private TextView mInputTv;  // SearchView控件里的输入控件
    private View mHistoryContainer;

    private String mKeyWord;
    private int mCurrentPage;
    private SearchListAdapter mSearchListAdapter;
    private HistorySearchListAdapter mHistoryListAdapter;
    private boolean mIsSearchNear = false;
    private int mNearType;
    private List<HistorySearch> mHistorySearches;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentPage = 1;
        mHistorySearches = readHistoryList();
        mSearchListAdapter = new SearchListAdapter(getActivity());
        mHistoryListAdapter = new HistorySearchListAdapter(getActivity(), mHistorySearches);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ImageButton backButton = view.findViewById(R.id.back_button);
        Button nearHotel = view.findViewById(R.id.near_hotel);
        Button nearMarket = view.findViewById(R.id.near_market);
        Button nearBank = view.findViewById(R.id.near_bank);
        mResultListView = view.findViewById(R.id.search_result);
        ListView historyListView = view.findViewById(R.id.search_history);
        mHistoryContainer = view.findViewById(R.id.history_search_container);
        View historyClear = view.findViewById(R.id.history_clear);
        mSearchView = view.findViewById(R.id.search_bar);
        // 拿到搜索框控件
        int tvId = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        mInputTv = mSearchView.findViewById(tvId);

        historyListView.setAdapter(mHistoryListAdapter);
        // 点击历史记录相当于点击了搜索按钮
        historyListView.setOnItemClickListener((parent, view1, position, id) -> {
            HistorySearch search = (HistorySearch) mHistoryListAdapter.getItem(position);
            mInputTv.setText(search.getContent());
            onQueryTextSubmit(search.getContent());
        });
        mResultListView.setAdapter(mSearchListAdapter);
        mResultListView.setOnLoadMoreListener(this);
        // 点击查询子项，跳转到路径规划
        mResultListView.setOnItemClickListener((parent, view1, position, id) -> viewRoute((PoiItem) mSearchListAdapter.getItem(position)));
        mSearchView.setOnQueryTextFocusChangeListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);

        backButton.setOnClickListener(v -> back());
        nearHotel.setOnClickListener(this);
        nearMarket.setOnClickListener(this);
        nearBank.setOnClickListener(this);
        historyClear.setOnClickListener(v -> clearHistoryList());

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            clearData();
        }
    }

    private void clearData() {
        mInputTv.setText("");
        mSearchListAdapter.clear();
        mSearchListAdapter.notifyDataSetChanged();
    }

    private void searchPoi(String keyWord) {
        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", "");
        query.setPageSize(DEFAULT_PAGE_SIZE);
        query.setPageNum(mCurrentPage);

        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null && activity.getMyLocation() != null) {
            Location location = activity.getMyLocation();
            LatLonPoint point = new LatLonPoint(location.getLatitude(), location.getLongitude());
            query.setLocation(point);
            query.setDistanceSort(false);
        } else {
            query.setCityLimit(true);
        }

        PoiSearch search = new PoiSearch(getActivity(), query);
        search.setOnPoiSearchListener(this);
        search.searchPOIAsyn();
    }

    private void searchNearPoi(int type) {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            if (!activity.hasLocationPermission()) {
                return;
            }

            Location location = activity.getMyLocation();
            if (location == null) {
                activity.showHintNeedLocation();
                return;
            }

            String poiType = "100100";
            switch (type) {
                case TYPE_HOTEL:
                    poiType = "100100";
                    break;
                case TYPE_MARKET:
                    poiType = "060400";
                    break;
                case TYPE_BANK:
                    poiType = "160100";
                    break;
            }
            PoiSearch.Query query = new PoiSearch.Query("", poiType, location.getExtras().getString("City"));
            query.setPageSize(DEFAULT_PAGE_SIZE);
            query.setPageNum(mCurrentPage);
            query.setDistanceSort(true);

            // 设置周边1公里范围
            LatLonPoint myPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
            PoiSearch.SearchBound bound = new PoiSearch.SearchBound(myPoint, 5000);

            PoiSearch search = new PoiSearch(activity, query);
            search.setOnPoiSearchListener(this);
            search.setBound(bound);
            search.searchPOIAsyn();
        }
    }

    private void showHistory() {
        mHistoryContainer.setVisibility(View.VISIBLE);
    }

    private void hideHistory() {
        mHistoryContainer.setVisibility(View.GONE);
    }

    private void viewRoute(PoiItem item) {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            Bundle data = new Bundle();
            data.putParcelable("poiItem", item);
            activity.gotoFragment(MainMapActivity.TAG_ROUTE_FRAGMENT, data);
        }
    }

    private void clearSearchResult() {
        mCurrentPage = 1;
        mSearchListAdapter.clear();
        mSearchListAdapter.notifyDataSetChanged();
    }

    private void back() {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            activity.back();
        }
    }

    private List<HistorySearch> readHistoryList() {
        List<HistorySearch> searches = null;
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            SharedPreferences preferences = activity.getSharedPreferences("data", Context.MODE_PRIVATE);
            String data = preferences.getString("history_search", "[]");
            try {
                searches = JsonUtil.json2HsList(new JSONArray(data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return searches;
    }

    private void insertHistoryList(String content) {
        HistorySearch search = new HistorySearch();
        search.setContent(content);
        for (HistorySearch item: mHistorySearches) {
            if (item.getContent().equals(search.getContent())) {
                mHistorySearches.remove(item);
                break;
            }
        }
        mHistorySearches.add(0, search);
        saveHistoryList(mHistorySearches);
    }

    private void clearHistoryList() {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            mHistorySearches.clear();
            SharedPreferences.Editor editor = activity.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
            editor.putString("history_search", "[]");
            editor.apply();
            hideHistory();
        }
    }

    private void saveHistoryList(List<HistorySearch> list) {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            JSONArray array = JsonUtil.hsList2Json(list);
            SharedPreferences.Editor editor = activity.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
            editor.putString("history_search", array.toString());
            editor.apply();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!StringUtil.isEmptyOrNull(query.trim())) {
            mSearchListAdapter.showDistance(false);
            clearSearchResult();
            mIsSearchNear = false;
            mKeyWord = query.trim();
            mSearchView.clearFocus();
            insertHistoryList(mKeyWord);    // 新增历史记录
            searchPoi(mKeyWord);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (i == 1000) {
            if (!poiResult.getPois().isEmpty()) {
                mSearchListAdapter.addAll(poiResult.getPois());
                mSearchListAdapter.notifyDataSetChanged();
            } else if (mCurrentPage > 1) {
                ToastUtil.showToast(getActivity(), "没有更多了~");
            } else {
                ToastUtil.showToast(getActivity(), "搜索结果为空，换个关键词试试哦~");
            }
        } else {
            ToastUtil.showToast(getActivity(), "查询失败，请重试！");
        }
        mResultListView.loadMoreCompleted();
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (!mHistorySearches.isEmpty()) {
                showHistory();
                mHistoryListAdapter.notifyDataSetChanged();
            }
        } else {
            hideHistory();
        }
    }

    @Override
    public void onLoadMore() {
        mCurrentPage++;
        if (!mIsSearchNear) {
            searchPoi(mKeyWord);
        } else {
            searchNearPoi(mNearType);
        }
    }

    @Override
    public void onClick(View v) {
        mIsSearchNear = true;
        mSearchView.clearFocus();
        clearSearchResult();
        switch (v.getId()) {
            case R.id.near_hotel:
                mNearType = TYPE_HOTEL;
                mInputTv.setText("周边酒店");
                break;
            case R.id.near_market:
                mNearType = TYPE_MARKET;
                mInputTv.setText("周边超市");
                break;
            case R.id.near_bank:
                mNearType = TYPE_BANK;
                mInputTv.setText("周边银行");
                break;
        }
        mSearchListAdapter.showDistance(true);
        searchNearPoi(mNearType);
    }
}
