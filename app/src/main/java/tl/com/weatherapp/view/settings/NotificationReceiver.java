package tl.com.weatherapp.view.settings;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;

import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.weather.Datum2;
import tl.com.weatherapp.model.weather.WeatherResult;
import tl.com.weatherapp.view.main.MainActivity;

public class NotificationReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Datum2 curHourlyWeather = getCurHourlyWeather(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String tempUnit = sharedPreferences.getString(context.getString(R.string.pref_temp_unit), context.getString(R.string.pref_temp_default_value));

        String strNotificationContent = " ";
        if (curHourlyWeather != null) {
            if (tempUnit.equals(context.getString(R.string.pref_temp_default_value))) {
                strNotificationContent = String.valueOf(Common.covertFtoC(curHourlyWeather.getTemperature())) + tempUnit + "  -  " +curHourlyWeather.getSummary();
            } else {
                strNotificationContent = String.valueOf(curHourlyWeather.getTemperature()) + tempUnit + "  -  " +curHourlyWeather.getSummary();
            }
        }

        Intent nIntent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2257, nIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Common.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cloud)
                .setContentTitle(strNotificationContent)
                .setContentText(context.getString(R.string.content_notification))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.content_notification)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(0, builder.build());

    }

    private Datum2 getCurHourlyWeather(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Common.DATA,Context.MODE_PRIVATE);
        String strWeather = sharedPreferences.getString(Common.SHARE_PREF_WEATHER_RESULT_KEY_IN_ADDRESS_ID + Common.CURRENT_ADDRESS_ID,"");
        Gson gson = new Gson();
        WeatherResult weatherResult = gson.fromJson(strWeather,WeatherResult.class);

        Calendar calendar = Calendar.getInstance();
        float x = calendar.getTimeInMillis() - weatherResult.getHourly().getData().get(0).getTime()*1000f;
        int curHourly =(int) (x / 3600000f);
        if (curHourly < 49)
        return weatherResult.getHourly().getData().get(curHourly);
        else return null;
    }

}
