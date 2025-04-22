package com.example.lab4.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lab4.R;
import com.skydoves.powerspinner.PowerSpinnerView;

public class SpinnerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spinner, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String selectedArgs = args.getString("selectedItem", "audio");
        Log.d("SELECTED ITEM", selectedArgs);

        PowerSpinnerView fileSpinner = view.findViewById(R.id.file_spinner);

        if ("audio".equals(selectedArgs)) {
            fileSpinner.setBackgroundResource(R.drawable.audio_rounded_spinner);
            fileSpinner.setSpinnerPopupBackground(
                    ContextCompat.getDrawable(requireContext(), R.drawable.spinner_selected_item_audio)
            );
            fileSpinner.setSpinnerSelectedItemBackground(
                    ContextCompat.getDrawable(requireContext(), R.drawable.spinner_selected_item_audio)
            );
        } else if ("video".equals(selectedArgs)) {
            fileSpinner.setBackgroundResource(R.drawable.video_rounded_spinner);
            fileSpinner.setSpinnerPopupBackground(
                    ContextCompat.getDrawable(requireContext(), R.drawable.spinner_selected_item_video)
            );
            fileSpinner.setSpinnerSelectedItemBackground(
                    ContextCompat.getDrawable(requireContext(), R.drawable.spinner_selected_item_video)
            );
        }

        fileSpinner.setOnSpinnerItemSelectedListener((spinnerView, position, selectedItemText, selectedText) -> {
            Fragment fragmentToLoad = null;
            Bundle bundle = new Bundle();
            bundle.putString("selectedItem", selectedArgs);

            if (selectedText.equals("Internal Storage")) {
                fragmentToLoad = new InternalFragment();
            } else if (selectedText.equals("External Storage")) {
                fragmentToLoad = new ExternalFragment();
            } else if (selectedText.equals("From Internet")) {
                fragmentToLoad = new InternetFragment();
            }

            if (fragmentToLoad != null) {
                fragmentToLoad.setArguments(bundle);
                replaceFragment(fragmentToLoad);
            }
        });
        fileSpinner.post(() -> {
            fileSpinner.selectItemByIndex(0);
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        String direction = getArguments() != null ? getArguments().getString("selectedItem", "audio") : "audio";

        if (direction.equals("audio")) {
            transaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        } else {
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        PowerSpinnerView fileSpinner = getView().findViewById(R.id.file_spinner);
        if (fileSpinner != null) {
            fileSpinner.dismiss();
        }
    }
}
