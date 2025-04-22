package com.example.lab4.audio.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab4.R;
import com.example.lab4.audio.dataholder.AudioDataHolder;
import com.example.lab4.audio.model.AudioModel;

import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayerActivity extends AppCompatActivity {
    private ImageButton btnPlay, btnPause, btnPrevious, btnNext;
    private ImageView titleView;
    private TextView audioTitle, audioAuthor, currentDuration, endDuration;
    private MediaPlayer mediaPlayer;
    private SeekBar audioDuration;
    private ArrayList<AudioModel> audioList;
    private int currentIndex;
    private Handler updateHandler = new Handler();
    private Runnable updateSeekBar;
    private volatile boolean isUpdating = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_audio_player);

        audioList = AudioDataHolder.getAudioList();
        currentIndex = getIntent().getIntExtra("CURRENT_INDEX", 0);

        audioTitle = findViewById(R.id.audioTitle);
        audioAuthor = findViewById(R.id.audioAuthor);
        audioDuration = findViewById(R.id.seekBar);
        titleView = findViewById(R.id.titleView);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        currentDuration = findViewById(R.id.currentDuration);
        endDuration = findViewById(R.id.endDuration);

        btnPlay.setOnClickListener(v -> playPauseAudio());
        btnPause.setOnClickListener(v -> playPauseAudio());
        btnPrevious.setOnClickListener(v -> playPreviousAudio());
        btnNext.setOnClickListener(v -> playNextAudio());

        audioTitle.setSelected(true);
        audioAuthor.setSelected(true);

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int current = mediaPlayer.getCurrentPosition();
                    currentDuration.setText(formatDuration(current));
                    audioDuration.setProgress(current);
                }
                updateHandler.postDelayed(this, 1000);
            }
        };

        setupMediaPlayer();

        audioDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        new Thread(() -> {
            while (isUpdating) {
                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int current = mediaPlayer.getCurrentPosition();
                        runOnUiThread(() -> {
                            currentDuration.setText(formatDuration(current));
                            audioDuration.setProgress(current);
                        });
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    break;
                }
            }
        }).start();
    }

    private void setupMediaPlayer() {
        AudioModel playAudio = audioList.get(currentIndex);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(playAudio.getPath());
            mediaPlayer.setOnPreparedListener(mp -> {
                audioDuration.setMax(mp.getDuration());
                endDuration.setText(formatDuration(mp.getDuration()));
                playPauseAudio();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        audioTitle.setText(playAudio.getAudioName());
        audioAuthor.setText(playAudio.getAudioAuthor());

        if (playAudio.getBitmap() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    playAudio.getBitmap(),
                    0,
                    playAudio.getBitmap().length
            );
            titleView.setImageBitmap(bitmap);
            titleView.clearColorFilter();
        } else {
            titleView.setImageResource(R.drawable.ic_audio_player);
        }
    }

    private void playPauseAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);
            } else {
                mediaPlayer.start();
                btnPlay.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
            }
        }
    }

    private void playNextAudio() {
        if (currentIndex < audioList.size() - 1) {
            currentIndex++;
            setupMediaPlayer();
        }
    }

    private void playPreviousAudio() {
        if (currentIndex > 0) {
            currentIndex--;
            setupMediaPlayer();
        }
    }

    private void pauseIfPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String formatDuration(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateSeekBar);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();
        updateHandler.post(updateSeekBar);

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            btnPlay.setVisibility(View.GONE);
            btnPause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isUpdating = false;
        pauseIfPlaying();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}
