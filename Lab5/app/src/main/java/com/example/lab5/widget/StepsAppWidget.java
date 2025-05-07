package com.example.lab5.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.lab5.MainActivity;
import com.example.lab5.R;

public class StepsAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences("StepPrefs", Context.MODE_PRIVATE);
        float currentSteps = prefs.getFloat("currentTotalSteps", 0);
        float previousSteps = prefs.getFloat("previousTotalSteps", 0);
        int goal = prefs.getInt("stepGoal", 10000);

        int todaySteps = (int)(currentSteps - previousSteps);
        updateWidgetViews(context, appWidgetManager, appWidgetIds, todaySteps, goal);
    }

    private void updateWidgetViews(Context context, AppWidgetManager appWidgetManager,
                                   int[] appWidgetIds, int todaySteps, int goal) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.step_widget);

            views.setTextViewText(R.id.widget_steps, String.valueOf(todaySteps));
            views.setTextViewText(R.id.widget_goal, "/" + goal);

            setClickIntent(context, views);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void setClickIntent(Context context, RemoteViews views) {
        Intent launchIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            updateAllWidgets(context);
        }
    }

    private void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, StepsAppWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
