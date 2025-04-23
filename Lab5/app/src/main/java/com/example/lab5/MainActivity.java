package com.example.lab5;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.lab5.fragments.ScoreSelectorFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity implements SensorEventListener, ScoreSelectorFragment.OnScoreSelectedListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorAvailable = false;
    private float totalSteps = 0f;
    private float previousSteps = 0f;
    private CircularProgressIndicator circularProgress;
    private  int stepGoal = 10000;
    private TextView stepCountText, distanceText, caloriesText, scoreText;
    private String currentDate;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACTIVITY_RECOGNITION,
                    },
                    1);
        }

        stepCountText = findViewById(R.id.step_count_text);
        distanceText = findViewById(R.id.distance_text);
        caloriesText = findViewById(R.id.calories_text);
        scoreText = findViewById(R.id.score_text);
        circularProgress = findViewById(R.id.circular_progress);
        circularProgress.setProgress(0, stepGoal);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        stepGoal = sharedPreferences.getInt("stepGoal", 10000);

        currentDate = getCurrentDate();
        String lastDate = sharedPreferences.getString("lastDate", "");

        if (!currentDate.equals(lastDate)) {
            totalSteps = 0;
            resetSteps();
        } else {
            previousSteps = sharedPreferences.getFloat("previousSteps", 0f);
            totalSteps = previousSteps;
        }

        circularProgress.setProgress(0, stepGoal);

        int initialSteps = Math.round(totalSteps - previousSteps);
        int initialMax = Math.max(stepGoal, initialSteps);
        circularProgress.setProgress(initialSteps, initialMax);

        scoreText.setText("Score: " + stepGoal);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        isSensorAvailable = (stepSensor != null);

        if (!isSensorAvailable) {
            stepCountText.setText("The step sensor is not supported on this device");
            distanceText.setVisibility(View.GONE);
            caloriesText.setVisibility(View.GONE);

        }

        Button getScore_btn = findViewById(R.id.score_btn);
        getScore_btn.setOnClickListener(view1 -> {
            ScoreSelectorFragment scoreSelectorFragment = new ScoreSelectorFragment();
            scoreSelectorFragment.setListener(MainActivity.this);

            Bundle bundle = new Bundle();
            bundle.putInt("currentGoal", stepGoal);
            scoreSelectorFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            scoreSelectorFragment.show(fragmentManager, "TEST");
        });

    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void resetSteps() {
        previousSteps = totalSteps;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("previousSteps", previousSteps);
        editor.putString("lastDate", currentDate);
        editor.apply();

        stepCountText.setText("Steps: 0");
        circularProgress.setProgress(0, stepGoal);
        distanceText.setText("Distance: 0.00 km");
        caloriesText.setText("Calories: 0.0 kcal");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String today = getCurrentDate();

            if (!today.equals(currentDate)) {
                currentDate = today;
                resetSteps();
                return;
            }

            totalSteps = event.values[0];
            int currentSteps = Math.max(0, Math.round(totalSteps - previousSteps));

            stepCountText.setText("Steps: " + currentSteps);
            int maxProgress = Math.max(stepGoal, currentSteps);
            circularProgress.setProgress(currentSteps, maxProgress);

            float distanceMeters = currentSteps * 0.0007f;
            float calories = currentSteps * 0.04f;

            distanceText.setText(String.format("Distance: %.2f km", distanceMeters));
            caloriesText.setText(String.format("Calories: %.1f kcal", calories));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onScoreSelected(int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepGoal", value);
        editor.apply();

        stepGoal = value;
        Toast.makeText(this, "New goal: " + value + " steps", Toast.LENGTH_SHORT).show();

        int currentSteps = Math.round(totalSteps - previousSteps);
        int maxProgress = Math.max(stepGoal, currentSteps);
        circularProgress.setProgress(currentSteps, maxProgress);

        scoreText.setText("Score: " + stepGoal);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String today = getCurrentDate();
        if (!today.equals(currentDate)) {
            currentDate = today;
            resetSteps();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSensorAvailable) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            stepCountText.setText("The step sensor is not supported on this device");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
    }
}