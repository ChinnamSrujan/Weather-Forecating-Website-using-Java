package com.example.weather.service;

import com.example.weather.exception.WeatherApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class OpenWeatherMapRetriever implements WeatherDataRetriever {

    @Value("${weather.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public OpenWeatherMapRetriever() {
        this.webClient = WebClient.builder().build();
    }

    @Override
    public String retrieveWeatherData(String city, String url) {
        try {
            String fullUrl = url + "?q=" + city + "&units=metric&appid=" + apiKey;
            return webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new WeatherApiException(e.getMessage(), e.getStatusCode().value());
        } catch (Exception e) {
            throw new WeatherApiException("Network error: " + e.getMessage(), 503);
        }
    }

    @Override
    public String retrieveWeatherDataByCoords(double lat, double lon, String url) {
        try {
            String fullUrl = url + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
            return webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new WeatherApiException(e.getMessage(), e.getStatusCode().value());
        } catch (Exception e) {
            throw new WeatherApiException("Network error: " + e.getMessage(), 503);
        }
    }
}
