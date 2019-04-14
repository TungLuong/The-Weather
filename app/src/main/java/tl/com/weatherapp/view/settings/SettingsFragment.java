package tl.com.weatherapp.view.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import tl.com.weatherapp.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference tempPref = (ListPreference) findPreference(getString(R.string.pref_temp_unit));
        ListPreference distancePref = (ListPreference) findPreference(getString(R.string.pref_distance_unit));
        ListPreference speedPref = (ListPreference) findPreference(getString(R.string.pref_speed_unit));

        tempPref.setSummary(tempPref.getValue());
        distancePref.setSummary(distancePref.getValue());
        speedPref.setSummary(speedPref.getValue());

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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Preference preference = findPreference(key);
        preference.setSummary(sharedPreferences.getString(key,null));
    }
}
