package com.liangnie.xmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.services.route.DriveStep;
import com.liangnie.xmap.R;
import com.liangnie.xmap.utils.MapUtil;

import java.util.ArrayList;
import java.util.List;

public class DrivingDetailListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DriveStep> mList = new ArrayList<>();

    public DrivingDetailListAdapter(Context context , List<DriveStep> list) {
        mContext = context;
        mList.add(new DriveStep()); // 起点
        mList.addAll(list);
        mList.add(new DriveStep()); // 终点
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DriveStep item = mList.get(position);
        if (position == 0) {
            holder.actionIcon.setImageResource(R.drawable.dir_start);
            holder.lineName.setText("出发");
        } else if (position == mList.size() - 1) {
            holder.actionIcon.setImageResource(R.drawable.dir_end);
            holder.lineName.setText("到达目的地");
        } else {
            int actionId = MapUtil.getDriveActionID(item.getAction());
            holder.actionIcon.setImageResource(actionId);
            holder.lineName.setText(item.getInstruction());
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_driving_detail_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        onBindViewHolder(holder, position);

        return convertView;
    }

    static class ViewHolder {
        TextView lineName;
        ImageView actionIcon;

        public ViewHolder(@NonNull View itemView) {
            actionIcon = itemView.findViewById(R.id.action_icon); // 路段动作
            lineName = itemView.findViewById(R.id.driving_line_name); // 路段内容
        }
    }
}
