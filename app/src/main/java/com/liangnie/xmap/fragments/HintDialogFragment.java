package com.liangnie.xmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.liangnie.xmap.R;

public class HintDialogFragment extends DialogFragment {
    private Button mPositiveButton;
    private Button mNegativeButton;
    private View.OnClickListener mPosiClickListener;
    private View.OnClickListener mNegaClickListener;

    private HintDialogFragment() {}

    public static HintDialogFragment newInstance(String title, String content) {
        HintDialogFragment fragment = new HintDialogFragment();
        Bundle data = new Bundle();
        data.putString("title", title);
        data.putString("content", content);
        fragment.setArguments(data);

        return fragment;
    }

    public HintDialogFragment setPositiveButton(String text, View.OnClickListener listener) {
        getArguments().putString("positiveButtonText", text);
        mPosiClickListener = listener;
        return this;
    }

    public HintDialogFragment setNegativeButton(String text, View.OnClickListener listener) {
        getArguments().putString("negativeButtonText", text);
        mNegaClickListener = listener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hint_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle data = getArguments();
        TextView title = view.findViewById(R.id.title);
        TextView content = view.findViewById(R.id.content);
        mPositiveButton = view.findViewById(R.id.positive_button);
        mNegativeButton = view.findViewById(R.id.negative_button);

        if (mPosiClickListener != null) {
            mPositiveButton.setOnClickListener(mPosiClickListener);
            mPositiveButton.setText(data.getString("positiveButtonText"));
            mPositiveButton.setVisibility(View.VISIBLE);
        }
        if (mNegaClickListener != null) {
            mNegativeButton.setOnClickListener(mNegaClickListener);
            mNegativeButton.setText(data.getString("negativeButtonText"));
            mNegativeButton.setVisibility(View.VISIBLE);
        }

        title.setText(data.getString("title"));
        content.setText(data.getString("content"));

        super.onViewCreated(view, savedInstanceState);
    }
}
