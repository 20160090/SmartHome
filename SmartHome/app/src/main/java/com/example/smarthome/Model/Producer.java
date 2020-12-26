package com.example.smarthome.model;

public class Producer {
    private String type;
    private String id;
    private double currentlyProduced;

    public Producer() {
        this("", "", 0.0);
    }

    public Producer(String id, String type, double currentlyProduced) {
        this.id = id;
        this.type = type;
        this.currentlyProduced = currentlyProduced;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
