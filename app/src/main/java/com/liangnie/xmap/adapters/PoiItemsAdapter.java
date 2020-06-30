package com.liangnie.xmap.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.liangnie.xmap.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class PoiItemsAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<PoiItem> mList;
    private Location mMyLocation;   // 我的位置，计算与POI距离

    public PoiItemsAdapter(Context mContext) {
        this.mContext = mContext;
        mList = new LinkedList<>();
    }

    public PoiItemsAdapter(Context mContext, List<PoiItem> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setMyLocation(Location mMyLocation) {
        this.mMyLocation = mMyLocation;
    }

    private String compDistanceToString(PoiItem item) {
        float distance = -1f;
        String result = "";

        if (null != mMyLocation) {
            LatLng a = new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude());
            LatLng b = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
            distance = AMapUtils.calculateLineDistance(a, b);
        }

        if (distance > 1000.0) {
            float compDistance = distance / 1000f;
            result = String.format(Locale.CHINA,"%.2f km", compDistance);
        } else if (distance >= 0) {
            result = String.format(Locale.CHINA,"%.0f m", distance);
        }

        return result;
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
        ViewHolder viewHolder;

        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_poi, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.snippet = convertView.findViewById(R.id.snippet);
            viewHolder.adName = convertView.findViewById(R.id.ad_name);
            viewHolder.distance = convertView.findViewById(R.id.distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PoiItem item = mList.get(position);
        viewHolder.title.setText(item.getTitle());  // POI名称
        viewHolder.snippet.setText(item.getSnippet());   // POI地址
        viewHolder.adName.setText(item.getAdName());
        viewHolder.distance.setText(compDistanceToString(item)); // POI距离

        return convertView;
    }

    public void addItem(PoiItem item) {
        mList.add(item);
    }

    public void clear() {
        mList.clear();
    }

    private static class ViewHolder {
        public TextView title;
        public TextView snippet;
        public TextView adName;
        public TextView distance;
    }
}
