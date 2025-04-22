package com.example.lab4.video.model;

import android.graphics.Bitmap;

public class VideoModel {

    public String videoName;
    public String videoDuration;
    public String path;
    public Bitmap previewImage;


    public VideoModel(String videoName, String videoDuration, String path, Bitmap previewImage) {
        this.videoName = videoName;
        this.videoDuration = videoDuration;
        this.path = path;
        this.previewImage = previewImage;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoDuration() {
        long durationMillis = Long.parseLong(videoDuration);
        int minutes = (int) (durationMillis / 1000) / 60;
        int seconds = (int) (durationMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getPath() {
        return path;
    }

    public Bitmap getPreviewImage() {
        return previewImage;
    }
}



