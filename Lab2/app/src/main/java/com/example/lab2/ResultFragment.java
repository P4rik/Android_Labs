package com.example.lab2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ResultFragment extends Fragment {

    private static final String ARG_TEXT = "result_text";
    private static final String ARG_SIZE = "result_size";

    public static ResultFragment newInstance(String text, float textSize) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putFloat(ARG_SIZE, textSize);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = view.findViewById(R.id.textView);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

        if (getArguments() != null) {
            textView.setText(getArguments().getString(ARG_TEXT));
            textView.setTextSize(getArguments().getFloat(ARG_SIZE));
            textView.setVisibility(View.VISIBLE);
        }

        buttonCancel.setOnClickListener(v -> {
            ((MainActivity) getActivity()).clearForm();
        });
    }
}
