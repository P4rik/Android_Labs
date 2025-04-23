package com.example.lab4.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab4.R;
import com.example.lab4.audio.adapter.AudioAdapter;
import com.example.lab4.audio.model.AudioModel;
import com.example.lab4.video.adapter.VideoAdapter;
import com.example.lab4.video.model.VideoModel;

import java.io.File;
import java.util.ArrayList;

public class ExternalFragment extends Fragment {

    ArrayList<AudioModel> audioList = new ArrayList<>();
    ArrayList<VideoModel> videoList = new ArrayList<>();

    TextView noAudio;
    TextView noVideo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_external_view, container, false);

        noAudio = view.findViewById(R.id.no_audio_external);
        noVideo = view.findViewById(R.id.no_video_external);


        Bundle args = getArguments();
        if (args != null) {
            String selectedItem = args.getString("selectedItem");

            View containerView = view.findViewById(R.id.external_container);

            int audioColor = ContextCompat.getColor(requireContext(), R.color.audio_recycler_bg);
            int videoColor = ContextCompat.getColor(requireContext(), R.color.video_recycler_bg);

            if ("audio".equals(selectedItem)) {
                animateBackgroundColor(containerView, videoColor, audioColor);
                loadAudioList(view);
            } else if ("video".equals(selectedItem)) {
                animateBackgroundColor(containerView, audioColor, videoColor);
                loadVideoList(view);
            }
        }

        return view;
    }

    private void loadAudioList(View view) {
        RecyclerView audioView = view.findViewById(R.id.audio_external_recycler);
        RecyclerView videoView = view.findViewById(R.id.video_external_recycler);
        ProgressBar loadingSpinner = view.findViewById(R.id.loadingSpinner);

        videoView.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);

        new Thread(() -> {
            String[] projection = {
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATA
            };

            audioList.clear();

            Cursor cursor = requireContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            System.out.println("SCANNING STORAGE FOR AUDIO STARTED");
            while (cursor.moveToNext()) {
                String title = cursor.getString(0);
                String artist = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);

                byte[] embeddedPicture = null;
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(path);
                    embeddedPicture = retriever.getEmbeddedPicture();
                    retriever.release();
                } catch (Exception e) {
                    Log.w("ExternalFragment", "Failed to retrieve cover for: " + path, e);
                }
                AudioModel audio = new AudioModel(title, artist, duration, path, embeddedPicture);
                audioList.add(audio);
            }
            cursor.close();

            requireActivity().runOnUiThread(() -> {
                AudioAdapter audioAdapter = new AudioAdapter(requireContext(), audioList);


                if (audioList.isEmpty()) {
                    noAudio.setVisibility(View.VISIBLE);
                } else {
                    audioView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    audioView.setAdapter(audioAdapter);
                }
                loadingSpinner.setVisibility(View.GONE);
            });
        }).start();
    }

    private void loadVideoList(View view) {
        RecyclerView audioView = view.findViewById(R.id.audio_external_recycler);
        RecyclerView videoView = view.findViewById(R.id.video_external_recycler);
        ProgressBar loadingSpinner = view.findViewById(R.id.loadingSpinner);
        VideoAdapter videoAdapter = new VideoAdapter(getContext(), videoList);

        audioView.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);

        new Thread(() -> {
            String[] projection = {
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.DATA
            };

            String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";

            videoList.clear();

            Cursor cursor = getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, sortOrder);

            int count = 0;
            while (cursor.moveToNext() && count < 1) {
                String title = cursor.getString(0);
                String duration = cursor.getString(1);
                String path = cursor.getString(2);

                MediaMetadataRetriever mediaRetriever = new MediaMetadataRetriever();
                mediaRetriever.setDataSource(path);

                Bitmap preview = mediaRetriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST);

                String previewToUse = (title != null && !title.isEmpty()) ? title : new File(path).getName();
                VideoModel videoModel = new VideoModel(previewToUse, duration, path, preview);

                videoList.add(videoModel);
                count++;
            }
            cursor.close();

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {

                    if (videoList.isEmpty()) {
                        noVideo.setVisibility(View.VISIBLE);
                    } else {
                        videoView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        videoView.setAdapter(videoAdapter);
                    }
                    loadingSpinner.setVisibility(View.GONE);
                });
            }
        }).start();
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
