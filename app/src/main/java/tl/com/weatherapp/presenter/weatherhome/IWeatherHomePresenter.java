package tl.com.weatherapp.presenter.weatherhome;

import java.util.List;

import tl.com.weatherapp.model.airquality.AirQuality;
import tl.com.weatherapp.model.weather.WeatherResult;

public interface IWeatherHomePresenter {
    void getResultList(List<WeatherResult> weatherResultList, List<AirQuality> airQualityList);


    void setCurrPositionPagerForView(int curPositionPager);

    void notifyItemChange(int position);
}
