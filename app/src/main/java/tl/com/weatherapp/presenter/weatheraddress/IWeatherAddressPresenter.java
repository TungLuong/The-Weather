package tl.com.weatherapp.presenter.weatheraddress;

import java.util.List;

import tl.com.weatherapp.model.weather.WeatherResult;

public interface IWeatherAddressPresenter {

    void getWeatherResult(List<WeatherResult> weatherResults);

    void notifyItemChange(int position);
}
