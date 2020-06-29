package com.liangnie.xmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.route.DriveStep;
import com.liangnie.xmap.R;
import com.liangnie.xmap.utils.MapUtil;

import java.util.ArrayList;
import java.util.List;

public class DrivingDetailListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DriveStep> mList = new ArrayList<>();

    public DrivingDetailListAdapter(Context context ,List<DriveStep> list) {
        mContext = context;
        mList.add(new DriveStep()); // 起点
        mList.addAll(list);
        mList.add(new DriveStep()); // 终点
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return  mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.driving_detail_list_item, parent, false);
            viewHolder.actionIcon = convertView.findViewById(R.id.action_icon); // 路段动作
            viewHolder.upDivider = convertView.findViewById(R.id.up_divider);   // 上分隔
            viewHolder.lineName = convertView.findViewById(R.id.driving_line_name); // 路段内容
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DriveStep item = mList.get(position);
        if (position == 0) {
            viewHolder.actionIcon.setImageResource(R.drawable.dir_start);
            viewHolder.upDivider.setVisibility(View.GONE);
            viewHolder.lineName.setText("出发");
        } else if (position == mList.size() - 1) {
            viewHolder.actionIcon.setImageResource(R.drawable.dir_end);
            viewHolder.lineName.setText("到达目的地");
        } else {
            int actionId = MapUtil.getDriveActionID(item.getAction());
            viewHolder.actionIcon.setImageResource(actionId);
            viewHolder.lineName.setText(item.getInstruction());
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView lineName;
        public ImageView upDivider;
        public ImageView actionIcon;
    }
}
