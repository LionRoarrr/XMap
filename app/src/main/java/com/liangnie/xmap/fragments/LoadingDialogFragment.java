package com.liangnie.xmap.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.liangnie.xmap.R;

public class LoadingDialogFragment extends DialogFragment {
    private LoadingDialogFragment() {}

    public static LoadingDialogFragment newInstance(String content) {
        LoadingDialogFragment fragment =  new LoadingDialogFragment();
        Bundle data = new Bundle();
        data.putString("content", content);
        fragment.setArguments(data);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
        }
        return inflater.inflate(R.layout.fragment_loading_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String content = getArguments().getString("content");
        TextView tv = view.findViewById(R.id.content);
        tv.setText(content);

        super.onViewCreated(view, savedInstanceState);
    }
}
