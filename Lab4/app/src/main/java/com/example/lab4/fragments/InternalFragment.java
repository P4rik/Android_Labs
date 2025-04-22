package com.example.lab4.fragments;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
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
import android.animation.ArgbEvaluator;

import com.example.lab4.R;
import com.example.lab4.audio.adapter.AudioAdapter;
import com.example.lab4.audio.model.AudioModel;
import com.example.lab4.video.adapter.VideoAdapter;
import com.example.lab4.video.model.VideoModel;

import java.io.File;
import java.util.ArrayList;

public class InternalFragment extends Fragment {
    ArrayList<AudioModel> audioList = new ArrayList<>();
    ArrayList<VideoModel> videoList = new ArrayList<>();
    TextView noAudio;
    TextView noVideo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internal_view, container, false);

        noAudio = view.findViewById(R.id.no_audio_internal);
        noVideo = view.findViewById(R.id.no_video_internal);

        Bundle args = getArguments();
        if (getArguments() != null) {
            String selectedItem = getArguments().getString("selectedItem");

            View containerView = view.findViewById(R.id.internal_container);

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
        RecyclerView audioView = view.findViewById(R.id.audio_internal_recycler);
        RecyclerView videoView = view.findViewById(R.id.video_internal_recycler);
        ProgressBar loadingSpinner = view.findViewById(R.id.loadingSpinner);

        videoView.setVisibility(View.GONE);
        loadingSpinner.setVisibility(View.VISIBLE);

        new Thread(() -> {
            File[] internalData = getContext().getFilesDir().listFiles();
            MediaMetadataRetriever mediaRetriever = new MediaMetadataRetriever();

            audioList.clear();

            for (File file : internalData) {
                if (audioFileCheck(file)) {
                    try {
                        String path = file.getAbsolutePath();
                        mediaRetriever.setDataSource(path);
                        String title = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String artist = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        String duration = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);


                        byte[] embeddedPicture = null;
                        try {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(path);
                            embeddedPicture = retriever.getEmbeddedPicture();
                            retriever.release();
                        } catch (Exception e) {
                            Log.w("ExternalFragment", "Failed to retrieve cover for: " + path, e);
                        }
                        audioList.add(new AudioModel(title, artist, duration, path, embeddedPicture));


                    } catch (Exception e) {
                        Log.e("InternalAudioFragment", "Error retrieving metadata for file: " + file.getName(), e);
                    }
                }
            }
            requireActivity().runOnUiThread(() -> {
                AudioAdapter audioAdapter = new AudioAdapter(getContext(), audioList);

                if (audioList.isEmpty()) {
                    noAudio.setVisibility(View.VISIBLE);
                } else {
                    audioView.setLayoutManager(new LinearLayoutManager(getContext()));
                    audioView.setAdapter(audioAdapter);
                }
                loadingSpinner.setVisibility(View.GONE);
            });
        }).start();
    }

    private void loadVideoList(View view) {
        RecyclerView videoView = view.findViewById(R.id.video_internal_recycler);
        RecyclerView audioView = view.findViewById(R.id.audio_internal_recycler);
        ProgressBar loadingSpinner = view.findViewById(R.id.loadingSpinner);

        loadingSpinner.setVisibility(View.VISIBLE);
        audioView.setVisibility(View.GONE);

        new Thread(() -> {
            File[] internalData = getContext().getFilesDir().listFiles();
            MediaMetadataRetriever mediaRetriever = new MediaMetadataRetriever();

            videoList.clear();

            for (File file : internalData) {
                if (videoFileCheck(file)) {
                    try {
                        mediaRetriever.setDataSource(file.getAbsolutePath());
                        String title = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String duration = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        Bitmap preview = mediaRetriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST);

                        String previewToUse = (title != null && !title.isEmpty()) ? title : file.getName();
                        VideoModel videoModel = new VideoModel(previewToUse, duration, file.getAbsolutePath(), preview);
                        videoList.add(videoModel);
                    } catch (Exception e) {
                        Log.e("InternalVideoFragment", "Error retrieving metadata for file: " + file.getName(), e);
                    }
                }
            }

            requireActivity().runOnUiThread(() -> {
                VideoAdapter videoAdapter = new VideoAdapter(getContext(), videoList);

                if (videoList.isEmpty()) {
                    noVideo.setVisibility(View.VISIBLE);
                } else {
                    videoView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                    videoView.setAdapter(videoAdapter);
                }
                loadingSpinner.setVisibility(View.GONE);
            });
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

    public static boolean audioFileCheck(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();

        String[] audioExtensions = {".mp3", ".wav", ".ogg", ".flac", ".aac", ".m4a", ".wma"};

        for (String ext : audioExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static boolean videoFileCheck(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();

        String[] videoExtensions = {".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".webm", ".3gp"};

        for (String ext : videoExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

}
