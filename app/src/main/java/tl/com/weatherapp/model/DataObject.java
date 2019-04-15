package tl.com.weatherapp.model;

import tl.com.weatherapp.model.airquality.AirQuality;
import tl.com.weatherapp.model.weather.WeatherResult;

public class DataObject {
    private WeatherResult weatherResult;
    private AirQuality airQuality;

    public DataObject(WeatherResult weatherResult, AirQuality airQuality) {
        this.weatherResult = weatherResult;
        this.airQuality = airQuality;
    }
}
