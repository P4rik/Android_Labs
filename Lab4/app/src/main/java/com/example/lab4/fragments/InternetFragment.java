package com.example.lab4.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lab4.R;
import com.example.lab4.audio.activity.AudioPlayerActivity;
import com.example.lab4.audio.dataholder.AudioDataHolder;
import com.example.lab4.audio.model.AudioModel;
import com.example.lab4.video.activity.VideoPlayerActivity;
import com.example.lab4.video.model.VideoModel;

import java.util.ArrayList;

public class InternetFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_url_view, container, false);

        Bundle args = getArguments();
        if (getArguments() != null) {
            String selectedItem = getArguments().getString("selectedItem");

            View containerView = view.findViewById(R.id.internet_container);

            int audioColor = ContextCompat.getColor(requireContext(), R.color.audio_recycler_bg);
            int videoColor = ContextCompat.getColor(requireContext(), R.color.video_recycler_bg);

            if ("audio".equals(selectedItem)) {
                animateBackgroundColor(containerView, videoColor, audioColor);
                loadAudioUrl(view);
            } else if ("video".equals(selectedItem)) {
                animateBackgroundColor(containerView, audioColor, videoColor);
                loadVideoUrl(view);
            }
        }

        return view;
    }

    private void loadAudioUrl(View view) {
        LinearLayout audioContainer = view.findViewById(R.id.url_audio_container);
        EditText audioUrl = view.findViewById(R.id.url_input_audio);
        Button audioButton = view.findViewById(R.id.play_url_audio);

        audioContainer.setVisibility(View.VISIBLE);

        audioButton.setOnClickListener(v -> {
            String url = audioUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(getContext(), "URL is empty!", Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<AudioModel> tempList = new ArrayList<>();
            tempList.add(new AudioModel(
                    "Online Audio",
                    "Streaming",
                    "0",
                    url,
                    null
            ));

            AudioDataHolder.setAudioList(tempList);

            Intent intent = new Intent(getContext(), AudioPlayerActivity.class);
            intent.putExtra("CURRENT_INDEX", 0);
            startActivity(intent);
        });
    }

    private void loadVideoUrl(View view) {
        LinearLayout videoContainer = view.findViewById(R.id.url_video_container);
        EditText videoUrl = view.findViewById(R.id.url_input_video);
        Button videoButton = view.findViewById(R.id.play_url_video);

        videoContainer.setVisibility(View.VISIBLE);

        videoButton.setOnClickListener(v -> {
            String url = videoUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(getContext(), "URL is empty!", Toast.LENGTH_LONG).show();
                return;
            }

            VideoModel videoModel = new VideoModel(
                    "Online Video",
                    "0",
                    url,
                    null
            );

            Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
            intent.putExtra("videoPath", videoModel.getPath());
            startActivity(intent);
        });
    }

    private void animateBackgroundColor(View view, @ColorInt int fromColor, @ColorInt int toColor) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnimation.setDuration(800);
        colorAnimation.addUpdateListener(animator -> {
            view.setBackgroundColor((int) animator.getAnimatedValue());
        });
        colorAnimation.start();
    }
}