package com.example.lab5.widget.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.lab5.R;
import com.example.lab5.util.StepUtils;


public class StepCounterService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private float totalSteps = 0f;
    private String currentDate;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, createNotification());

        currentDate = StepUtils.getCurrentDate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        StepUtils.getPreviousSteps(this);
        totalSteps = StepUtils.getPrefs(this).getFloat("totalSteps", 0f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            float stepsFromSensor = event.values[0];
            String today = StepUtils.getCurrentDate();

            if (!today.equals(currentDate)) {
                currentDate = today;
                StepUtils.saveStepData(this, stepsFromSensor, currentDate);
            }

            totalSteps = stepsFromSensor;
            StepUtils.getPrefs(this).edit().putFloat("totalSteps", totalSteps).apply();

            StepUtils.updateWidget(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "StepCounterChannel",
                    "Step Counter Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, "StepCounterChannel")
                .setContentTitle("Step Counter")
                .setContentText("Tracking your steps")
                .setSmallIcon(R.drawable.footprint_ic)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
}
