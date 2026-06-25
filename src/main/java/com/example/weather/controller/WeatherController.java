package com.example.weather.controller;

import com.example.weather.exception.WeatherApiException;
import com.example.weather.model.AirQuality;
import com.example.weather.model.CurrentWeather;
import com.example.weather.model.WeatherForecastData;
import com.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/weather")
    public String getWeatherPost(@RequestParam String city, Model model) {
        return loadWeatherPage(city, model);
    }

    @GetMapping("/weather")
    public String getWeatherGet(@RequestParam(required = false) String city, Model model) {
        if (city == null || city.trim().isEmpty()) return "redirect:/";
        return loadWeatherPage(city, model);
    }

    private String loadWeatherPage(String city, Model model) {
        try {
            List<WeatherForecastData> forecast = weatherService.getWeatherForecast(city);
            if (forecast.isEmpty()) {
                model.addAttribute("error", "No weather data found for \"" + city + "\". Check the city name and try again.");
                return "index";
            }

            CurrentWeather current = weatherService.getCurrentWeather(city);
            List<Map<String, Object>> daily = weatherService.getDailyForecast(city);

            AirQuality airQuality = null;
            try {
                airQuality = weatherService.getAirQuality(city);
            } catch (Exception ignored) {
                // Air quality is optional — don't fail the whole page
            }

            model.addAttribute("forecast", forecast);
            model.addAttribute("current", current);
            model.addAttribute("daily", daily);
            model.addAttribute("airQuality", airQuality);
            model.addAttribute("city", city);

            // Background theme driven by current weather condition
            String bgClass = current != null ? current.getBackgroundClass() : "bg-default";
            model.addAttribute("bgClass", bgClass);

            return "weather";
        } catch (WeatherApiException e) {
            model.addAttribute("error", e.getUserMessage());
            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Something went wrong. Please try again.");
            return "index";
        }
    }
}
