package com.liangnie.xmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.liangnie.xmap.R;
import com.liangnie.xmap.activities.MainMapActivity;

public class IndexFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab_location);
        ImageButton roadButton = view.findViewById(R.id.btn_road);
        LinearLayout searchView = view.findViewById(R.id.search_view);

        fab.setOnClickListener(this);
        roadButton.setOnClickListener(this);
        searchView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (null != activity) {
            switch (v.getId()) {
                case R.id.fab_location:
                    activity.toggleMyLocationType();
                    break;
                case R.id.btn_road:
                    activity.gotoFragment(MainMapActivity.TAG_ROUTE_FRAGMENT, null);
                    break;
                case R.id.search_view:
                    activity.gotoFragment(MainMapActivity.TAG_SEARCH_FRAGMENT, null);
                    break;
            }
        }
    }
}
