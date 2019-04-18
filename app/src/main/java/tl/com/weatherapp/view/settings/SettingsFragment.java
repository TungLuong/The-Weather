package tl.com.weatherapp.view.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import java.util.Arrays;

import tl.com.weatherapp.R;
import tl.com.weatherapp.view.main.MainActivity;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference tempPref = (ListPreference) findPreference(getString(R.string.pref_temp_unit));
        ListPreference distancePref = (ListPreference) findPreference(getString(R.string.pref_distance_unit));
        ListPreference speedPref = (ListPreference) findPreference(getString(R.string.pref_speed_unit));
        ListPreference langPref = (ListPreference) findPreference(getString(R.string.pref_lang_key));
        String[] languages = getResources().getStringArray(R.array.pref_lang_entries);
        String[] langPrefValues = getResources().getStringArray(R.array.pref_lang_values);

        tempPref.setSummary(tempPref.getValue());
        distancePref.setSummary(distancePref.getValue());
        speedPref.setSummary(speedPref.getValue());

        for(int i = 0; i < langPrefValues.length;i++){
            if(langPrefValues[i].equals(langPref.getValue())){
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if(key.equals(getString(R.string.pref_lang_key))){
            ListPreference langPref = (ListPreference) findPreference(getString(R.string.pref_lang_key));
            String[] languages = getResources().getStringArray(R.array.pref_lang_entries);
            String[] langPrefValues = getResources().getStringArray(R.array.pref_lang_values);

            for(int i = 0; i < langPrefValues.length;i++){
                if(langPrefValues[i].equals(langPref.getValue())){
                    langPref.setSummary(languages[i]);
                    break;
                }
            }

            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        else{
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key,null));
        }

    }
}
