package com.liangnie.xmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.liangnie.xmap.R;
import com.liangnie.xmap.utils.MapUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<PoiItem> mList;
    private boolean mIsShowDistance = false;

    public SearchListAdapter(Context mContext) {
        this.mContext = mContext;
        this.mList = new ArrayList<>();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        bindViewHolder(holder, position);
        
        return convertView;
    }

    private void bindViewHolder(ViewHolder holder, int position) {
        PoiItem item = mList.get(position);
        holder.poiTitleTv.setText(item.getTitle());
        holder.poiProvCity.setText(String.format("%s·%s", item.getProvinceName(), item.getCityName()));
        holder.poiSnippetTv.setText(item.getSnippet());
        if (mIsShowDistance) {
            holder.poiTypeTv.setText(String.format("%s·%s", MapUtil.getFriendlyPoiType(item.getTypeDes()), MapUtil.getFriendlyLength(item.getDistance())));
        } else {
            holder.poiTypeTv.setText(MapUtil.getFriendlyPoiType(item.getTypeDes()));
        }
    }

    public void addAll(List<PoiItem> list) {
        mList.addAll(list);
    }

    public void clear() {
        mList.clear();
    }

    public void showDistance(boolean show) {
        mIsShowDistance = show;
    }

    private static class ViewHolder {
        TextView poiTitleTv;
        TextView poiTypeTv;
        TextView poiProvCity;
        TextView poiSnippetTv;

        public ViewHolder(View view) {
            poiTitleTv = view.findViewById(R.id.poi_title);
            poiTypeTv = view.findViewById(R.id.poi_type);
            poiProvCity = view.findViewById(R.id.poi_prov_city);
            poiSnippetTv = view.findViewById(R.id.poi_snippet);
        }
    }
}
