package com.example.lab4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lab4.fragments.SpinnerFragment;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;


public class MainActivity extends AppCompatActivity {

    ChipNavigationBar navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_AUDIO
                    },
                    1);
        }

        navigation = findViewById(R.id.navigationBar);

        navigation.setOnItemSelectedListener(id -> {

            String selectedItem = "audio";
            if (id == R.id.audio_menu) {
                selectedItem = "audio";
            } else if (id == R.id.video_menu) {
                selectedItem = "video";
            }

            Bundle args = new Bundle();
            args.putString("selectedItem", selectedItem);

            SpinnerFragment spinnerFragment = new SpinnerFragment();
            spinnerFragment.setArguments(args);
            selectFragment(spinnerFragment);
        });
    }
    private void selectFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.spinner_container, fragment)
                .commit();
    }
}