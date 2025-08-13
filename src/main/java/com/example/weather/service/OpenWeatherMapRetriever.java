package com.example.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class OpenWeatherMapRetriever implements WeatherDataRetriever {
    
    private final String apiKey;
    private final WebClient webClient;

    public OpenWeatherMapRetriever() {
        this.apiKey = "873179812fcf5caeb8c1c66ee7fd268e"; // In production, use @Value or environment variable
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
                    .block(); // For simplicity, using blocking call. In production, use reactive approach
                    
        } catch (WebClientResponseException e) {
            System.err.println("Error retrieving weather data: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return null;
        }
    }
}
