package com.example.ivan.dailyweather;

/**
 * Created by ivan on 2/8/18.
 */

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;



public interface WeatherAPIClient {

    @Headers("x-api-key: 03da8312a7a5e6916e46e172e8c0b2f0")
    @GET("/data/2.5/weather?units=metric")
    Call<WeatherResponse> getWeatherByName(@Query("q") String city);

    @Headers("x-api-key: 03da8312a7a5e6916e46e172e8c0b2f0")
    @GET("/data/2.5/weather?units=metric")
    Call<WeatherResponse> getWeatherByPosition(@Query("lat") float latitude, @Query("lon") float longitude);
}
