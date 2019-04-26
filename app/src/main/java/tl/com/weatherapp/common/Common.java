package tl.com.weatherapp.common;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;

public class Common {
    public static final String WEATHER_API_KEY = "015f333c2ca5576208dd63d15a91c5ba";
    public static final String AQI_API_KEY = "7b33c7eecac7d9ec232ed4dc7edddcd5dae7e512";
    public static final String GOOGLE_MAP_API_KEY = "AIzaSyAsvRrYBZ770RpBDQNsbaOCsz3XZjD9Vc8";
    public static final String INTENT_ADDRESS_ID = "INTENT_ADDRESS_ID" ;
    public static final String INTENT_WEATHER_RESULT = "INTENT_WEATHER_RESULT";
    public static final String DATA = "DATA";
    public static final int CURRENT_ADDRESS_ID = 0;
    public static final String LIST_WEATHER_RESULT = "LIST_WEATHER_RESULT";
    public static final String ACTION_UPDATE_CONFIG_WEATHER = "ACTION_UPDATE_CONFIG_WEATHER";
    public static final String SHARE_PREF_LAT_KEY_AT = "SHARE_PREF_LAT_KEY_AT";
    public static final String SHARE_PREF_LNG_KEY_AT = "SHARE_PREF_LNG_KEY_AT" ;
    public static final String SHARE_PREF_ADDRESS_NAME_KEY_AT = "SHARE_PREF_ADDRESS_NAME_KEY_AT" ;
    public static final String SHARE_PREF_WIDGET_ADDRESS_ID_KEY_AT = "SHARE_PREF_WIDGET_ADDRESS_ID_KEY_AT" ;
    public static final String SHARE_PREF_ADDRESS_ID_KEY_AT = "SHARE_PREF_ADDRESS_ID_KEY_AT" ;
    public static final String INTENT_APP_WIDGET_ID = "INTENT_APP_WIDGET_ID";
    public static final String SHARE_PREF_TOTAL_ADDRESS_KEY = "SHARE_PREF_TOTAL_ADDRESS_KEY" ;
    public static final String SHARE_PREF_MAX_ID_KEY = "SHARE_PREF_MAX_ID_KEY";
    public static final int UPDATE_ALL_WIDGET = -1;
    public static final int NO_UPDATE_WIDGET = -2 ;
    public static final String SHARE_PREF_WEATHER_KEY_AT = "SHARE_PREF_WEATHER_KEY_AT" ;
    public static final int TOTAL_ATTRIBUTE_WEATHER = 8;
    public static final String SHARE_PREF_AIR_QUALITY_KEY_AT = "SHARE_PREF_AIR_QUALITY_KEY_AT" ;

    //    public static Location current_location = new Location("");
    public static String ACTION_GET_WEATHER_RESULT_BY_ADDRESS_ID = "ACTION_GET_WEATHER_RESULT_BY_ADDRESS_ID";
    public static String ACTION_RECEIVER_RESPONSE_FROM_WIDGET = "ACTION_RECEIVER_RESPONSE_FROM_WIDGET";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToDate(int dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToTime(int dt) {
        Date time = new Date(dt * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" HH:mm");
        String strTime = simpleDateFormat.format(time);
        return strTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String convertUnixToDay(int dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE");
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }

    public static int covertFtoC(double temperature) {
        double temp = (temperature - 32) / 1.8;
        return (int) temp;
    }

    public static int kmsToMiles(double distance){
        return (int) (distance * 0.62);
    }

    public static int mpsToKmph(double speed){
        return(int) (3.6 * speed);
    }

    public static int mpsToMph(double speed){
        return(int) (0.277778 * 3.6 * speed);
    }

    public static String pressureConverter(double value,String unit){
        String output = "";
        switch (unit){
            case "mBar":{
                output = (int) value + " mBar";
                break;
            }
            case "inHg":{
                output = (int)(value * 0.03) + " inHg";
                break;
            }
            case "psi":{
                output = (int)(value * 0.014) + " psi";
                break;
            }
            case "bar":{
                output = (int)(value * 0.001) + " bar";
                break;
            }
            case "mmHg":{
                output = (int)(value * 0.75) + " mmHg";
                break;
            }
            default:{
                output = (int) value + " mBar";
                break;
            }
        }
        return output;
    }
}
