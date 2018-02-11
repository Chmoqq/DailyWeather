package com.example.ivan.dailyweather;

import java.util.List;

/**
 * Created by ivan on 1/31/18.
 */

public class WeatherResponse {

    private int visibility;
    private int cod;
    private String name;
    private List<WeatherInfoEntity> weather;
    private WeatherInfoTemp main;
    private WeatherClouds clouds;
    private OtherInfo sys;

    public class WeatherInfoEntity {

        private String main;
        private String description;

        public String getWeatherInfoMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }
    }

    public class WeatherInfoTemp {

        private int temp_min;
        private int temp_max;
        private int pressure;
        private double temp;
        private int humidity;

        public int getTemp_min() {
            return temp_min;
        }

        public int getTemp_max() {
            return temp_max;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public double getTemp() {
            return temp;
        }

    }

    public class WeatherClouds {

        private int all;

        public int getClouds() {
            return all;
        }

    }

    public class OtherInfo {

        private String country;
        private int sunrise;
        private int sunset;

        public String getCountry() {
            return country;
        }

        public int getSunrise() {
            return sunrise;
        }

        public int getSunset() {
            return sunset;
        }
    }

    public OtherInfo getOtherInfo() {
        return sys;
    }

    public WeatherClouds getCloudsClass() {
        return clouds;
    }

    public WeatherInfoTemp getMainTemp() {
        return main;
    }

    public int getCod() {
        return cod;
    }

    public String getName() {
        return name;
    }

    public int getVisibility() {
        return visibility;
    }

    public List<WeatherInfoEntity> getWeather() {
        return weather;
    }

}
