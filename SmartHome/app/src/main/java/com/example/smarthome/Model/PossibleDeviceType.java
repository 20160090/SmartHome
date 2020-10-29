package com.example.smarthome.model;

public class PossibleDeviceType {
    String id, type;
    double averageConsumption;

    public PossibleDeviceType(){
        this.id="";
        this.type="";
        this.averageConsumption=0.0;
    }
    public PossibleDeviceType(String id, String type, double averageConsumption){
        this.id=id;
        this.type = type;
        this.averageConsumption = averageConsumption;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }
}
