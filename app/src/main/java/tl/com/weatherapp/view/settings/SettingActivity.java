package tl.com.weatherapp.view.settings;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import tl.com.weatherapp.R;

public class SettingActivity extends AppCompatActivity {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        actionBar = getSupportActionBar();

        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container,new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
