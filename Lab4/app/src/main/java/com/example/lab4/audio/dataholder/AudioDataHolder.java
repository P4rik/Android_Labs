package com.example.lab4.audio.dataholder;

import com.example.lab4.audio.model.AudioModel;

import java.util.ArrayList;

public class AudioDataHolder {
    private static ArrayList<AudioModel> audioList;

    public static void setAudioList(ArrayList<AudioModel> list) {
        audioList = list;
    }

    public static ArrayList<AudioModel> getAudioList() {
        return audioList;
    }
}