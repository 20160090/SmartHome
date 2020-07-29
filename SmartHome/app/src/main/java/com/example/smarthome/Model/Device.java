package com.example.smarthome.Model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Device {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ RUNNING, NOT_RUNNING, SHOULD_BE_RUNNING, SHOULD_NOT_BE_RUNNING})
    @interface DeviceState{}
    public static final String RUNNING="läuft";
    public static final String NOT_RUNNING="Sollte nicht eingeschalten werden";
    public static final String SHOULD_BE_RUNNING="Sollte eingeschalten werden";
    public static final String SHOULD_NOT_BE_RUNNING="Sollte nicht eingeschalten sein";




    private String name, type, manufacturer;
    private String state;
    private double averageConsumption;

    public Device() {
        this.name = "unknown";
        this.type = "unknown";
        this.manufacturer = "unknown";
        this.state = NOT_RUNNING;
        this.averageConsumption = 0.0;
    }

    public Device(String name, String type, String manufacturer, String state, double averageConsumption) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.manufacturer = manufacturer;
        this.averageConsumption = averageConsumption;
    }
    public Device (String name, String type, String manufacturer){
        this(name,type,manufacturer,NOT_RUNNING,0.0);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @DeviceState
    public String getState() {
        return state;
    }
    @DeviceState
    public void setState(String state) {
        this.state = state;
    }

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }
}

