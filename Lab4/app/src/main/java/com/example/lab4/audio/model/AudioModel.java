package com.example.lab4.audio.model;

import java.io.Serializable;

public class AudioModel implements Serializable {

    private final String audioName;
    private final String audioAuthor;
    private final String audioDuration;
    private final String path;
    public byte[] bitmap;

    public AudioModel(String audioName, String audioAuthor, String audioDuration, String path, byte[] bitmap) {
        this.audioName = audioName;
        this.audioAuthor = audioAuthor;
        this.audioDuration = audioDuration;
        this.path = path;
        this.bitmap = bitmap;
    }

    public String getAudioName() {
        return audioName;
    }

    public String getAudioAuthor() {
        return audioAuthor;
    }

    public String getAudioDuration() {
        long durationMillis = Long.parseLong(audioDuration);
        int minutes = (int) (durationMillis / 1000) / 60;
        int seconds = (int) (durationMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getPath() {
        return path;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

}
