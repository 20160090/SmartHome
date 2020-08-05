package com.example.smarthome.model;

public class Producer {
    private String name, manufacturer, type;
    private double currentlyProduced;

    public Producer(){
        this("","","",0.0);
    }
    public Producer(String name, String manufacturer, String type){
        this(name,manufacturer,type,0.0);
    }
    public Producer(String name, String manufacturer, String type, double currentlyProduced){
        this.name=name;
        this.manufacturer=manufacturer;
        this.type=type;
        this.currentlyProduced=currentlyProduced;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getCurrentlyProduced() {
        return this.currentlyProduced;
    }

    public void setCurrentlyProduced(double currentlyProduced) {
        this.currentlyProduced = currentlyProduced;
    }
}
