package tl.com.weatherapp.view.weatherdetail;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hai.semicircle.SemiCircle;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.R;
import tl.com.weatherapp.adapter.ItemDailyWeatherAdapter;
import tl.com.weatherapp.adapter.ItemHourlyWeatherAdapter;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.airquality.AirQuality;
import tl.com.weatherapp.model.weather.WeatherResult;
import tl.com.weatherapp.presenter.weatherdetail.WeatherDetailPresenter;
import tl.com.weatherapp.view.main.MainActivity;


@TargetApi(Build.VERSION_CODES.M)
public class WeatherDetailFragment extends Fragment implements View.OnScrollChangeListener {

    static WeatherDetailFragment instance;

    private SwipeRefreshLayout refreshLayout;
    private RoundedImageView imgWeather;
    private ImageView iconWeather;
    private TextView tvCityName, tvHumidity, tvPressure, tvTemperature, tvDateTime, tvDescription, tvDewPoint, tvCloudCover, tvUVIndex, tvVisibility, tvOzone;
    private LinearLayout mLinerLayout1, mLinerLayout2, mLinerLayour3;
    private RecyclerView rcvDaily, rcvHourly, rcvAttributeWeather;
    private NestedScrollView scrollView1;
    private ScrollView scrollView2;
    private LinearLayout mLinearLayout4;
    private RelativeLayout space;
    private LinearLayout weatherPanel;
    private ConstraintLayout mainDetailsLayout;

    private TextView tvAqiIndex;
    private TextView tvAqiLevel;
    private TextView tvAqiDes;
    private ImageView aqiIndexIndicator;
    private LinearLayout airQualityIndexScale;
    private RelativeLayout relativeLayoutAqi;
    private ListView aqiDescription;

    //private ProgressBar loading;
    private ImageView background;

    private AlertDialog dialogInternet;

    private BroadcastReceiver UIBroadcastReceiver;

    private WeatherResult weatherResult;
    private AirQuality airQuality;

    private SemiCircle sunSemiCircle;
    private ImageView windmillWings;
    private TextView windSpeed;
    private TextView windDirection;


    private int countAddress;
    private float alphaLinerLayout2 = 1.0f;
    private WeatherDetailPresenter presenter;
    private SharedPreferences sharedPreferences;

    public WeatherDetailFragment() {
    }

    @SuppressLint("ValidFragment")
    public WeatherDetailFragment(WeatherResult weatherResult, AirQuality airQuality, int countAddress) {
        this.weatherResult = weatherResult;
        this.airQuality = airQuality;
        this.countAddress = countAddress;
        presenter = new WeatherDetailPresenter();

    }

    //    public WeatherDetailFragment() {
//
//    }
//
//    public static WeatherDetailFragment getInstance() {
//        if (instance == null) {
//            instance = new WeatherDetailFragment();
//        }
//        return instance;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weather_detail_new, container, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // loading = view.findViewById(R.id.loading);
        background = view.findViewById(R.id.background_image_view);
        tvCityName = view.findViewById(R.id.tv_city_name);
        tvHumidity = view.findViewById(R.id.tv_humidity);
        tvPressure = view.findViewById(R.id.tv_pressure);
        tvTemperature = view.findViewById(R.id.tv_temperature);
        tvDateTime = view.findViewById(R.id.tv_date_time);
        tvDescription = view.findViewById(R.id.tv_description);
        tvDewPoint = view.findViewById(R.id.tv_dew_point);
        tvCloudCover = view.findViewById(R.id.tv_cloud_cover);
        tvUVIndex = view.findViewById(R.id.tv_uv_index);
        tvVisibility = view.findViewById(R.id.tv_visibility);
//        tvOzone = view.findViewById(R.id.tv_ozone);

