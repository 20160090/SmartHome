package com.example.smarthome.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Weather {
    private Forecast weather;
    private LocalTime sunset;
    private LocalTime sunrise;

    public Weather() {
        this.weather = new Forecast();
        this.sunrise = LocalTime.now();
        this.sunset = LocalTime.now();
    }

    public Weather(Forecast weather, LocalTime sunrise, LocalTime sunset) {
        this.weather=weather;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public Forecast getWeather() {
        return this.weather;
    }

    public void setWeather(Forecast weather) {
        this.weather = weather;
    }

    public LocalTime getSunset() {
        return this.sunset;
    }

    public void setSunset(LocalTime sunset) {
        this.sunset = sunset;
    }

    public LocalTime getSunrise() {
        return this.sunrise;
    }

    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }
}
