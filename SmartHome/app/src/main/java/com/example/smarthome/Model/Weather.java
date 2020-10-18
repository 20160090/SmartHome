package com.example.smarthome.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Weather {
    private Timestamp date;
    private String description;
    private double temp;
    private LocalTime sunset;
    private LocalTime sunrise;
    public Weather(){
    }
    public Weather(Timestamp date, String description, double temp, LocalTime sunrise, LocalTime sunset){
        this.date=date;
        this.description=description;
        this.temp=temp;
        this.sunrise=sunrise;
        this.sunset=sunset;
    }
    public Weather(LocalTime sunset){
        this.sunset=sunset;

    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemp() {
        return temp;
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
        return sunrise;
    }

    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }
}
