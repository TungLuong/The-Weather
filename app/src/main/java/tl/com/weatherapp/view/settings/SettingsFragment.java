package tl.com.weatherapp.view.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
<<<<<<< HEAD
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
=======
>>>>>>> 991cb8b54193326f1941193f66ad237d0de52efd

import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.view.main.MainActivity;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    ListPreference tempPref;
    ListPreference distancePref;
    ListPreference speedPref;
    ListPreference langPref;
    CheckBoxPreference dailyNotiPref;
    Preference timeNotiPref;
    String strTimeNoti;
    Calendar curCalendar;
    AlarmManager alarmManager;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        tempPref = (ListPreference) findPreference(getString(R.string.pref_temp_unit));
        distancePref = (ListPreference) findPreference(getString(R.string.pref_distance_unit));
        speedPref = (ListPreference) findPreference(getString(R.string.pref_speed_unit));
        langPref = (ListPreference) findPreference(getString(R.string.pref_lang_key));
        dailyNotiPref = (CheckBoxPreference) findPreference(getString(R.string.pref_daily_notification_key));
        timeNotiPref = findPreference(getString(R.string.pref_time_notification_key));
        dailyNotiPref.setOnPreferenceChangeListener(this);
        timeNotiPref.setOnPreferenceClickListener(this);
        updateDataSettings();
    }

<<<<<<< HEAD
    private void updateDataSettings() {
        sharedPreferences = getActivity().getSharedPreferences(Common.SETTINGS, MODE_PRIVATE);
        strTimeNoti = sharedPreferences.getString(Common.SHARED_PREF_SETTING_TIME_NOTIFICATION_KEY, getString(R.string.time_notification_default));
        setCalendar(Integer.valueOf(strTimeNoti.substring(0, 2)), Integer.valueOf(strTimeNoti.substring(3)));

        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        checkTimeNotiPrefEnable(dailyNotiPref.isChecked());
        timeNotiPref.setShouldDisableView(false);
=======
        ListPreference tempPref = (ListPreference) findPreference(getString(R.string.pref_temp_unit));
        ListPreference distancePref = (ListPreference) findPreference(getString(R.string.pref_distance_unit));
        ListPreference speedPref = (ListPreference) findPreference(getString(R.string.pref_speed_unit));
        ListPreference pressurePref = (ListPreference) findPreference(getString(R.string.pref_pressure_unit));
        ListPreference langPref = (ListPreference) findPreference(getString(R.string.pref_lang_key));
>>>>>>> 991cb8b54193326f1941193f66ad237d0de52efd
        String[] languages = getResources().getStringArray(R.array.pref_lang_entries);
        String[] langPrefValues = getResources().getStringArray(R.array.pref_lang_values);

        tempPref.setSummary(tempPref.getValue());
        distancePref.setSummary(distancePref.getValue());
        speedPref.setSummary(speedPref.getValue());
        pressurePref.setSummary(pressurePref.getValue());

        for (int i = 0; i < langPrefValues.length; i++) {
            if (langPrefValues[i].equals(langPref.getValue())) {
                langPref.setSummary(languages[i]);
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_lang_key))) {
            ListPreference langPref = (ListPreference) findPreference(getString(R.string.pref_lang_key));
            String[] languages = getResources().getStringArray(R.array.pref_lang_entries);
            String[] langPrefValues = getResources().getStringArray(R.array.pref_lang_values);

            for (int i = 0; i < langPrefValues.length; i++) {
                if (langPrefValues[i].equals(langPref.getValue())) {
                    langPref.setSummary(languages[i]);
                    break;
                }
            }

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (key.equals(getString(R.string.pref_daily_notification_key))) {

        } else {
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key, null));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        if (preference.getKey().equals(getString(R.string.pref_daily_notification_key))) {
            if (checkTimeNotiPrefEnable(!dailyNotiPref.isChecked())) {
                cancelAlarm();
                setCalendar(curCalendar.get(Calendar.HOUR_OF_DAY), curCalendar.get(Calendar.MINUTE));
                startAlarm();
            } else {
                cancelAlarm();
            }
            ;

        }
        return true;
    }

    private boolean checkTimeNotiPrefEnable(Boolean b) {

        if (b) {
            timeNotiPref.setEnabled(true);
            timeNotiPref.setSummary(strTimeNoti);
            return true;
        } else {
            timeNotiPref.setEnabled(false);
            timeNotiPref.setSummary("   ");
            return false;
        }
    }

    private void cancelAlarm() {
        // alarmManager.cancel(pendingIntent);
        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1253, intent, 0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getActivity(), "Cancel Alarm ", Toast.LENGTH_SHORT).show();

    }

    private void startAlarm() {
        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1253, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, curCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getActivity(), "Start Alarm  ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_time_notification_key))) {
            setTime();
        }
        return true;
    }

    private void setTime() {
        TimePickerDialog dialog = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    setCalendar(i, i1);
                    cancelAlarm();
                    startAlarm();
                }
            }, Integer.valueOf(strTimeNoti.substring(0, 2)), Integer.valueOf(strTimeNoti.substring(3)), true);
            dialog.show();
        }
    }

    private void setCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (hour < calendar.get(Calendar.HOUR_OF_DAY) || (hour == calendar.get(Calendar.HOUR) && minute < calendar.get(Calendar.MINUTE))) {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 86400000);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        }
        curCalendar = calendar;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        strTimeNoti = simpleDateFormat.format(curCalendar.getTime());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Common.SHARED_PREF_SETTING_TIME_NOTIFICATION_KEY, strTimeNoti);
        editor.commit();
        timeNotiPref.setSummary(strTimeNoti);
    }

}
