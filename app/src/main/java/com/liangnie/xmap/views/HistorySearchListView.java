package com.liangnie.xmap.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.liangnie.xmap.R;

public class HistorySearchListView extends ListView {

    private View mFooterView;
    private OnClickListener listener;

    public HistorySearchListView(Context context) {
        super(context);
        initView(context);
    }

    public HistorySearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HistorySearchListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public HistorySearchListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        mFooterView = LayoutInflater.from(context).inflate(R.layout.footer_history_search_list, null);
        mFooterView.setOnClickListener(listener);
        addFooterView(mFooterView);
    }

    public void setClearHistoryClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
