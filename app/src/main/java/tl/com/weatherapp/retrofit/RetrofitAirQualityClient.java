package tl.com.weatherapp.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAirQualityClient {
    private static Retrofit instance;

    public static Retrofit getInstance() {
        if(instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl("https://api.waqi.info/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }
}
