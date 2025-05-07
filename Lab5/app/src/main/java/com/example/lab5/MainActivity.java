package com.example.lab5;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lab5.fragments.ScoreSelectorFragment;
import com.example.lab5.util.StepUtils;
import com.example.lab5.widget.receiver.ResetStepsReceiver;
import com.example.lab5.widget.service.StepCounterService;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class MainActivity extends AppCompatActivity implements SensorEventListener, ScoreSelectorFragment.OnScoreSelectedListener {

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorAvailable = false;
    private float totalSteps = 0f, previousSteps = 0f;
    private int stepGoal;
    private String currentDate;
    private CircularProgressIndicator circularProgress;
    private TextView stepCountText, distanceText, caloriesText, scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.ACTIVITY_RECOGNITION
                    }, 1);
        } else {
            startStepService();
        }


        stepCountText = findViewById(R.id.step_count_text);
        distanceText = findViewById(R.id.distance_text);
        caloriesText = findViewById(R.id.calories_text);
        scoreText = findViewById(R.id.score_text);
        circularProgress = findViewById(R.id.circular_progress);

        stepGoal = StepUtils.getStepGoal(this);
        currentDate = StepUtils.getCurrentDate();

        String lastDate = StepUtils.getPrefs(this).getString("lastDate", "");
        if (!currentDate.equals(lastDate)) {
            totalSteps = 0;
            resetSteps();
        } else {
            previousSteps = StepUtils.getPreviousSteps(this);
            totalSteps = previousSteps;
        }

        updateProgressUI();
        scheduleDailyResetAlarm();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        isSensorAvailable = (stepSensor != null);

        if (!isSensorAvailable) {
            stepCountText.setText("The step sensor is not supported on this device");
            distanceText.setVisibility(View.GONE);
            caloriesText.setVisibility(View.GONE);
        }

        Button getScore_btn = findViewById(R.id.score_btn);
        getScore_btn.setOnClickListener(v -> showScoreSelector());
    }

    private void scheduleDailyResetAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ResetStepsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    private void startStepService() {
        Intent serviceIntent = new Intent(this, StepCounterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void resetSteps() {
        previousSteps = totalSteps;
        StepUtils.saveStepData(this, totalSteps, currentDate);
        stepCountText.setText("Steps: 0");
        circularProgress.setProgress(0, stepGoal);
        distanceText.setText("Distance: 0.00 km");
        caloriesText.setText("Calories: 0.0 kcal");
    }

    private void updateProgressUI() {
        int steps = Math.round(totalSteps - previousSteps);
        int max = Math.max(stepGoal, steps);
        circularProgress.setProgress(steps, max);
        scoreText.setText("Goal: " + stepGoal);
    }

    private void showScoreSelector() {
        ScoreSelectorFragment fragment = new ScoreSelectorFragment();
        fragment.setListener(this);
        Bundle bundle = new Bundle();
        bundle.putInt("currentGoal", stepGoal);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "ScoreSelector");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            String today = StepUtils.getCurrentDate();
            if (!today.equals(currentDate)) {
                currentDate = today;
                resetSteps();
                return;
            }

            totalSteps = event.values[0];
            StepUtils.getPrefs(this).edit().putFloat("totalSteps", totalSteps).apply();
            int currentSteps = Math.round(totalSteps - previousSteps);

            stepCountText.setText("Steps: " + currentSteps);
            circularProgress.setProgress(currentSteps, Math.max(stepGoal, currentSteps));

            float distance = currentSteps * 0.0007f;
            float calories = currentSteps * 0.04f;
            distanceText.setText(String.format("Distance: %.2f km", distance));
            caloriesText.setText(String.format("Calories: %.1f kcal", calories));

            StepUtils.updateWidget(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onScoreSelected(int value) {
        StepUtils.saveStepGoal(this, value);
        stepGoal = value;
        updateProgressUI();
        StepUtils.updateWidget(this);
        Toast.makeText(this, "New goal set: " + value, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSensorAvailable) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isSensorAvailable) {
            sensorManager.unregisterListener(this);
        }
    }
}
