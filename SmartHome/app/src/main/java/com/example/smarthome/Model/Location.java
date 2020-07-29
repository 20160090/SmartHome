package com.example.smarthome.Model;

import java.util.ArrayList;

public class Location {
    private ArrayList<Device> devices;
    private ArrayList<Producer> producers;
    private String name;
    private int zip;
    private String city;
    private String country;

    public Location() {
        this.devices = new ArrayList<Device>();
        this.producers = new ArrayList<Producer>();
        this.zip = 0;
        this.name = "unknown";
    }

    public Location(String name, int zip, String city, String country) {
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.name = name;
        this.devices = new ArrayList<Device>();
        this.producers = new ArrayList<Producer>();
    }

    public Location(String name, int zip, String city, String country, ArrayList<Device> devices, ArrayList<Producer> producers) {
        this(name, zip, city, country);
        this.setDevices(devices);
        this.setProducers(producers);
    }


    public void addDevice(Device device) {
        this.devices.add(device);
        //Server??
    }

    public void addProducer(Producer producer) {
        this.producers.add(producer);
    }

    public int getRunningNum() {
        int count = 0;
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getState() == Device.RUNNING) {
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
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ArrayList<Producer> getProducers() {
        return this.producers;
    }

    public void setProducers(ArrayList<Producer> producers) {
        this.devices.clear();
        this.devices.addAll(devices);
    }

    public boolean equalsNoDevices(Location location) {
        if (this.name.equals(location.getName()) && this.country.equals(location.getCountry()) && this.zip == location.getZip()&& this.city.equals(location.getCity())) {
            return true;
        } else {
            return false;
        }
    }

}
