package com.example.smarthome.Model;

import java.util.ArrayList;

public class User {
    private static User user = new User();
    private ArrayList<Location> locations = new ArrayList<Location>();
    private String displayName = "", role = "";

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
}


