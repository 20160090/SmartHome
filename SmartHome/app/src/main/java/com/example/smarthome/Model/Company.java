package com.example.smarthome.model;

import java.util.ArrayList;

public class Company {
    String id, name;
    ArrayList<PossibleDeviceType> devices;

    public Company(){
        this.id="";
        this.name="";
        this.devices= new ArrayList<>();
    }
    public Company(String id, String name){
        this.id=id;
        this.name = name;
        this.devices = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PossibleDeviceType> getDevices() {
        return this.devices;
    }

    public void setDevices(ArrayList<PossibleDeviceType> devices) {
        this.devices = devices;
    }
}
