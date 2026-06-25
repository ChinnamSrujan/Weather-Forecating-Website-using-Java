package com.example.weather.service;

import com.example.weather.model.AirQuality;
import com.example.weather.model.CurrentWeather;
import com.example.weather.model.WeatherForecastData;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WeatherService {

    private final WeatherDataRetriever weatherDataRetriever;

    @Value("${weather.api.base-url}")
    private String forecastUrl;

    @Value("${weather.api.current-url}")
    private String currentUrl;

    @Value("${weather.api.air-quality-url}")
    private String airQualityUrl;

    @Autowired
    public WeatherService(WeatherDataRetriever weatherDataRetriever) {
        this.weatherDataRetriever = weatherDataRetriever;
    }

    // ─── Forecast (3-hour intervals, all 40 entries) ───────────────────────────

    @Cacheable(value = "forecasts", key = "#city.toLowerCase()")
    public List<WeatherForecastData> getWeatherForecast(String city) {
        String json = weatherDataRetriever.retrieveWeatherData(city, forecastUrl);
        return json != null ? parseForecast(json, city) : new ArrayList<>();
    }

    // ─── Current Weather ────────────────────────────────────────────────────────

    @Cacheable(value = "current", key = "#city.toLowerCase()")
    public CurrentWeather getCurrentWeather(String city) {
        String json = weatherDataRetriever.retrieveWeatherData(city, currentUrl);
        return json != null ? parseCurrentWeather(json) : null;
    }

    // ─── Daily summary (one card per day, min/max) ──────────────────────────────

    @Cacheable(value = "daily", key = "#city.toLowerCase()")
    public List<Map<String, Object>> getDailyForecast(String city) {
        List<WeatherForecastData> all = getWeatherForecast(city);
        // Group by date portion of "YYYY-MM-DD HH:mm:ss"
        Map<String, List<WeatherForecastData>> byDay = new LinkedHashMap<>();
        for (WeatherForecastData d : all) {
            String day = d.getDate().substring(0, 10);
            byDay.computeIfAbsent(day, k -> new ArrayList<>()).add(d);
        }

        List<Map<String, Object>> daily = new ArrayList<>();
        for (Map.Entry<String, List<WeatherForecastData>> entry : byDay.entrySet()) {
            List<WeatherForecastData> dayEntries = entry.getValue();
            double min = dayEntries.stream()
                    .mapToDouble(d -> parseDouble(d.getTemperature())).min().orElse(0);
            double max = dayEntries.stream()
                    .mapToDouble(d -> parseDouble(d.getTemperature())).max().orElse(0);
            // Use midday entry if available, otherwise first
            WeatherForecastData rep = dayEntries.stream()
                    .filter(d -> d.getDate().contains("12:00:00"))
                    .findFirst().orElse(dayEntries.get(0));

            Map<String, Object> dayMap = new LinkedHashMap<>();
            dayMap.put("date", entry.getKey());
            dayMap.put("minTemp", String.format("%.1f", min));
            dayMap.put("maxTemp", String.format("%.1f", max));
            dayMap.put("icon", rep.getIcon());
            dayMap.put("condition", rep.getWeatherCondition());
            dayMap.put("humidity", rep.getHumidity());
            dayMap.put("windSpeed", rep.getWindSpeed());
            daily.add(dayMap);
        }
        return daily;
    }

    // ─── Current Weather by Coords ──────────────────────────────────────────────

    @Cacheable(value = "currentCoords", key = "#lat + ',' + #lon")
    public CurrentWeather getCurrentWeatherByCoords(double lat, double lon) {
        try {
            String json = weatherDataRetriever.retrieveWeatherDataByCoords(lat, lon, currentUrl);
            return json != null ? parseCurrentWeather(json) : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ─── Air Quality ────────────────────────────────────────────────────────────

    @Cacheable(value = "airquality", key = "#city.toLowerCase()")
    public AirQuality getAirQuality(String city) {
        // First get coordinates from current weather
        String currentJson = weatherDataRetriever.retrieveWeatherData(city, currentUrl);
        if (currentJson == null) return null;
        JsonObject currentObj = JsonParser.parseString(currentJson).getAsJsonObject();
        JsonObject coord = currentObj.getAsJsonObject("coord");
        double lat = coord.get("lat").getAsDouble();
        double lon = coord.get("lon").getAsDouble();

        String aqJson = weatherDataRetriever.retrieveWeatherDataByCoords(lat, lon, airQualityUrl);
        return aqJson != null ? parseAirQuality(aqJson) : null;
    }

    // ─── Parsers ────────────────────────────────────────────────────────────────

    private List<WeatherForecastData> parseForecast(String json, String city) {
        List<WeatherForecastData> list = new ArrayList<>();
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray forecastArray = root.getAsJsonArray("list");
            for (JsonElement el : forecastArray) {
                JsonObject obj = el.getAsJsonObject();
                WeatherForecastData d = new WeatherForecastData();
                d.setCity(city);
                d.setDate(obj.get("dt_txt").getAsString());

                JsonObject main = obj.getAsJsonObject("main");
                d.setTemperature(formatDouble(main.get("temp").getAsDouble()));
                d.setFeelsLike(formatDouble(main.get("feels_like").getAsDouble()));
                d.setHumidity(String.valueOf(main.get("humidity").getAsInt()));
                d.setPressure(String.valueOf(main.get("pressure").getAsInt()));
                d.setMinTemp(formatDouble(main.get("temp_min").getAsDouble()));
                d.setMaxTemp(formatDouble(main.get("temp_max").getAsDouble()));

                JsonArray weatherArr = obj.getAsJsonArray("weather");
                JsonObject w = weatherArr.get(0).getAsJsonObject();
                d.setWeatherCondition(w.get("main").getAsString());
                d.setWeatherDescription(capitalize(w.get("description").getAsString()));

                if (obj.has("wind")) {
                    JsonObject wind = obj.getAsJsonObject("wind");
                    d.setWindSpeed(formatDouble(wind.get("speed").getAsDouble()));
                    if (wind.has("deg")) {
                        d.setWindDirection(WeatherForecastData.windDegToDirection(wind.get("deg").getAsDouble()));
                    }
                }
                if (obj.has("visibility")) {
                    d.setVisibility(String.valueOf(obj.get("visibility").getAsInt() / 1000) + " km");
                }
                list.add(d);
            }
        } catch (JsonParseException e) {
            System.err.println("Error parsing forecast data: " + e.getMessage());
        }
        return list;
    }

    private CurrentWeather parseCurrentWeather(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            CurrentWeather cw = new CurrentWeather();
            cw.setCity(root.get("name").getAsString());

            JsonObject sys = root.getAsJsonObject("sys");
            cw.setCountry(sys.get("country").getAsString());
            cw.setSunrise(sys.get("sunrise").getAsLong());
            cw.setSunset(sys.get("sunset").getAsLong());

            JsonObject main = root.getAsJsonObject("main");
            cw.setTemperature(main.get("temp").getAsDouble());
            cw.setFeelsLike(main.get("feels_like").getAsDouble());
            cw.setTempMin(main.get("temp_min").getAsDouble());
            cw.setTempMax(main.get("temp_max").getAsDouble());
            cw.setHumidity(main.get("humidity").getAsInt());
            cw.setPressure(main.get("pressure").getAsInt());

            if (root.has("visibility")) {
                cw.setVisibility(root.get("visibility").getAsInt());
            }
            if (root.has("timezone")) {
                cw.setTimezone(root.get("timezone").getAsLong());
            }

            JsonObject wind = root.getAsJsonObject("wind");
            cw.setWindSpeed(wind.get("speed").getAsDouble());
            if (wind.has("deg")) {
                cw.setWindDirection(WeatherForecastData.windDegToDirection(wind.get("deg").getAsDouble()));
            }

            JsonArray weatherArr = root.getAsJsonArray("weather");
            JsonObject w = weatherArr.get(0).getAsJsonObject();
            cw.setWeatherCondition(w.get("main").getAsString());
            cw.setWeatherDescription(capitalize(w.get("description").getAsString()));
            cw.setIcon(w.get("icon").getAsString());

            return cw;
        } catch (Exception e) {
            System.err.println("Error parsing current weather: " + e.getMessage());
            return null;
        }
    }

    private AirQuality parseAirQuality(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject item = root.getAsJsonArray("list").get(0).getAsJsonObject();
            JsonObject components = item.getAsJsonObject("components");
            JsonObject mainAq = item.getAsJsonObject("main");

            AirQuality aq = new AirQuality();
            aq.setAqi(mainAq.get("aqi").getAsInt());
            aq.setCo(components.get("co").getAsDouble());
            aq.setNo2(components.get("no2").getAsDouble());
            aq.setO3(components.get("o3").getAsDouble());
            aq.setPm2_5(components.get("pm2_5").getAsDouble());
            aq.setPm10(components.get("pm10").getAsDouble());
            return aq;
        } catch (Exception e) {
            System.err.println("Error parsing air quality: " + e.getMessage());
            return null;
        }
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private String formatDouble(double val) {
        return String.format("%.1f", val);
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
