package tl.com.weatherapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.modelnetwork.ModelNetwork;

import static tl.com.weatherapp.common.Common.ACTION_UPDATE_CONFIG_WEATHER;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {
    ModelNetwork modelNetwork;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        modelNetwork = ModelNetwork.getInstance();
        modelNetwork.create(context);
        // cap nhat toan bo widget
        for (int appWidgetId : appWidgetIds) {
            modelNetwork.updateWidgetAndInformation(appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        modelNetwork = ModelNetwork.getInstance();
        modelNetwork.create(context);
        if (intent.getAction().equals(ACTION_UPDATE_CONFIG_WEATHER)) {
            int appWidgetId = intent.getIntExtra(Common.INTENT_APP_WIDGET_ID, -1);
            if (appWidgetId != -1) {
                modelNetwork.updateWidgetAndInformation(appWidgetId);
            }
        }


    }

}

