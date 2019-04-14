package tl.com.weatherapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import tl.com.weatherapp.model.airquality.AirQuality;
import tl.com.weatherapp.view.weatherdetail.WeatherDetailFragment;
import tl.com.weatherapp.model.weather.WeatherResult;

public class WeatherFragmentAdapter extends FragmentStatePagerAdapter {

    List<WeatherResult> weatherResultList = new ArrayList<>();
    List<AirQuality> airQualityList = new ArrayList<>();

    public WeatherFragmentAdapter(FragmentManager fm,List<WeatherResult> weatherResultList,List<AirQuality> airQualityList) {
        super(fm);
        this.weatherResultList = weatherResultList;
        this.airQualityList = airQualityList;
    }


    @Override
    public Fragment getItem(int position) {
        WeatherDetailFragment fragment = new WeatherDetailFragment(weatherResultList.get(position),airQualityList.get(position),position);
        return fragment;
    }

    @Override
    public int getCount() {
        return weatherResultList.size() >= airQualityList.size() ? weatherResultList.size() : airQualityList.size() ;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }




}
