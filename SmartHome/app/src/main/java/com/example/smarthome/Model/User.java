package com.example.smarthome.model;

import android.animation.Keyframe;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.adding.AddingLocationActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.TimeZone;

public class User {
    private static final User user = new User();
    private FirebaseUser firebaseUser;
    private ArrayList<Location> locations;
    private ArrayList<Company> companies;
    private Observable loadingData;

    public User() {
        this.locations = new ArrayList<>();
        this.companies = new ArrayList<>();
        loadingData = new Observable();
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

    public Observable getLoadingData() {
        return loadingData;
    }

    public void setLoadingData(Observable loadingData) {
        this.loadingData = loadingData;
    }

    public void addLocation(Location location){
        Optional<Location> old = this.locations.stream().filter(l -> l.getId().equals(location.getId())).findAny();
        if(old.isPresent()){
            old.get().setProducers(location.getProducers());
            old.get().setDevices(location.getDevices());
            old.get().setWeather(location.getWeather());
            old.get().setName(location.getName());
            old.get().setCity(location.getCity());
            old.get().setCountry(location.getCountry());
            old.get().setForecast(location.getForecast());
            old.get().setZip(location.getZip());
        }
        else{
            this.locations.add(location);
        }
    }
    public void loadingAll(){
       TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new LoadingAll(firebaseUser.getEmail()), (data) ->{

            locations=data;
            loadingData.notify();
        });
    }
}


