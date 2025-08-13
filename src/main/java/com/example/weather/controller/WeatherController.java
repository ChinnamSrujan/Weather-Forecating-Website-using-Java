package com.example.weather.controller;

import com.example.weather.model.WeatherForecastData;
import com.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }



    @RequestMapping(value = "/weather", method = RequestMethod.POST)
    public String getWeatherForecastPage(@RequestParam String city, Model model) {
        List<WeatherForecastData> forecast = weatherService.getWeatherForecast(city);

        if (forecast.isEmpty()) {
            model.addAttribute("error", "Failed to retrieve weather data for " + city);
            return "index";
        }

        model.addAttribute("forecast", forecast);
        model.addAttribute("city", city);
        return "weather";
    }

    @RequestMapping(value = "/weather", method = RequestMethod.GET)
    public String getWeatherForecastPageGet(@RequestParam(required = false) String city, Model model) {
        if (city == null || city.trim().isEmpty()) {
            return "redirect:/";
        }

        List<WeatherForecastData> forecast = weatherService.getWeatherForecast(city);

        if (forecast.isEmpty()) {
            model.addAttribute("error", "Failed to retrieve weather data for " + city);
            return "index";
        }

        model.addAttribute("forecast", forecast);
        model.addAttribute("city", city);
        return "weather";
    }
}
