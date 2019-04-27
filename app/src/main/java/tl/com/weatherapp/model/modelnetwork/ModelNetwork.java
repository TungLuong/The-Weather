package tl.com.weatherapp.model.modelnetwork;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import tl.com.weatherapp.model.airquality.AirQuality;
import tl.com.weatherapp.presenter.weatheraddress.IWeatherAddressPresenter;
import tl.com.weatherapp.presenter.weatherhome.IWeatherHomePresenter;
import tl.com.weatherapp.retrofit.IAirQualityService;
import tl.com.weatherapp.retrofit.IDarkSkyService;
import tl.com.weatherapp.retrofit.RetrofitAirQualityClient;
import tl.com.weatherapp.retrofit.RetrofitWeatherClient;
import tl.com.weatherapp.view.main.MainActivity;
import tl.com.weatherapp.R;
import tl.com.weatherapp.WeatherWidget;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.weather.WeatherResult;
import tl.com.weatherapp.presenter.main.IMainPresenter;

import static android.content.Context.MODE_PRIVATE;
import static tl.com.weatherapp.common.Common.CURRENT_ADDRESS_ID;
import static tl.com.weatherapp.common.Common.NO_UPDATE_WIDGET;
import static tl.com.weatherapp.common.Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION;
import static tl.com.weatherapp.common.Common.UPDATE_ALL_WIDGET;

public class ModelNetwork {
    private static ModelNetwork instance = new ModelNetwork();
    static SharedPreferences sharedPreferences;
    private static SharedPreferences defSharedPreferences;
    private static List<WeatherResult> weatherResultList = new ArrayList<>();
    private static List<AirQuality> airQualityList = new ArrayList<>();
    private static Context mContext;
    private static int totalAddress;
    private static boolean isMainActivityReceiver = false;
    private static int curPositionPager = 0;
    private MyLocation myLocation;
    private static IMainPresenter iMainPresenter;
    private static IWeatherHomePresenter iWeatherHomePresenter;
    private static IWeatherAddressPresenter iWeatherAddressPresenter;
    private static Gson gson;

    public static ModelNetwork getInstance() {
        return instance;
    }


    public void setiMainPresenter(IMainPresenter iMainPresenter) {
        this.iMainPresenter = iMainPresenter;
    }

    public void setiWeatherHomePresenter(IWeatherHomePresenter iWeatherHomePresenter) {
        this.iWeatherHomePresenter = iWeatherHomePresenter;
    }

    public void setiWeatherAddressPresenter(IWeatherAddressPresenter iWeatherAddressPresenter) {
        this.iWeatherAddressPresenter = iWeatherAddressPresenter;
    }

