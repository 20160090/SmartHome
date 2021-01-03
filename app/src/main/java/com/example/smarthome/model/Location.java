package com.example.smarthome.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.smarthome.model.Device.State.RUNNING;

public class Location {
    private final ArrayList<Device> devices;
    private final ArrayList<Producer> producers;
    private String name;
    private int zip;
    private String city;
    private String country;
    private Weather weather;
    private ArrayList<Forecast> forecast;
    private String id;

    public Location() {
        this.devices = new ArrayList<>();
        this.producers = new ArrayList<>();
        this.zip = 0;
        this.name = "unknown";
        this.city = "unknown";
        this.country = "unknown";
        this.weather = new Weather();
        this.forecast = new ArrayList<>();
        this.id = "";
    }

    public Location(String id, String name, int zip, String city, String country) {
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.name = name;
        this.devices = new ArrayList<>();
        this.producers = new ArrayList<>();
        this.weather = new Weather();
        this.forecast = new ArrayList<>();
        this.id = id;
    }

    public Location(String id, String name, int zip, String city, String country, ArrayList<Device> devices, ArrayList<Producer> producers, Weather weather, ArrayList<Forecast> forecast) {
        this(id, name, zip, city, country);
        this.setDevices(devices);
        this.setProducers(producers);
        this.weather = weather;
        this.forecast = forecast;
    }


    public void addDevice(Device device) {
        Optional<Device> act = this.devices.stream().filter(d -> d.getSerialNumber().equals(device.getSerialNumber())).findFirst();
        if(act.isPresent()){
            Device actDevice = act.get();
            actDevice.setName(device.getName());
            actDevice.setCompany(device.getCompany());
            actDevice.setPossibleDeviceType(device.getPossibleDeviceType());
            actDevice.setAverageConsumption(device.getAverageConsumption());
            actDevice.setState(device.getState());
            actDevice.setId(device.getId());
        }
        else{
            this.devices.add(device);
        }
    }

    public void addProducer(Producer producer) {
        Optional<Producer> act = this.producers.stream().filter(p -> p.getId().equals(producer.getId())).findFirst();
        if (act.isPresent()) {
            act.get().setType(producer.getType());
            act.get().setCurrentlyProduced(producer.getCurrentlyProduced());
        }
        else{
            this.producers.add(producer);
        }

    }

    public int getRunningNum() {
        int count = 0;
        for (int i = 0; i < this.devices.size(); i++) {
            if (this.devices.get(i).getState().equals(RUNNING)) {
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

    public String getZipString() {
        return "" + this.zip + "";
    }

    public Weather getWeather() {
        return this.weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public ArrayList<Forecast> getForecast() {
        return this.forecast;
    }

    public void setForecast(ArrayList<Forecast> forecast) {
        this.forecast = forecast;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String locationInfo() {
        return this.name + " (ID: " + this.id + ") \n" + this.getZipString() + " " + this.city + " \n" + this.country;
    }
}
