package com.example.weather.model;

public class AirQuality {
    private int aqi;           // Air Quality Index 1–5
    private double co;         // Carbon monoxide μg/m³
    private double no2;        // Nitrogen dioxide μg/m³
    private double o3;         // Ozone μg/m³
    private double pm2_5;      // Fine particles μg/m³
    private double pm10;       // Coarse particles μg/m³

    public AirQuality() {}

    public int getAqi() { return aqi; }
    public void setAqi(int aqi) { this.aqi = aqi; }

    public double getCo() { return co; }
    public void setCo(double co) { this.co = co; }

    public double getNo2() { return no2; }
    public void setNo2(double no2) { this.no2 = no2; }

    public double getO3() { return o3; }
    public void setO3(double o3) { this.o3 = o3; }

    public double getPm2_5() { return pm2_5; }
    public void setPm2_5(double pm2_5) { this.pm2_5 = pm2_5; }

    public double getPm10() { return pm10; }
    public void setPm10(double pm10) { this.pm10 = pm10; }

    /** Human-readable AQI label */
    public String getAqiLabel() {
        switch (aqi) {
            case 1: return "Good";
            case 2: return "Fair";
            case 3: return "Moderate";
            case 4: return "Poor";
            case 5: return "Very Poor";
            default: return "Unknown";
        }
    }

    /** CSS class for colour-coding the AQI badge */
    public String getAqiClass() {
        switch (aqi) {
            case 1: return "aqi-good";
            case 2: return "aqi-fair";
            case 3: return "aqi-moderate";
            case 4: return "aqi-poor";
            case 5: return "aqi-very-poor";
            default: return "aqi-unknown";
        }
    }
}
