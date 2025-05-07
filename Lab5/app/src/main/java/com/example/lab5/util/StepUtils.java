package com.example.lab5.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.lab5.R;
import com.example.lab5.widget.StepsAppWidget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepUtils {

    private static final String PREFS_NAME = "StepPrefs";

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void saveStepData(Context context, float totalSteps, String currentDate) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putFloat("totalSteps", totalSteps);
        editor.putString("lastDate", currentDate);
        editor.putFloat("previousSteps", totalSteps);
        editor.apply();
    }

    public static float getPreviousSteps(Context context) {
        return getPrefs(context).getFloat("previousSteps", 0f);
    }

    public static int getStepGoal(Context context) {
        return getPrefs(context).getInt("stepGoal", 10000);
    }

    public static void saveStepGoal(Context context, int goal) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt("stepGoal", goal);
        editor.apply();
    }

    public static void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, StepsAppWidget.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.step_widget);

        float totalSteps = getPrefs(context).getFloat("totalSteps", 0f);
        float previousSteps = getPrefs(context).getFloat("previousSteps", 0f);
        int goal = getPrefs(context).getInt("stepGoal", 10000);

        int todaySteps = (int) (totalSteps - previousSteps);
        int percent = (int) ((todaySteps / (float) goal) * 100);

        views.setTextViewText(R.id.widget_steps, String.valueOf(todaySteps));
        views.setTextViewText(R.id.widget_goal, "/" + goal);
        views.setProgressBar(R.id.widget_progress, goal, todaySteps, false);
        views.setTextViewText(R.id.widget_percent, percent + "%");

        appWidgetManager.updateAppWidget(thisWidget, views);
    }
}
