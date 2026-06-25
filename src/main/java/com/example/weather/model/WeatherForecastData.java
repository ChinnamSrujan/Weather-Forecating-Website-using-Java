package com.example.weather.model;

public class WeatherForecastData {
    private String date;
    private String temperature;
    private String humidity;
    private String city;
    private String weatherCondition;
    private String weatherDescription;
    private String icon;
    // New fields
    private String feelsLike;
    private String windSpeed;
    private String windDirection;
    private String pressure;
    private String visibility;
    private String minTemp;
    private String maxTemp;

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
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    public String getHumidity() { return humidity; }
    public void setHumidity(String humidity) { this.humidity = humidity; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
        this.icon = getWeatherConditionIcon(weatherCondition);
    }

    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getFeelsLike() { return feelsLike; }
    public void setFeelsLike(String feelsLike) { this.feelsLike = feelsLike; }

    public String getWindSpeed() { return windSpeed; }
    public void setWindSpeed(String windSpeed) { this.windSpeed = windSpeed; }

    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }

    public String getPressure() { return pressure; }
    public void setPressure(String pressure) { this.pressure = pressure; }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public String getMinTemp() { return minTemp; }
    public void setMinTemp(String minTemp) { this.minTemp = minTemp; }

    public String getMaxTemp() { return maxTemp; }
    public void setMaxTemp(String maxTemp) { this.maxTemp = maxTemp; }

    private String getWeatherConditionIcon(String weatherCondition) {
        if (weatherCondition == null) return "🌤️";
        switch (weatherCondition.toLowerCase()) {
            case "clear":       return "☀️";
            case "clouds":      return "☁️";
            case "rain":        return "🌧️";
            case "drizzle":     return "🌦️";
            case "thunderstorm":return "⛈️";
            case "snow":        return "❄️";
            case "mist":
            case "fog":
            case "haze":        return "🌫️";
            default:            return "🌤️";
        }
    }

    /** Returns a wind direction compass label from degrees */
    public static String windDegToDirection(double deg) {
        String[] dirs = {"N","NE","E","SE","S","SW","W","NW"};
        return dirs[(int) Math.round(deg / 45) % 8];
    }
}
