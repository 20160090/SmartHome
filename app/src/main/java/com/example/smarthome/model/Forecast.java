package com.example.smarthome.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Forecast {
    private LocalDateTime time;
    private String description;
    private double temp;

    public Forecast() {
        time = LocalDateTime.now();
        description = "notSetJet";
        temp=-100;
    }

    public Forecast(LocalDateTime date, String description, double temp) {
        this.time = date;
        this.description = description;
        this.temp = temp;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
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

}
