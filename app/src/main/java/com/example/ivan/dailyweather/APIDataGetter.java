package com.example.ivan.dailyweather;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIDataGetter {
    private Retrofit retrofit;
    private WeatherAPIClient service;

    public APIDataGetter() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(WeatherAPIClient.class);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(WeatherUnit event) {
        if (event.is_completed)
            return;

        if (event.city != null)
            infoGetterCity(event);
        else
            infoGetterLongLat(event);
    }

    private void infoGetterCity(final WeatherUnit wu) {
        Call<WeatherResponse> call = service.getWeatherByName(wu.city);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                wu.response = response.body();
                wu.is_completed = true;
                EventBus.getDefault().post(wu);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
            }
        });
    }

    private void infoGetterLongLat(final WeatherUnit wu) {
        Call<WeatherResponse> callLatLong = service.getWeatherByPosition((float) wu.latitude, (float) wu.longitude);
        callLatLong.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                wu.response = response.body();
                wu.is_completed = true;
                EventBus.getDefault().post(wu);
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                System.out.println(t.getLocalizedMessage());
            }
        });
    }
}