    public void create(Context context) {
        if (mContext == null) {
            this.mContext = context;
        }
        if (gson == null) {
            gson = new Gson();
        }
        sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
        defSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        totalAddress = sharedPreferences.getInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, 1);
    }

    public void loadDataForMainPresenter() {
        weatherResultList = new ArrayList<>();
        airQualityList = new ArrayList<>();
        for (int i = 0; i < totalAddress; i++) {
            weatherResultList.add(null);
            airQualityList.add(null);
            int addressId = sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + i, CURRENT_ADDRESS_ID);
            updateInformationByAddressId(addressId, UPDATE_ALL_WIDGET);
        }
    }

    private void updateWeatherInformation(final float lat, final float lng, final int addressId, String address, int appWidgetId) {

        Retrofit retrofitWeather = RetrofitWeatherClient.getInstance();
        IDarkSkyService mWeatherService = retrofitWeather.create(IDarkSkyService.class);
        CompositeDisposable compositeDisposableWeather = new CompositeDisposable();
        compositeDisposableWeather.add(mWeatherService.getWeatherByLatIng(
                Common.WEATHER_API_KEY
                , String.valueOf(lat)
                , String.valueOf(lng))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {

                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        weatherResult.setAddress(address);
                        try {
                            int position = getPagerPositionByAddressId(addressId);
                            saveWeatherDataByAddressId(weatherResult, addressId);
                            if (weatherResultList.size() > 0) {
                                weatherResultList.set(position, weatherResult);
                                checkLoadDataFinish();
                                checkNotifiDataWeather(position);
                            }
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            updateWeatherWidget(weatherResult, addressId, appWidgetId);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int position = getPagerPositionByAddressId(addressId);

                        WeatherResult weatherResult = getWeatherDataByAddressId(addressId);
                        if (weatherResultList.size() > 0) {
                            weatherResultList.set(position, weatherResult);
                            checkLoadDataFinish();
                            checkNotifiDataWeather(position);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            updateWeatherWidget(weatherResult, addressId, appWidgetId);
                        }
                    }
                }));

    }

    private WeatherResult getWeatherDataByAddressId(int addressId) {
        String strWeatherResult = sharedPreferences.getString(Common.SHARE_PREF_WEATHER_RESULT_KEY_IN_ADDRESS_ID + addressId, "");
        return gson.fromJson(strWeatherResult, WeatherResult.class);
    }

    private void checkNotifiDataWeather(int position) {
        if (iWeatherAddressPresenter != null) {
            iWeatherAddressPresenter.notifyItemChange(position);
        }
        if (iWeatherHomePresenter != null) {
            iWeatherHomePresenter.notifyItemChange(position);
        }
    }

    private void checkLoadDataFinish() {
        if (!isMainActivityReceiver && onDataReady()) {
            isMainActivityReceiver = true;
            iMainPresenter.loadDataFinish();
        }
    }

    private void saveWeatherDataByAddressId(WeatherResult weatherResult, int addressId) {
        String strWeatherResult = gson.toJson(weatherResult);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Common.SHARE_PREF_WEATHER_RESULT_KEY_IN_ADDRESS_ID + addressId, strWeatherResult);
        editor.commit();
    }

    private int getPagerPositionByAddressId(int addressId) {
        for (int position = 0; position < totalAddress; position++) {
            if (sharedPreferences.getInt(SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + position, -1) == addressId) {
                return position;
            }
        }
        return 0;
    }

    private void updateAirQualityIndex(final float lat, final float lng, final int addressID) {
        Retrofit retrofitAirQuality = RetrofitAirQualityClient.getInstance();
        IAirQualityService mAirQualityService = retrofitAirQuality.create(IAirQualityService.class);
        CompositeDisposable compositeDisposableAirQuality = new CompositeDisposable();
        compositeDisposableAirQuality.add(mAirQualityService.getAirQuality(
                String.valueOf(lat),
                String.valueOf(lng),
                Common.AQI_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<AirQuality>() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void accept(AirQuality airQuality) throws Exception {
                        try {
                            int position = getPagerPositionByAddressId(addressID);
                            saveAirQualityDataByAddressId(airQuality, addressID);
                            if (airQualityList.size() > 0) {
                                airQualityList.set(position, airQuality);
                                checkLoadDataFinish();
                                checkNotifiDataWeather(position);
                            }
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int position = getPagerPositionByAddressId(addressID);

                        if (airQualityList.size() > 0) {
                            airQualityList.set(position, getAirQuailityDataByAddressId(addressID));
                            checkLoadDataFinish();
                            checkNotifiDataWeather(position);
                        }
                    }
                }));
    }

    private AirQuality getAirQuailityDataByAddressId(int addressID) {
        String strAirQuality = sharedPreferences.getString(Common.SHARE_PREF_AIR_QUALITY_KEY_IN_ADDRESS_ID + addressID, "");
        return gson.fromJson(strAirQuality, AirQuality.class);
    }

    private void saveAirQualityDataByAddressId(AirQuality airQuality, int addressID) {
        String strAirQuality = gson.toJson(airQuality);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Common.SHARE_PREF_AIR_QUALITY_KEY_IN_ADDRESS_ID + addressID, strAirQuality);
        editor.commit();
    }

    private boolean onDataReady() {
        for (int i = 0; i < totalAddress; i++) {
            if (airQualityList.get(i) == null) return false;
            if (weatherResultList.get(i) == null) return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void updateWeatherWidget(WeatherResult weatherResult, int addressID, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.weather_widget);
        sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
        if (appWidgetId == UPDATE_ALL_WIDGET) {
            ComponentName name = new ComponentName(mContext.getPackageName(), WeatherWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);
            for (int id : appWidgetIds) {
                int wgAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_BY_WIDGET_ID + id, -1);
                if (weatherResult != null && wgAddressId != -1 && wgAddressId == addressID) {
                    updatAppWidget(weatherResult, views, id);
                    appWidgetManager.updateAppWidget(id, views);

                }
            }
        } else if (appWidgetId != Common.NO_UPDATE_WIDGET) {
            if (weatherResult != null) {
                updatAppWidget(weatherResult, views, appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }

    }

    private static void updatAppWidget(WeatherResult weatherResult, RemoteViews views, int appWidgetId) {
        String tempUnit = defSharedPreferences.getString(mContext.getString(R.string.pref_temp_unit), mContext.getString(R.string.pref_temp_default_value));
        String temp = null;
        if (tempUnit.equals(mContext.getString(R.string.pref_temp_default_value))) {
            temp = Common.covertFtoC(weatherResult.getCurrently().getTemperature()) + "°";
        } else {
            temp = (int) weatherResult.getCurrently().getTemperature() + "°";
        }


        Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
                .append(weatherResult.getCurrently().getIcon())
                .append(".png").toString()).into(views, R.id.img_icon, new int[]{appWidgetId});
        views.setTextViewText(R.id.tv_temp, temp);
        String lastTimeUpdate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            lastTimeUpdate = Common.convertUnixToTime(weatherResult.getCurrently().getTime());
        }
        views.setTextViewText(R.id.tv_time_update, lastTimeUpdate);
        String lastLocation = weatherResult.getAddress();
        views.setTextViewText(R.id.tv_location, lastLocation);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(Common.INTENT_APP_WIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, appWidgetId, intent, 0);
        views.setOnClickPendingIntent(R.id.linear_layout, pendingIntent);
    }

    public void updateWidgetAndInformation(int appWidgetId) {
        int addressID = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_BY_WIDGET_ID + appWidgetId, -1);
        updateInformationByAddressId(addressID, appWidgetId);

    }

    public void updateInformationByAddressId(int addressID, int appWidgetId) {
        if (addressID == CURRENT_ADDRESS_ID) {
            myLocation = new MyLocation();
            myLocation.updateWeatherAndAirQualityDeviceLocation(appWidgetId);
        } else if (addressID != -1) {
            float lat = sharedPreferences.getFloat(Common.SHARE_PREF_LAT_KEY_IN_ADDRESS_ID + addressID, 0f);
            float lng = sharedPreferences.getFloat(Common.SHARE_PREF_LNG_KEY_IN_ADDRESS_ID + addressID, 0f);
            String address = sharedPreferences.getString(Common.SHARE_PREF_ADDRESS_NAME_KEY_IN_ADDRESS_ID + addressID, "unknown");
            updateWeatherInformation(lat, lng, addressID, address, appWidgetId);
            updateAirQualityIndex(lat, lng, addressID);
        }
    }


    public void getResultWeatherForWeatherHomePresenter() {
        iWeatherHomePresenter.getResultList(weatherResultList, airQualityList);
        iWeatherHomePresenter.setCurrPositionPagerForView(curPositionPager);
    }


    public void getResultWeatherForWeatherAddressPresenter() {
        iWeatherAddressPresenter.getWeatherResult(weatherResultList);
    }

    public void deleteItem(int position) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + position, -1);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(Common.SHARE_PREF_LAT_KEY_IN_ADDRESS_ID + addressId);
        editor.remove(Common.SHARE_PREF_LAT_KEY_IN_ADDRESS_ID + addressId);
        editor.remove(Common.SHARE_PREF_ADDRESS_NAME_KEY_IN_ADDRESS_ID + addressId);
        editor.remove(Common.SHARE_PREF_WEATHER_RESULT_KEY_IN_ADDRESS_ID + addressId);
        editor.remove(Common.SHARE_PREF_AIR_QUALITY_KEY_IN_ADDRESS_ID + addressId);
        editor.remove(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + position);

        for (int i = position; i < weatherResultList.size() - 1; i++) {
            int newAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + (i + 1), -1);
            editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + i, newAddressId);
        }
        totalAddress--;
        editor.putInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, totalAddress);
        editor.commit();
        weatherResultList.remove(position);
        airQualityList.remove(position);
        if (position <= curPositionPager) curPositionPager--;
    }

    public void moveItem(int oldPo, int newPo) {
        WeatherResult weatherResult = weatherResultList.remove(oldPo);
        weatherResultList.add(newPo, weatherResult);
        AirQuality airQuality = airQualityList.remove(oldPo);
        airQualityList.add(newPo, airQuality);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + oldPo, -1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (newPo < oldPo) {
            for (int i = newPo; i < oldPo; i++) {
                int newAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + i, -1);
                editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + (i + 1), newAddressId);
            }
            editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + newPo, addressId);
        } else if (newPo > oldPo) {
            for (int i = newPo; i > oldPo; i--) {
                int newAddressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + i, -1);
                editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + (i - 1), newAddressId);
            }
            editor.putInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + newPo, addressId);
        }
        editor.commit();
    }

    public void addAddress(Place place) {
        LatLng latLng = place.getLatLng();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Common.DATA, MODE_PRIVATE);

        int maxId = sharedPreferences.getInt(Common.SHARE_PREF_MAX_ID_KEY, 0);
        int newId = maxId + 1;

        Float lat = (float) latLng.latitude;
        Float lng = (float) latLng.longitude;
        String addressName = String.valueOf(place.getName());
        saveAddressInfoData(totalAddress,newId,lat,lng,addressName);

        totalAddress++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Common.SHARE_PREF_TOTAL_ADDRESS_KEY, totalAddress);
        editor.putInt(Common.SHARE_PREF_MAX_ID_KEY, newId);
        editor.commit();

        weatherResultList.add(null);
        airQualityList.add(null);
        updateWeatherInformation(lat, lng, newId, addressName, NO_UPDATE_WIDGET);
        updateAirQualityIndex(lat, lng, newId);
    }

    public void setCurrentPagerByAppWidgetId(int appWidgetId) {
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_BY_WIDGET_ID + appWidgetId, -1);
        for (int position = 0; position < totalAddress; position++) {
            int sfAdressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + position, -1);

            if (sfAdressId == addressId) {
                curPositionPager = position;
                break;
            }
            Log.d(ModelNetwork.class.getSimpleName(), "ADDRESS ID " + sfAdressId + "  POSITION PAGER :" + position);

        }
        //  Log.d(ModelNetwork.class.getSimpleName(),"ADDRESS ID "+addressId+"  POSITION PAGER :"+ curPositionPager);
    }

    public void setCurrentPager(int positionPager) {
        curPositionPager = positionPager;
    }

    public void updateInformationByPosition(int position) {
        int addressId = sharedPreferences.getInt(Common.SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + position, -1);
        updateInformationByAddressId(addressId, UPDATE_ALL_WIDGET);
        Toast.makeText(mContext,mContext.getString(R.string.update_successful),Toast.LENGTH_SHORT).show();
        //iWeatherHomePresenter.notifyItemChange(position);
    }

    public void isNotReceiver() {
        isMainActivityReceiver = false;
    }

    class MyLocation {
        private FusedLocationProviderClient mFusedLocationProviderClient;

        private void updateWeatherAndAirQualityDeviceLocation(int appWidgetId) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
            try {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location curLocation = task.getResult();
                            float cLat = 0;
                            float cLng = 0;
                            String cAddress = "";
                            sharedPreferences = mContext.getSharedPreferences(Common.DATA, Context.MODE_PRIVATE);
                            if (curLocation != null) {
                                cLat = (float) curLocation.getLatitude();
                                cLng = (float) curLocation.getLongitude();
                                cAddress = getAddress(cLat, cLng);

                                saveAddressInfoData(0,CURRENT_ADDRESS_ID,cLat,cLng,cAddress);
                            } else {
                                cLat = sharedPreferences.getFloat(Common.SHARE_PREF_LAT_KEY_IN_ADDRESS_ID + CURRENT_ADDRESS_ID, 0f);
                                cLng = sharedPreferences.getFloat(Common.SHARE_PREF_LNG_KEY_IN_ADDRESS_ID + CURRENT_ADDRESS_ID, 0f);
                                cAddress = sharedPreferences.getString(Common.SHARE_PREF_ADDRESS_NAME_KEY_IN_ADDRESS_ID + CURRENT_ADDRESS_ID, "unknown");
                            }
                            updateWeatherInformation(cLat, cLng, CURRENT_ADDRESS_ID, cAddress, appWidgetId);
                            updateAirQualityIndex(cLat, cLng, CURRENT_ADDRESS_ID);
                        } else {
                        }
                    }
                });
            } catch (final Exception e) {
            }

        }

        private String getAddress(float lat, float lng) {
            Geocoder geocoder = new Geocoder(mContext);
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                if (addresses.size() > 0) {
                    if (addresses.get(0).getSubAdminArea() == null) {
                        return "unknown";
                    }
                    return addresses.get(0).getSubAdminArea();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "unknown";
        }

    }

    private void saveAddressInfoData(int position, int addressId, float cLat, float cLng, String cAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHARE_PREF_ADDRESS_ID_KEY_IN_POSITION + position, addressId);
        editor.putFloat(Common.SHARE_PREF_LAT_KEY_IN_ADDRESS_ID + addressId, cLat);
        editor.putFloat(Common.SHARE_PREF_LNG_KEY_IN_ADDRESS_ID + addressId, cLng);
        editor.putString(Common.SHARE_PREF_ADDRESS_NAME_KEY_IN_ADDRESS_ID + addressId, cAddress);
        editor.commit();
    }

}
