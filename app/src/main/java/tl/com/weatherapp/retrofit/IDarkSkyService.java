package tl.com.weatherapp.retrofit;

import retrofit2.http.GET;
import retrofit2.http.Path;
import tl.com.weatherapp.model.weather.WeatherResult;

public interface IDarkSkyService {
    @GET("forecast/{id}/{lat},{lng}")
    io.reactivex.Observable<WeatherResult> getWeatherByLatIng(@Path("id") String appid,
                                                              @Path("lat") String lat ,
                                                              @Path("lng") String lng);

}
