package tl.com.weatherapp.view.weatherhome;

import java.util.List;

import tl.com.weatherapp.model.airquality.AirQuality;
import tl.com.weatherapp.model.weather.WeatherResult;

public interface IWeatherHomeView {
    void getResultList(List<WeatherResult> weatherResultList,List<AirQuality> airQualityLis);

    void setCurrPositionPager(int curPositionPager);

    void notifyItemChange(int position);

}
