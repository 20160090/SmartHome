package com.example.smarthome.model;

import java.util.ArrayList;

public class User {
    private static final User user = new User();
    private ArrayList<Location> locations = new ArrayList<>();
    private String displayName = "", role = "";
    private Weather weather = new Weather();

    public User() {
    }

    public static User getInstance() {
        return user;
    }


    public void addLocation(Location location){
        this.locations.add(location);
    }
    public ArrayList<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Weather getWeather() {
        return this.weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}


