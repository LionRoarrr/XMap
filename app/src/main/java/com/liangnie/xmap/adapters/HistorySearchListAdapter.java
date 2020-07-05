package com.liangnie.xmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liangnie.xmap.R;
import com.liangnie.xmap.bean.HistorySearch;

import java.util.List;

public class HistorySearchListAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<HistorySearch> mList;

    public HistorySearchListAdapter(Context mContext, List<HistorySearch> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history_search_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindViewHolder(holder, position);

        return convertView;
    }

    private void bindViewHolder(ViewHolder holder, int position) {
        holder.historyText.setText(mList.get(position).getContent());
    }

    private static class ViewHolder {
        TextView historyText;

        public ViewHolder(View view) {
            historyText = view.findViewById(R.id.history_text);
        }
    }
}
