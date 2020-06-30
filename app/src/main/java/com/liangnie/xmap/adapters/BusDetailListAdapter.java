package com.liangnie.xmap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RailwayStationItem;
import com.amap.api.services.route.RouteBusLineItem;
import com.liangnie.xmap.R;
import com.liangnie.xmap.bean.SchemeBusStep;

import java.util.ArrayList;
import java.util.List;

public class BusDetailListAdapter extends BaseAdapter {

    private Context mContext;
    private List<SchemeBusStep> mList = new ArrayList<>();

    public BusDetailListAdapter(Context context, List<BusStep> list) {
        mContext = context;
        mList.add(new SchemeBusStep(null));
        for (BusStep step: list) {
            if (step.getWalk() != null) {
                SchemeBusStep walk = new SchemeBusStep(step);
                walk.setIsWalk(true);
                mList.add(walk);
            }
            if (step.getBusLines() != null && !step.getBusLines().isEmpty()) {
                SchemeBusStep bus = new SchemeBusStep(step);
                bus.setIsBus(true);
                mList.add(bus);
            }
            if (step.getRailway() != null) {
                SchemeBusStep railway = new SchemeBusStep(step);
                railway.setIsRailway(true);
                mList.add(railway);
            }
            if (step.getTaxi() != null) {
                SchemeBusStep taxi = new SchemeBusStep(step);
                taxi.setIsTaxi(true);
                mList.add(taxi);
            }
        }
        mList.add(new SchemeBusStep(null));
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bus_detail_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        onBindViewHolder(holder, position);

        return convertView;
    }

    private void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.actionImage.setImageResource(R.drawable.dir_start);
            holder.busLineName.setText("出发");
        } else if (position == mList.size() - 1) {
            holder.actionImage.setImageResource(R.drawable.dir_end);
            holder.busLineName.setText("到达目的地");
        } else {
            SchemeBusStep item = mList.get(position);

            if (item.isWalk()) {
                int distance = (int) item.getWalk().getDistance();
                if (distance > 1) {
                    holder.actionImage.setImageResource(R.drawable.dir13);
                    holder.busLineName.setText("步行" + distance + "米");
                } else {
                    holder.actionImage.setImageResource(R.drawable.swap_station);
                    holder.busLineName.setText("同站换乘");
                }
            } else if (item.isBus()) {
                holder.actionImage.setImageResource(R.drawable.dir14);
                holder.busLineName.setText(item.getBusLines().get(0).getBusLineName());
                holder.stationNum.setText((item.getBusLines().get(0).getPassStationNum() + 1) + "站");
                holder.position = position;
                ExpandClick expandClick = new ExpandClick(holder, item);
                holder.stationNum.setOnClickListener(expandClick);
            } else if (item.isRailway()) {
                holder.actionImage.setImageResource(R.drawable.dir16);
                holder.busLineName.setText(item.getRailway().getName());
                holder.stationNum.setText((item.getRailway().getViastops().size() + 1) + "站");
                holder.position = position;
                ExpandClick expandClick = new ExpandClick(holder, item);
                holder.stationNum.setOnClickListener(expandClick);
            } else if (item.isTaxi()) {
                holder.actionImage.setImageResource(R.drawable.dir14);
                holder.busLineName.setText("打车到目的地");
            }
        }
    }

    private class ViewHolder {
        ImageView actionImage;
        TextView busLineName;
        TextView stationNum;
        LinearLayout expand;
        boolean isExpand = false;
        int position;

        public ViewHolder(View view) {
            actionImage = view.findViewById(R.id.action_icon);
            busLineName = view.findViewById(R.id.bus_line_name);
            stationNum = view.findViewById(R.id.station_num);
            expand = view.findViewById(R.id.expand);
        }
    }

    private class ExpandClick implements View.OnClickListener {
        private ViewHolder holder;
        private SchemeBusStep busStep;

        public ExpandClick(ViewHolder holder, SchemeBusStep busStep) {
            this.holder = holder;
            this.busStep = busStep;
        }

        @Override
        public void onClick(View v) {
            busStep = mList.get(holder.position);
            if (busStep.isBus()) {
                if (!holder.isExpand) {
                    holder.isExpand = true;

                    RouteBusLineItem busLineItem = busStep.getBusLines().get(0);
                    addBusStation(busLineItem.getDepartureBusStation());    // 出发站
                    for (BusStationItem station: busLineItem.getPassStations()) {
                        addBusStation(station); // 途径站
                    }
                    addBusStation(busStep.getBusLines().get(0).getArrivalBusStation());  // 目的站
                } else {
                    holder.isExpand = false;
                    holder.expand.removeAllViews();
                }
            } else if (busStep.isRailway()) {
                if (!holder.isExpand) {
                    holder.isExpand = true;

                    addRailwayStation(busStep.getRailway().getDeparturestop());    // 出发站
                    for (RailwayStationItem station: busStep.getRailway().getViastops()) {
                        addRailwayStation(station); // 途径站
                    }
                    addRailwayStation(busStep.getRailway().getArrivalstop());    // 出发站
                } else {
                    holder.isExpand = false;
                    holder.expand.removeAllViews();
                }
            }
        }

        private void addBusStation(BusStationItem item) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_bus_detail_list_ex, null);
            TextView tv = ll.findViewById(R.id.bus_line_name);
            tv.setText(item.getBusStationName());
            holder.expand.addView(ll);
        }

        private void addRailwayStation(RailwayStationItem item) {
            LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_bus_detail_list_ex, null);
            TextView tv = ll.findViewById(R.id.bus_line_name);
            tv.setText(item.getName());
            holder.expand.addView(ll);
        }
    }
}
