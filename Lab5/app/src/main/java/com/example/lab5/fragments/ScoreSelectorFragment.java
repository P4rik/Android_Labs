package com.example.lab5.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lab5.R;
import com.shawnlin.numberpicker.NumberPicker;

public class ScoreSelectorFragment extends DialogFragment {

    private OnScoreSelectedListener listener;

    public interface OnScoreSelectedListener {
        void onScoreSelected(int value);
    }

    public void setListener(OnScoreSelectedListener listener) {
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_selector, container, false);

        int currentGoal = getArguments() != null ? getArguments().getInt("currentGoal", 10000) : 10000;

        NumberPicker numberPicker = view.findViewById(R.id.number_picker);
        Button submit_btn = view.findViewById(R.id.submit_btn);

        int step = 100;
        int min = 1000;
        int max = 50000;
        int count = ((max - min) / step) + 1;

        String[] values = new String[count];
        for (int i = 0; i < count; i++) {
            values[i] = String.valueOf(min + (i * step));
        }

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(count - 1);
        numberPicker.setDisplayedValues(values);

        int currentIndex = (currentGoal - min) / step;
        numberPicker.setValue(currentIndex);

        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(10);
                }
            }
        });

        submit_btn.setOnClickListener(v -> {
            int selectedIndex = numberPicker.getValue();
            int selectedValue = Integer.parseInt(values[selectedIndex]);
            if (listener != null) {
                listener.onScoreSelected(selectedValue);
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
