package com.example.smarthome.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.adding.AddingLocationActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.TimeZone;

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

}


