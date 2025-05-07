package com.example.lab5.widget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.lab5.util.StepUtils;

public class ResetStepsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        float currentSteps = StepUtils.getPrefs(context).getFloat("totalSteps", 0f);
        String currentDate = StepUtils.getCurrentDate();

        SharedPreferences.Editor editor = StepUtils.getPrefs(context).edit();
        editor.putFloat("previousSteps", currentSteps);
        editor.putString("lastDate", currentDate);
        editor.apply();

        StepUtils.updateWidget(context);
    }
}
