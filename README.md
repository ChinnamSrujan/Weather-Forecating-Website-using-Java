# Weather Forecasting Project in Java

## 🌐 Live Demo

**[https://weather-forecating-website-using-java-2.onrender.com/](https://weather-forecating-website-using-java-2.onrender.com/)**

> ⚠️ Hosted on Render free tier — may take 30–60 seconds to wake up on first visit.

---

## Problem Statement

The objective of this project is to develop a weather forecasting system in Java that provides accurate and timely weather predictions for a specified location. The system utilizes available meteorological data to generate forecasts for upcoming days, including information on temperature, precipitation, wind speed, and atmospheric conditions. It is user-friendly, allowing users to input their desired location and retrieve forecasted weather information in a clear and understandable format.

We leverage Java programming and the OpenWeatherMap API to provide accurate and up-to-date weather forecasts.

---

## Introduction

Weather forecasting is the science and practice of predicting atmospheric conditions for a specific location and time period in the future. It is a crucial field that provides valuable information about temperature, precipitation, wind speed, humidity, and other atmospheric phenomena. Accurate weather forecasts enable individuals, businesses, and governments to plan activities, make informed decisions, and take necessary precautions.

---

## Project Overview

The Weather Forecasting System collects meteorological data and presents it in a rich, user-friendly web interface. Key components include data collection, JSON parsing, Spring Boot REST APIs, caching, and a responsive frontend with dynamic theming.

---

## ✨ Features

- 🌡️ **Current Weather** — Temperature, feels-like, humidity, wind speed & direction, pressure, visibility
- 📅 **5-Day Daily Summary** — Min/max temperature cards grouped by day
- 🕐 **Hourly Forecast Carousel** — Next 24 hours in a scrollable strip
- 📈 **Temperature Trend Chart** — Interactive Chart.js line graph
- 🌅 **Sunrise / Sunset Arc** — Canvas animation showing sun position
- 🌿 **Air Quality Index** — PM2.5, PM10, NO₂, O₃, CO levels with AQI badge
- 👗 **Clothing Suggestions** — Smart tips based on temperature and conditions
- 🎨 **Dynamic Backgrounds** — Background changes with weather condition
- 🌡️ **°C / °F Toggle** — Switch units instantly without re-fetching
- 📍 **Geolocation** — Auto-detect your city with one click
- 🕐 **Search History** — Last 5 searched cities saved locally
- ⭐ **Favourite Cities** — Bookmark cities for quick access
- ⚡ **Instant Search** — Live API search with skeleton loading UI
- 🚀 **Spring Cache** — 15-minute Caffeine cache to reduce API calls
- 🔒 **Secure API Key** — Loaded from environment variables

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Templating | Thymeleaf |
| HTTP Client | Spring WebFlux (WebClient) |
| JSON Parsing | Gson |
| Caching | Spring Cache + Caffeine |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Charts | Chart.js |
| Build Tool | Maven |
| Deployment | Render (Docker) |

---

## Key Objectives

1. **Data Retrieval** — Fetch weather data from the OpenWeatherMap API
2. **Data Parsing** — Process and parse JSON to extract relevant weather information
3. **Data Presentation** — Display forecasts in a clear, visually rich format
4. **Modularity** — Well-structured, modular, and reusable components
5. **Performance** — Caching, optimized builds, and responsive UI

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- OpenWeatherMap API key (free at [openweathermap.org](https://openweathermap.org/api))

### Run Locally

```bash
# Clone the repository
git clone https://github.com/ChinnamSrujan/Weather-Forecating-Website-using-Java.git
cd Weather-Forecating-Website-using-Java

# Set your API key (optional — has a fallback key)
export WEATHER_API_KEY=your_api_key_here

# Run the app
mvn spring-boot:run
```

Open [http://localhost:8081](http://localhost:8081) in your browser.

### API Endpoints

| Endpoint | Description |
|---|---|
| `GET /api/current/{city}` | Current weather conditions |
| `GET /api/weather/{city}` | Full 5-day / 3-hour forecast |
| `GET /api/daily/{city}` | Daily summary with min/max |
| `GET /api/airquality/{city}` | Air quality index |
| `GET /api/health` | Health check |

---

## Deployment

Deployed on **Render** using Docker.  
**Live URL:** [https://weather-forecating-website-using-java-2.onrender.com/](https://weather-forecating-website-using-java-2.onrender.com/)
