package com.liangnie.xmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.liangnie.xmap.R;
import com.liangnie.xmap.utils.MapUtil;

import java.util.List;

public class BusPathListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BusPath> mList;

    public BusPathListAdapter(Context context, List<BusPath> list) {
        mContext = context;
        mList = list;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bus_path_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        onBindViewHolder(holder, position);

        return convertView;
    }

    private void onBindViewHolder(ViewHolder holder, int position) {
        BusPath path = mList.get(position);
        holder.pathTitle.setText(MapUtil.getBusPathTitle(path));
        holder.pathDes.setText(MapUtil.getBusPathDes(path));
    }

    static class ViewHolder {
        TextView pathTitle;
        TextView pathDes;

        public ViewHolder(View view) {
            pathTitle = view.findViewById(R.id.path_title);
            pathDes = view.findViewById(R.id.path_des);
        }
    }
}
