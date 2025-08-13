package com.example.weather.controller;

import com.example.weather.model.WeatherForecastData;
import com.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WeatherApiController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherApiController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather/{city}")
    public ResponseEntity<List<WeatherForecastData>> getWeatherForecast(@PathVariable String city) {
        try {
            List<WeatherForecastData> forecast = weatherService.getWeatherForecast(city);
            
            if (forecast.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(forecast);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Weather API is running!");
    }
}
