package tl.com.weatherapp.retrofit;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tl.com.weatherapp.model.airquality.AirQuality;

public interface IAirQualityService {
    @GET("feed/geo:{lat};{lon}/")
    io.reactivex.Observable<AirQuality> getAirQuality(
                                                      @Path("lat") String lat,
                                                      @Path("lon") String lon,
                                                      @Query("token") String apiKey);
}
