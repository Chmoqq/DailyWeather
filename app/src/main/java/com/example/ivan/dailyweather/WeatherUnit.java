package com.example.ivan.dailyweather;

public class WeatherUnit {

    public String city = null;
    public double longitude;
    public double latitude;
    public int fragmentID;
    public WeatherResponse response = null;
    public boolean is_completed;

    public WeatherUnit(int fragmentID, String city) {
        this.fragmentID = fragmentID;
        this.city = city;
    }

    public WeatherUnit(int fragmentID, double latitude, double longitude) {
        this.fragmentID = fragmentID;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
