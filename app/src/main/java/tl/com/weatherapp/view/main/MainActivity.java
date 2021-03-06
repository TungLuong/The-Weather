package tl.com.weatherapp.view.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Locale;

import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.view.searchaddress.SearchAddressFragment;
import tl.com.weatherapp.view.weatheraddress.WeatherAddressFragment;
import tl.com.weatherapp.view.weatherhome.WeatherHomeFragment;
import tl.com.weatherapp.base.BaseActivity;
import tl.com.weatherapp.presenter.main.MainPresenter;

public class MainActivity extends BaseActivity implements IMainView {


//    private BroadcastReceiver UIBroadcastReceiver;

    public static final String TAG = MainActivity.class.getSimpleName();
//    private int totalAddress = 0;
//    private int positionPager = CURRENT_ADDRESS_ID;
//    private List<WeatherResult> listWeatherResults = new ArrayList<>();
//    private List<Boolean> isReceiver = new ArrayList<>();
    private RelativeLayout loadingView;
    private MainPresenter mainPresenter;
    private TextView notifiConnection;
//    private boolean fragmentHomeIsVisible = false;
//    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        sharedPreferences = getSharedPreferences(Common.DATA, MODE_PRIVATE);
//        totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
        mainPresenter = new MainPresenter(this);
        mainPresenter.setiMainView(this);
        Bundle extras = getIntent().getExtras();
        mainPresenter.isNotReceiver();
        if (extras != null) {
            int appWidgetId = extras.getInt(Common.INTENT_APP_WIDGET_ID);
            mainPresenter.setCurrentPagerByAppWidgetId(appWidgetId);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPreferences.getString(getString(R.string.pref_lang_key),getString(R.string.pref_lang_default_value));
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_main);
        initView();
        //Request permission
        requestPermission();
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        Bundle extras = getIntent().getExtras();
//        if (extras != null && mainPresenter != null) {
//            mainPresenter.isNotReceiver();
//            int addressId = extras.getInt(Common.INTENT_ADDRESS_ID);
//            mainPresenter.setCurrentPagerByAppWidgetId(addressId);
//        }
//    }

    private void requestPermission() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mainPresenter.start();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
    }

    private void initView() {
        loadingView = findViewById(R.id.loading_view);
        loadingView.setVisibility(View.VISIBLE);
        notifiConnection = findViewById(R.id.tv_notifi_connection);
//        BookLoading bookLoading = findViewById(R.id.bookloading);
//        bookLoading.start();
//        NewtonCradleLoading newtonCradleLoading = findViewById(R.id.newton_cradle_loading);
//        newtonCradleLoading.start();
        ImageView flashImage = findViewById(R.id.flash_image);
        Animation rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.spin);
        flashImage.startAnimation(rotation);
    }

    @Override
    public void openWeatherHomeFragment() {
        WeatherHomeFragment fragment = new WeatherHomeFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in,R.anim.translate_form_top_to_botton);
        transaction.replace(R.id.frame_layout, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.commitAllowingStateLoss();
        transaction.disallowAddToBackStack();
       //transaction.addToBackStack(null);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void showNotifiConnection(boolean b) {
        if(b) notifiConnection.setVisibility(View.VISIBLE);
        else notifiConnection.setVisibility(View.GONE);

    }

    public void openWeatherAddressFragment() {
        WeatherAddressFragment fragment = new WeatherAddressFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.translate_form_botton_to_top,R.anim.fade_out,R.anim.fade_in,R.anim.translate_form_top_to_botton);
        transaction.replace(R.id.frame_layout, fragment);
        //transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public void openSearchAddressFragment() {
        SearchAddressFragment fragment = new SearchAddressFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out,R.anim.fade_in,R.anim.fade_out);
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    public boolean isConnectedNetwork(){
        return mainPresenter.isConnectedNetwork(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.unregisterReceiver();
    }

}
