package com.example.weather.service;

public interface WeatherDataRetriever {
    /** Fetch forecast/current weather by city name */
    String retrieveWeatherData(String city, String url);

    /** Fetch data by latitude and longitude (for air quality, etc.) */
    String retrieveWeatherDataByCoords(double lat, double lon, String url);
}
