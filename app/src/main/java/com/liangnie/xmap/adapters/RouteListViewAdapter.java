package com.liangnie.xmap.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

import com.liangnie.xmap.R;

import java.util.List;

public class RouteListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;

    public RouteListViewAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            viewHolder.watcher = new MyTextWatcher(position);

            convertView = LayoutInflater.from(mContext).inflate(R.layout.route_listview_item, parent, false);
            viewHolder.removeRouteButton = convertView.findViewById(R.id.btn_remove_route);
            viewHolder.inputRoute = convertView.findViewById(R.id.input_route);

            viewHolder.removeRouteButton.setOnClickListener(v -> {
                mList.remove(position);
                viewHolder.inputRoute.removeTextChangedListener(viewHolder.watcher);
                notifyDataSetChanged();
            });
            viewHolder.inputRoute.addTextChangedListener(viewHolder.watcher);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.updateWatcherPosition(position);
        }

        viewHolder.inputRoute.setText(mList.get(position));

        return convertView;
    }

    private final class ViewHolder {
        public ImageButton removeRouteButton;
        public EditText inputRoute;
        public MyTextWatcher watcher;

        public void updateWatcherPosition(int position) {
            watcher.updatePosition(position);
        }
    }

    private final class MyTextWatcher implements TextWatcher {
        private int mPosition;

        public MyTextWatcher(int position) {
            this.mPosition = position;
        }

        public void updatePosition(int position) {
            mPosition = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mList.set(mPosition, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
