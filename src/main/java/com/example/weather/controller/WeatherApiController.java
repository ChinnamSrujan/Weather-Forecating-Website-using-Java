package com.example.weather.controller;

import com.example.weather.exception.WeatherApiException;
import com.example.weather.model.AirQuality;
import com.example.weather.model.CurrentWeather;
import com.example.weather.model.WeatherForecastData;
import com.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WeatherApiController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherApiController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /** Full 5-day / 3-hour interval forecast */
    @GetMapping("/weather/{city}")
    public ResponseEntity<?> getWeatherForecast(@PathVariable String city) {
        try {
            List<WeatherForecastData> forecast = weatherService.getWeatherForecast(city);
            if (forecast.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(forecast);
        } catch (WeatherApiException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getUserMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Unexpected error. Please try again."));
        }
    }

    /** Current conditions (temperature, wind, sunrise/sunset, etc.) */
    @GetMapping("/current/{city}")
    public ResponseEntity<?> getCurrentWeather(@PathVariable String city) {
        try {
            CurrentWeather cw = weatherService.getCurrentWeather(city);
            if (cw == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(cw);
        } catch (WeatherApiException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getUserMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Unexpected error. Please try again."));
        }
    }

    /** Daily summary — one card per day with min/max temps */
    @GetMapping("/daily/{city}")
    public ResponseEntity<?> getDailyForecast(@PathVariable String city) {
        try {
            List<Map<String, Object>> daily = weatherService.getDailyForecast(city);
            if (daily.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(daily);
        } catch (WeatherApiException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getUserMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Unexpected error. Please try again."));
        }
    }

    /** Air quality index and pollutant levels */
    @GetMapping("/airquality/{city}")
    public ResponseEntity<?> getAirQuality(@PathVariable String city) {
        try {
            AirQuality aq = weatherService.getAirQuality(city);
            if (aq == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(aq);
        } catch (WeatherApiException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getUserMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Unexpected error. Please try again."));
        }
    }

    /** Reverse geocode – get city name from lat/lon (used by geolocation feature) */
    @GetMapping("/current-by-coords")
    public ResponseEntity<?> getCurrentByCoords(@RequestParam double lat, @RequestParam double lon) {
        try {
            CurrentWeather cw = weatherService.getCurrentWeatherByCoords(lat, lon);
            if (cw == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(cw);
        } catch (WeatherApiException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("error", e.getUserMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Unexpected error."));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Weather API is running!");
    }
}
