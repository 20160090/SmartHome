package com.example.smarthome.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Weather {
    private LocalDateTime date;
    private String description;
    private double temp;
    private LocalTime sunset;
    private LocalTime sunrise;

    public Weather() {
        date = LocalDateTime.now();
        description = "notSetJet";
        temp=-100;
        sunrise = LocalTime.now();
        sunset = LocalTime.now();
    }

    public Weather(LocalDateTime date, String description, double temp, LocalTime sunrise, LocalTime sunset) {
        this.date = date;
        this.description = description;
        this.temp = temp;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemp() {
        return this.temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public LocalTime getSunset() {
        return sunset;
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