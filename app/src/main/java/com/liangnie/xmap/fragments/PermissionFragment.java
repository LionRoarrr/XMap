package com.liangnie.xmap.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liangnie.xmap.R;
import com.liangnie.xmap.activities.MainMapActivity;

public class PermissionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission, container, false);

        Button agree = view.findViewById(R.id.agree);
        Button deny = view.findViewById(R.id.deny);

        agree.setOnClickListener(v -> requestPermissions());
        deny.setOnClickListener(v -> {
            MainMapActivity activity = (MainMapActivity) getActivity();
            if (activity != null) {
                activity.finish();
            }
        });

        return view;
    }

    private void requestPermissions() {
        String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE
                ,Manifest.permission.ACCESS_FINE_LOCATION
                ,Manifest.permission.READ_PHONE_STATE};
        requestPermissions(permissions, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainMapActivity activity = (MainMapActivity) getActivity();
        if (activity != null) {
            SharedPreferences.Editor editor = activity.getSharedPreferences("launchData", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isRequestPermission", true);
            editor.apply();
            activity.gotoFragment(MainMapActivity.TAG_MAIN_FRAGMENT);
        }
    }
}
