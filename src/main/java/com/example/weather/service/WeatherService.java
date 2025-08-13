package com.example.weather.service;

import com.example.weather.model.WeatherForecastData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private final WeatherDataRetriever weatherDataRetriever;
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";

    @Autowired
    public WeatherService(WeatherDataRetriever weatherDataRetriever) {
        this.weatherDataRetriever = weatherDataRetriever;
    }

    public List<WeatherForecastData> getWeatherForecast(String city) {
        String weatherData = weatherDataRetriever.retrieveWeatherData(city, BASE_URL);
        
        if (weatherData != null) {
            return parseWeatherData(weatherData, city);
        }
        
        return new ArrayList<>();
    }

    private List<WeatherForecastData> parseWeatherData(String weatherData, String city) {
        List<WeatherForecastData> forecastDataList = new ArrayList<>();
        
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(weatherData, JsonObject.class);
            JsonArray forecastArray = jsonObject.getAsJsonArray("list");

            for (JsonElement element : forecastArray) {
                JsonObject forecastObject = element.getAsJsonObject();
                String date = forecastObject.get("dt_txt").getAsString();
                JsonObject mainObject = forecastObject.getAsJsonObject("main");
                String temperature = mainObject.get("temp").getAsString();
                String humidity = mainObject.get("humidity").getAsString();

                JsonArray weatherArray = forecastObject.getAsJsonArray("weather");
                JsonObject weatherObject = weatherArray.get(0).getAsJsonObject();
                String weatherCondition = weatherObject.get("main").getAsString();

                WeatherForecastData forecastData = new WeatherForecastData(date, temperature, humidity, city, weatherCondition);
                forecastDataList.add(forecastData);
            }

        } catch (JsonParseException e) {
            System.err.println("Error parsing weather data: " + e.getMessage());
        }
        
        return forecastDataList;
    }
}