        tvAqiIndex = view.findViewById(R.id.tv_aqi_index);
        tvAqiLevel = view.findViewById(R.id.tv_aqi_level);
        tvAqiDes = view.findViewById(R.id.tv_aqi_des);
        aqiIndexIndicator = view.findViewById(R.id.aqi_index_indicator);
        airQualityIndexScale = view.findViewById(R.id.index_scale);
        aqiDescription = view.findViewById(R.id.lv_description_aqi);
        relativeLayoutAqi = view.findViewById(R.id.relative_layout_aqi);
        relativeLayoutAqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aqiDescription.getVisibility() == View.VISIBLE) {
                    aqiDescription.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
                    aqiDescription.setVisibility(View.GONE);
                } else {
                    aqiDescription.setVisibility(View.VISIBLE);
                    aqiDescription.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                }
            }
        });

        sunSemiCircle = view.findViewById(R.id.sum_semicircle);
        windmillWings = view.findViewById(R.id.windmill_wings);
        windSpeed = view.findViewById(R.id.wind_speed);
        windDirection = view.findViewById(R.id.wind_direction);

        rcvDaily = view.findViewById(R.id.rcv_daily);
        rcvHourly = view.findViewById(R.id.rcv_hourly);
        //rcvAttributeWeather = view.findViewById(R.id.rcv_attribute_weather);

        mainDetailsLayout = view.findViewById(R.id.main_details_layout);
        mLinerLayout1 = view.findViewById(R.id.liner_layout_1);
        mLinerLayout2 = view.findViewById(R.id.liner_layout_2);
        mLinerLayour3 = view.findViewById(R.id.liner_layout_3);
        scrollView1 = view.findViewById(R.id.scrollView_1);
        scrollView1.setOverScrollMode(View.OVER_SCROLL_NEVER);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                if (((MainActivity) getActivity()).isConnectedNetwork()) {
                    presenter.updateInformationInPosition(countAddress);
                } else {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), getString(R.string.title_disconnect), Toast.LENGTH_SHORT).show();
                }
//                ((MainActivity) getActivity()).getIsReceiver().set(countAddress, false);
//                ((MainActivity) getActivity()).sendRequestGetWeatherInfo(countAddress);

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getWeatherInfo(weatherResult);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getWeatherInfo(WeatherResult weatherResult) {
        if (weatherResult == null) {
            refreshLayout.setRefreshing(true);
            Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getContext().getPackageName() + "/drawable/" + "blue_sky_2");
            Picasso.get()
                    .load(uri)
                    .fit()
                    .into(background);
            return;
        }
        String icon_name = weatherResult.getCurrently().getIcon().replace('-', '_');
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getActivity().getPackageName() + "/drawable/" + icon_name);
        Picasso.get()
                .load(uri)
                .fit()
                .into(background);


        //set RecyclerView Hourly
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ItemHourlyWeatherAdapter itemHourlyWeatherAdapter = new ItemHourlyWeatherAdapter(weatherResult, getActivity()
        );
        rcvHourly.setLayoutManager(layoutManager);
        rcvHourly.setAdapter(itemHourlyWeatherAdapter);

        //set RecyclerView Daily
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        ItemDailyWeatherAdapter itemDailyWeatherAdapter = new ItemDailyWeatherAdapter(weatherResult, getActivity());
        rcvDaily.setLayoutManager(layoutManager2);
        rcvDaily.setAdapter(itemDailyWeatherAdapter);
        //rcvDaily.setNestedScrollingEnabled(false);

        // load information
        if (weatherResult.getAddress() == null) {
            tvCityName.setText("unknown");
        } else tvCityName.setText(weatherResult.getAddress());


