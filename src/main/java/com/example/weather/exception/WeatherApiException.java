package com.example.weather.exception;

public class WeatherApiException extends RuntimeException {
    private final int statusCode;

    public WeatherApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getUserMessage() {
        switch (statusCode) {
            case 401: return "Invalid API key. Please check your configuration.";
            case 404: return "City not found. Please check the spelling and try again.";
            case 429: return "API rate limit exceeded. Please try again in a few minutes.";
            case 503: return "Weather service is temporarily unavailable. Try again later.";
            default:  return "Unable to retrieve weather data. Please try again.";
        }
    }
}
