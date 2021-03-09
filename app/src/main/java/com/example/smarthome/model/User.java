package com.example.smarthome.model;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Optional;

public class User {
    private static final User user = new User();
    private FirebaseUser firebaseUser;
    private ArrayList<Location> locations;
    private ArrayList<Company> companies;

    public User() {
        this.locations = new ArrayList<>();
        this.companies = new ArrayList<>();
    }

    public static User getInstance() {
        return user;
    }

    public FirebaseUser getFirebaseUser() {
        return this.firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public ArrayList<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<Company> getCompanies() {
        return this.companies;
    }

    public void setCompanies(ArrayList<Company> companies) {
        this.companies = companies;
    }

    public void addLocation(Location location) {
        Optional<Location> old = this.locations.stream().filter(l -> l.getId().equals(location.getId())).findAny();
        if (old.isPresent()) {
            old.get().setProducers(location.getProducers());
            old.get().setDevices(location.getDevices());
            old.get().setWeather(location.getWeather());
            old.get().setName(location.getName());
            old.get().setCity(location.getCity());
            old.get().setCountry(location.getCountry());
            old.get().setForecast(location.getForecast());
            old.get().setZip(location.getZip());
        } else {
            this.locations.add(location);
        }
    }


}