//        List<AttributeWeather> list= new ArrayList<>();
//        list.add(new AttributeWeather(getString(R.string.Humidity),new StringBuffer(String.valueOf(weatherResult.getCurrently().getHumidity() * 100)).append("%").toString()));
//        list.add(new AttributeWeather(getString(R.string.Pressure),new StringBuilder(String.valueOf(weatherResult.getCurrently().getPressure())).append(" hPa").toString()));
//        list.add(new AttributeWeather(getString(R.string.WindSpeed),new StringBuilder(String.valueOf(weatherResult.getCurrently().getWindSpeed())).append("m/s").toString()));
//        list.add(new AttributeWeather(getString(R.string.DewPoint),new StringBuilder(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getDewPoint()))).append("˚").toString()));
//        list.add(new AttributeWeather(getString(R.string.CloudCover),new StringBuilder(String.valueOf(weatherResult.getCurrently().getCloudCover())).toString()));
//        list.add(new AttributeWeather(getString(R.string.UVIndex),new StringBuilder(String.valueOf(weatherResult.getCurrently().getUvIndex())).toString()));
//        list.add(new AttributeWeather(getString(R.string.Visibility),new StringBuilder(String.valueOf(weatherResult.getCurrently().getVisibility())).append("+ km").toString()));
//        list.add(new AttributeWeather(getString(R.string.Ozone),new StringBuilder(String.valueOf(weatherResult.getCurrently().getOzone())).toString()));
//        //set RecyclerView Attribute Weather
//
//        ItemAttributeWeatherAdapter itemAttributeWeatherAdapter = new ItemAttributeWeatherAdapter(list);
//        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext());
//        layoutManager3.setOrientation(LinearLayoutManager.VERTICAL);
//        //rcvAttributeWeather.setNestedScrollingEnabled(false);
//        rcvAttributeWeather.setLayoutManager(layoutManager3);
//        rcvAttributeWeather.setAdapter(itemAttributeWeatherAdapter);

        String tempUnit = sharedPreferences.getString(getString(R.string.pref_temp_unit), getString(R.string.pref_temp_default_value));
        String distanceUnit = sharedPreferences.getString(getString(R.string.pref_distance_unit), getString(R.string.pref_distance_default_value));
        String speedUnit = sharedPreferences.getString(getString(R.string.pref_speed_unit), getString(R.string.pref_speed_default_value));

        if (tempUnit.equals(getString(R.string.pref_temp_default_value))) {
            tvTemperature.setText(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getTemperature())) + tempUnit);
            tvDewPoint.setText(String.valueOf(Common.covertFtoC(weatherResult.getCurrently().getDewPoint())) + tempUnit);
        } else {
            tvTemperature.setText(String.valueOf((int) weatherResult.getCurrently().getTemperature()) + tempUnit);
            tvDewPoint.setText(String.valueOf((int) weatherResult.getCurrently().getDewPoint()) + tempUnit);
        }

        if (distanceUnit.equals(getString(R.string.pref_distance_default_value))) {
            tvVisibility.setText(String.valueOf((int) weatherResult.getCurrently().getVisibility()) + " " + distanceUnit);
        } else {
            tvVisibility.setText(String.valueOf(Common.kmsToMiles(weatherResult.getCurrently().getVisibility())) + " " + distanceUnit);
        }

        String pressureUnit = sharedPreferences.getString(getString(R.string.pref_pressure_unit), getString(R.string.pref_pressure_default_value));
        tvPressure.setText(Common.pressureConverter(weatherResult.getCurrently().getPressure(),pressureUnit));

        double windSpeedMps = weatherResult.getCurrently().getWindSpeed();
        if (speedUnit.equals(getString(R.string.pref_speed_default_value))) {
            windSpeed.setText((int) windSpeedMps + " " + speedUnit);
        } else if (speedUnit.equals("km/h")) {
            windSpeed.setText(Common.mpsToKmph(windSpeedMps) + " " + speedUnit);
        } else if (speedUnit.equals("mph")) {
            windSpeed.setText(Common.mpsToMph(windSpeedMps) + " " + speedUnit);
        }

        String windDirectionText = "";
        if(windSpeedMps > 0){
            double windBearing = weatherResult.getCurrently().getWindBearing();
            if((windBearing >= 0 && windBearing < 11.25) || (windBearing >= 348.75 && windBearing <= 360)){
                windDirectionText = getString(R.string.wind_north);
            }
            else if(windBearing >= 11.25 && windBearing < 33.75){
                windDirectionText = getString(R.string.wind_north) + " - " + getString(R.string.wind_north_east);
            }
            else if(windBearing >= 33.75 && windBearing < 56.25){
                windDirectionText = getString(R.string.wind_north_east);
            }
            else if(windBearing >= 56.25 && windBearing < 78.75){
                windDirectionText = getString(R.string.wind_east) + " - " + getString(R.string.wind_north_east);
            }
            else if(windBearing >= 78.75 && windBearing < 101.25){
                windDirectionText = getString(R.string.wind_east);
            }
            else if(windBearing >= 101.25 && windBearing < 123.75){
                windDirectionText = getString(R.string.wind_east) + " - " + getString(R.string.wind_south_east);
            }
            else if(windBearing >= 123.75 && windBearing < 146.25){
                windDirectionText = getString(R.string.wind_south_east);
            }
            else if(windBearing >= 146.25 && windBearing < 168.75){
                windDirectionText = getString(R.string.wind_south) + " - " + getString(R.string.wind_south_east);
            }
            else if(windBearing >= 168.75 && windBearing < 191.25){
                windDirectionText = getString(R.string.wind_south);
            }
            else if(windBearing >= 191.25 && windBearing < 213.75){
                windDirectionText = getString(R.string.wind_south) + " - " + getString(R.string.wind_south_west);
            }
            else if(windBearing >= 213.75 && windBearing < 236.25){
                windDirectionText = getString(R.string.wind_south_west);
            }
            else if(windBearing >= 236.25 && windBearing < 258.75){
                windDirectionText = getString(R.string.wind_west) + " - " + getString(R.string.wind_south_west);
            }
            else if(windBearing >= 258.75 && windBearing < 281.25){
                windDirectionText = getString(R.string.wind_west);
            }
            else if(windBearing >= 281.25 && windBearing < 303.75){
                windDirectionText = getString(R.string.wind_west) + " - " + getString(R.string.wind_north_west);
            }
            else if(windBearing >= 303.75 && windBearing <= 326.25){
                windDirectionText = getString(R.string.wind_north_west);
            }
            else if(windBearing >= 326.25 && windBearing < 348.75){
                windDirectionText = getString(R.string.wind_north) + " - " + getString(R.string.wind_north_west);
            }
        }
        windDirection.setText(windDirectionText);

        //set Windmill rotating animation
        windmillWings.clearAnimation();
        double pinWheelDiameter = 10; //diameter in meter

        double roundPerSec = windSpeedMps/(Math.PI * pinWheelDiameter);
        int duration = (int) (1000/(roundPerSec));
        RotateAnimation rotate = new RotateAnimation(0, -360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setDuration(duration);
        rotate.setRepeatCount(Animation.INFINITE);
        windmillWings.startAnimation(rotate);

        tvHumidity.setText((int) (weatherResult.getCurrently().getHumidity() * 100) + " %");
        tvDescription.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getSummary())));
        String cloud = String.valueOf(weatherResult.getCurrently().getCloudCover());
        double cloudPerc = Double.parseDouble(cloud) * 100;
        tvCloudCover.setText(new StringBuilder(String.valueOf((int) cloudPerc)).append(" %"));

        tvUVIndex.setText(new StringBuilder(String.valueOf(weatherResult.getCurrently().getUvIndex())));


        // TODO: update Air Quality View
        //Update sun position
        long rise = weatherResult.getDaily().getData().get(0).getSunriseTime()*1000;
        long set = weatherResult.getDaily().getData().get(0).getSunsetTime()*1000;
        String sunriseTime = String.valueOf(DateFormat.format("HH:mm", rise));
        String sunsetTime = String.valueOf(DateFormat.format("HH:mm", set));
        sunSemiCircle.setSunriseTime(sunriseTime);
        sunSemiCircle.setSunsetTime(sunsetTime);

        long cur = System.currentTimeMillis();
        if (cur > set) {
            sunSemiCircle.setAngle(0);
            sunSemiCircle.setEnabled(false);
        } else {
            cur -= rise;
            set -= rise;
            int angle = (int) ((double) cur/set *180.0);
            sunSemiCircle.setAngle(angle);
        }

        float aqiIndex;
        //get aqi from model
        if (airQuality != null) {
            aqiIndex = airQuality.getData().aqi;
            List<String> listAqiDescription = new ArrayList<>();
            String strIAQI;
            strIAQI = airQuality.data != null && airQuality.data.iaqi != null && airQuality.data.iaqi.pm25 != null ? String.valueOf(airQuality.data.iaqi.pm25.v) : getString(R.string.aqi_not_available);
            listAqiDescription.add(String.valueOf(new StringBuilder("").append(getString(R.string.index)).append(" ").append(getString(R.string.aqi_pm25)).append(" ").append(strIAQI)));

            strIAQI = airQuality.data != null && airQuality.data.iaqi != null && airQuality.data.iaqi.pm10 != null ? String.valueOf(airQuality.data.iaqi.pm10.v) :  getString(R.string.aqi_not_available);
            listAqiDescription.add(String.valueOf(new StringBuilder("").append(getString(R.string.index)).append(" ").append(getString(R.string.aqi_pm10)).append(" ").append(strIAQI)));

            strIAQI = airQuality.data != null && airQuality.data.iaqi != null && airQuality.data.iaqi.co != null ? String.valueOf(airQuality.data.iaqi.co.v) :  getString(R.string.aqi_not_available);
            listAqiDescription.add(String.valueOf(new StringBuilder("").append(getString(R.string.index)).append(" ").append(getString(R.string.aqi_co)).append(" ").append(strIAQI)));

            strIAQI = airQuality.data != null && airQuality.data.iaqi != null && airQuality.data.iaqi.o3 != null ? String.valueOf(airQuality.data.iaqi.o3.v) :  getString(R.string.aqi_not_available);
            listAqiDescription.add(String.valueOf(new StringBuilder("").append(getString(R.string.index)).append(" ").append(getString(R.string.aqi_o3)).append(" ").append(strIAQI)));

            strIAQI = airQuality.data != null && airQuality.data.iaqi != null && airQuality.data.iaqi.no2 != null ? String.valueOf(airQuality.data.iaqi.no2.v) :  getString(R.string.aqi_not_available);
            listAqiDescription.add(String.valueOf(new StringBuilder("").append(getString(R.string.index)).append(" ").append(getString(R.string.aqi_no2)).append(" ").append(strIAQI)));

            strIAQI = airQuality.data != null && airQuality.data.iaqi != null && airQuality.data.iaqi.so2 != null ? String.valueOf(airQuality.data.iaqi.so2.v) : getString(R.string.aqi_not_available);
            listAqiDescription.add(String.valueOf(new StringBuilder("").append(getString(R.string.index)).append(" ").append(getString(R.string.aqi_so2)).append(" ").append(strIAQI)));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.simple_list_item_in_black_background, android.R.id.text1, listAqiDescription);
            aqiDescription.setAdapter(adapter);

        }
        else aqiIndex = 0;

        //125 is provided for example purpose

        airQualityIndexScale.post(new Runnable() {
            public void run() {
                try {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) aqiIndexIndicator.getLayoutParams();
                    float scaleWidth = (float) airQualityIndexScale.getWidth();
                    float leftMargin = aqiIndex / 500 * scaleWidth;
                    params.leftMargin = (int) leftMargin;
                    aqiIndexIndicator.setLayoutParams(params);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvAqiIndex.setText(String.valueOf((int) aqiIndex));
        if (aqiIndex <= 50) {
            tvAqiIndex.setTextColor(getResources().getColor(R.color.aqi_good));
            tvAqiLevel.setText(getString(R.string.aqi_good));
            tvAqiDes.setText(getString(R.string.aqi_good_des));
        } else if (aqiIndex > 50 && aqiIndex <= 100) {
            tvAqiIndex.setTextColor(getResources().getColor(R.color.aqi_moderate));
            tvAqiLevel.setText(getString(R.string.aqi_moderate));
            tvAqiDes.setText(getString(R.string.aqi_moderate_des));
        } else if (aqiIndex > 100 && aqiIndex <= 150) {
            tvAqiIndex.setTextColor(getResources().getColor(R.color.aqi_unhealthy_sensitive));
            tvAqiLevel.setText(getString(R.string.aqi_unhealthy_sensitive));
            tvAqiDes.setText(getString(R.string.aqi_unhealthy_sensitive_des));
        } else if (aqiIndex > 150 && aqiIndex <= 200) {
            getResources().getColor(R.color.aqi_unhealthy);
            tvAqiIndex.setTextColor(getResources().getColor(R.color.aqi_unhealthy));
            tvAqiLevel.setText(getString(R.string.aqi_unhealthy));
            tvAqiDes.setText(getString(R.string.aqi_unhealthy_des));
        } else if (aqiIndex > 200 && aqiIndex <= 300) {
            tvAqiIndex.setTextColor(getResources().getColor(R.color.aqi_very_unhealthy));
            tvAqiLevel.setText(getString(R.string.aqi_very_unhealthy));
            tvAqiDes.setText(getString(R.string.aqi_very_unhealthy_des));
        } else if (aqiIndex > 300 && aqiIndex <= 500) {
            tvAqiIndex.setTextColor(getResources().getColor(R.color.aqi_hazardous));
            tvAqiLevel.setText(getString(R.string.aqi_hazardous));
            tvAqiDes.setText(getString(R.string.aqi_hazardous_des));
        }
        refreshLayout.setRefreshing(false);

    }

    public void isDisConnected() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        switch (v.getId()) {
            case R.id.scrollView_1:
                mLinerLayout1.setY(Math.max(0, scrollY));
                mLinerLayour3.setY(Math.max(mLinerLayout1.getHeight() + scrollY, mLinerLayout1.getHeight() + mLinerLayout2.getHeight() - scrollY));

                alphaLinerLayout2 = 20.0f / (scrollY + 1) - 0.25f;
                mLinerLayout2.setAlpha(alphaLinerLayout2);
                mLinearLayout4.setY(Math.max(mLinerLayout1.getHeight() + mLinerLayour3.getHeight() + scrollY
                        , mLinerLayout1.getHeight() + mLinerLayout2.getHeight() + mLinerLayour3.getHeight() - scrollY));
                break;
            case R.id.scrollView_2:
                if (scrollView1.canScrollVertically(1)) {
                    scrollView2.scrollTo(0, 0);
                } else scrollView2.scrollTo(0, scrollY);
                break;

        }

    }

}

