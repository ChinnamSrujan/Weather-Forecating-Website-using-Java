package com.example.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherForecastData {
    private String date;
    private String temperature;
    private String humidity;
    private String city;
    private String weatherCondition;
    private String icon;

    public WeatherForecastData() {}

    public WeatherForecastData(String date, String temperature, String humidity, String city, String weatherCondition) {
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.city = city;
        this.weatherCondition = weatherCondition;
        this.icon = getWeatherConditionIcon(weatherCondition);
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
        this.icon = getWeatherConditionIcon(weatherCondition);
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private String getWeatherConditionIcon(String weatherCondition) {
        if (weatherCondition == null) return "🌤";
        
        switch (weatherCondition) {
            case "Clear":
                return "☀";
            case "Clouds":
                return "☁";
            case "Rain":
                return "🌧";
            case "Snow":
                return "❄";
            default:
                return "🌤";
        }
    }
}
