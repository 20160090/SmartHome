package com.example.smarthome.model;

import java.util.ArrayList;
import java.util.Collections;

public class Location {
    private ArrayList<Device> devices;
    private ArrayList<Producer> producers;
    private String name;
    private int zip;
    private String city;
    private String country;
    private Weather weather;

    public Location() {
        this.devices = new ArrayList<>();
        this.producers = new ArrayList<>();
        this.zip = 0;
        this.name = "unknown";
        this.city="unknown";
        this.country = "unknown";
        this.weather = new Weather();
    }

    public Location(String name, int zip, String city, String country) {
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.name = name;
        this.devices = new ArrayList<>();
        this.producers = new ArrayList<>();
    }

    public Location(String name, int zip, String city, String country, ArrayList<Device> devices, ArrayList<Producer> producers) {
        this(name, zip, city, country);
        this.setDevices(devices);
        this.setProducers(producers);
    }


    public void addDevice(Device device) {
        this.devices.add(device);
        Collections.sort(devices);
        //Server??
    }

    public void addProducer(Producer producer) {
        this.producers.add(producer);
    }

    public int getRunningNum() {
        int count = 0;
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getState().equals(Device.RUNNING)) {
                count++;
            }
        }
        return count;
    }

    public double getCurrentEnergy() {
        double energy = 0;
        for (int i = 0; i < producers.size(); i++) {
            energy += producers.get(i).getCurrentlyProduced();
        }
        return energy;
    }

    public ArrayList<Device> getDevices() {
        return this.devices;
    }

    public void setDevices(ArrayList<Device> devices) {
        Collections.sort(devices);
        this.devices.clear();
        this.devices.addAll(devices);
    }

    public int getZip() {
        return this.zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ArrayList<Producer> getProducers() {
        return this.producers;
    }

    public void setProducers(ArrayList<Producer> producers) {
        this.producers.clear();
        this.producers.addAll(producers);
    }
    public String getZipString(){
        return ""+this.zip+"";
    }

}
