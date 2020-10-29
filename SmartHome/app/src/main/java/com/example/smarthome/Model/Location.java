package com.example.smarthome.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.example.smarthome.model.Device.State.RUNNING;

public class Location {
    private ArrayList<Device> devices;
    private ArrayList<Producer> producers;
    private String name;
    private int zip;
    private String city;
    private String country;
    private Weather weather;
    private Map<String, Weather> forecast;
    private String id;

    public Location() {
        this.devices = new ArrayList<>();
        this.producers = new ArrayList<>();
        this.zip = 0;
        this.name = "unknown";
        this.city="unknown";
        this.country = "unknown";
        this.weather = new Weather();
        this.forecast = new HashMap<String, Weather>();
        this.id="";
    }

    public Location(String id, String name, int zip, String city, String country) {
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.name = name;
        this.devices = new ArrayList<>();
        this.producers = new ArrayList<>();
        this.weather = new Weather();
        this.forecast = new HashMap<String, Weather>();
        this.id=id;
    }

    public Location(String id, String name, int zip, String city, String country, ArrayList<Device> devices, ArrayList<Producer> producers, Weather weather, HashMap<String, Weather> forecast) {
        this(id, name, zip, city, country);
        this.setDevices(devices);
        this.setProducers(producers);
    }


    public void addDevice(Device device) {
        this.devices.add(device);
        Collections.sort(devices);
    }

    public void addProducer(Producer producer) {
        this.producers.add(producer);
    }

    public int getRunningNum() {
        int count = 0;
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getState().equals(RUNNING)) {
                count++;
            }
        }
        return count;
    }

    //TODO: braucht man vlt nicht
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

    public Weather getWeather() {
        return this.weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Map<String, Weather> getForecast() {
        return this.forecast;
    }

    public void setForecast(Map<String, Weather> forecast) {
        this.forecast = forecast;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String locationInfo(){
        return this.name +" (ID: "+this.id+") \n"+this.getZipString()+" "+this.city+" \n"+this.country;
    }
}
