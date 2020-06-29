package com.liangnie.xmap.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.liangnie.xmap.R;

public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {
    private int mTotalItemCount;    // 列表项数量
    private boolean mIsLoading; // 是否正在加载
    private OnLoadMoreListener mLoadMoreListener;   // 加载监听

    private View mFooterView;   // 列表底部布局

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mIsLoading = false;
        mFooterView = LayoutInflater.from(context).inflate(R.layout.loading_footer_view, null);
        setOnScrollListener(this);
    }

    public void loadMoreCompleted() {
        mIsLoading = false;
        removeFooterView(mFooterView);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mLoadMoreListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int lastVisibleIndex = view.getLastVisiblePosition();
        if (scrollState == SCROLL_STATE_IDLE && !mIsLoading && lastVisibleIndex == mTotalItemCount - 1) {
            mIsLoading = true;
            addFooterView(mFooterView);
            view.setSelection(view.getBottom());
            if (null != mLoadMoreListener) {
                mLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mTotalItemCount = totalItemCount;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
