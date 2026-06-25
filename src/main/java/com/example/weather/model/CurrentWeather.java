package com.example.weather.model;

public class CurrentWeather {
    private String city;
    private String country;
    private double temperature;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private int humidity;
    private int pressure;
    private int visibility;
    private double windSpeed;
    private String windDirection;
    private String weatherCondition;
    private String weatherDescription;
    private String icon;
    private long sunrise;
    private long sunset;
    private long timezone;

    public CurrentWeather() {}

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public int getVisibility() { return visibility; }
    public void setVisibility(int visibility) { this.visibility = visibility; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }

    public String getWeatherCondition() { return weatherCondition; }
    public void setWeatherCondition(String weatherCondition) { this.weatherCondition = weatherCondition; }

    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public long getSunrise() { return sunrise; }
    public void setSunrise(long sunrise) { this.sunrise = sunrise; }

    public long getSunset() { return sunset; }
    public void setSunset(long sunset) { this.sunset = sunset; }

    public long getTimezone() { return timezone; }
    public void setTimezone(long timezone) { this.timezone = timezone; }

    public String getWeatherEmoji() {
        if (weatherCondition == null) return "🌤️";
        switch (weatherCondition.toLowerCase()) {
            case "clear":        return "☀️";
            case "clouds":       return "☁️";
            case "rain":         return "🌧️";
            case "drizzle":      return "🌦️";
            case "thunderstorm": return "⛈️";
            case "snow":         return "❄️";
            case "mist":
            case "fog":
            case "haze":         return "🌫️";
            default:             return "🌤️";
        }
    }

    /** Returns the CSS class name used for dynamic background theming */
    public String getBackgroundClass() {
        if (weatherCondition == null) return "bg-default";
        switch (weatherCondition.toLowerCase()) {
            case "clear":        return "bg-clear";
            case "clouds":       return "bg-cloudy";
            case "rain":
            case "drizzle":      return "bg-rainy";
            case "thunderstorm": return "bg-storm";
            case "snow":         return "bg-snowy";
            case "mist":
            case "fog":
            case "haze":         return "bg-foggy";
            default:             return "bg-default";
        }
    }

    /** Clothing suggestion based on temperature and condition */
    public String getClothingSuggestion() {
        StringBuilder sb = new StringBuilder();
        if (temperature < 0) {
            sb.append("🧥 Heavy winter coat, gloves, and a hat are essential.");
        } else if (temperature < 10) {
            sb.append("🧥 Wear a warm coat and layers.");
        } else if (temperature < 18) {
            sb.append("🧣 A jacket or light coat is recommended.");
        } else if (temperature < 25) {
            sb.append("👕 Comfortable long-sleeve or light jacket should do.");
        } else {
            sb.append("🩳 Light, breathable clothing — it's warm out.");
        }

        String cond = weatherCondition != null ? weatherCondition.toLowerCase() : "";
        if (cond.contains("rain") || cond.contains("drizzle") || cond.contains("thunderstorm")) {
            sb.append(" ☂️ Don't forget an umbrella!");
        } else if (cond.contains("snow")) {
            sb.append(" 👢 Waterproof boots are a good idea.");
        } else if (cond.contains("clear") && temperature > 22) {
            sb.append(" 🕶️ Sunglasses and sunscreen recommended.");
        }
        return sb.toString();
    }
}
