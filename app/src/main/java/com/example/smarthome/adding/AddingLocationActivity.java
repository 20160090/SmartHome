package com.example.smarthome.adding;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.example.smarthome.R;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class AddingLocationActivity extends AppCompatActivity {
    private Parser parser;
    private User user;
    private Location location;
    private TextView pvId, name;
    private FirebaseFunctions mFunction;
    private final int LAUNCH_ADDING_ACTIVITY = 1;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_location);
        this.parser = Parser.getInstance();
        this.user = User.getInstance();
        this.mFunction = FirebaseFunctions.getInstance();


        Button con = findViewById(R.id.continueBtn);
        this.name = findViewById(R.id.nameEt);
        this.pvId = findViewById(R.id.pvEt);
        this.progressBar = findViewById(R.id.progressBar);
        this.pvId.setText("6dd05177-193f-4580-97bd-3331e3abe530");

        this.location = new Location();


        con.setOnClickListener(view -> {
            if (TextUtils.isEmpty(this.pvId.getText())) {
                Toast.makeText(AddingLocationActivity.this, getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
            } else {
                con.setClickable(false);
                con.setBackgroundResource(R.drawable.rounded_btn_disabled);
                this.progressBar.setVisibility(View.VISIBLE);
                callFronius();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.user.getLocations().remove(user.getLocations().size() - 1);

    }

    public Location parseLocation(JSONObject object) {
        this.location = this.parser.parseLocation(object);
        if (!this.name.getText().toString().equals("")) {
            this.location.setName(this.name.getText().toString());
        }
        return this.location;
    }


    public void callFronius() {
        Map<String, String> data = new HashMap<>();
        data.put("pvID", this.pvId.getText().toString());
        this.mFunction
                .getHttpsCallable("getFroniusLocation")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {
                    try {
                        //TODO: funktioniert nicht wenn error zurückgegeben
                        JSONObject object = new JSONObject(httpsCallableResult.getData().toString());
                        System.out.println(object);
                        parseLocation(object);
                        callFunctionAddLocationData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void callFunctionAddLocationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", this.user.getFirebaseUser().getEmail());
        data.put("zip", this.location.getZipString());
        data.put("country", this.location.getCountry());
        data.put("city", this.location.getCity());
        data.put("name", this.location.getName());
        this.mFunction
                .getHttpsCallable("addLocation")
                .call(data)
                .addOnSuccessListener(result -> {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(result.getData().toString());
                        this.location.setId(object.getString("locationID"));
                        this.parser.callGetWeatherCallback(this.location,null);
                        callFunctionAddPV();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });
    }

    private void callFunctionAddPV() {
        Map<String, String> data = new HashMap<>();
        data.put("email", this.user.getFirebaseUser().getEmail());
        data.put("locationID", this.location.getId());
        data.put("pvID", this.pvId.getText().toString());

        this.mFunction
                .getHttpsCallable("addPV")
                .call(data)
                .addOnSuccessListener(result -> {
                    System.out.println(result.getData().toString());
                    this.parser.callGetGeneratorCallback(this.location,null,null);
                    this.progressBar.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putString("locationID", this.location.getId());
                    bundle.putBoolean("adding", true);
                    Intent intent = new Intent(AddingLocationActivity.this, LocationDetailActivity.class);
                    intent.putExtras(bundle);
                    this.user.addLocation(this.location);
                    startActivityForResult(intent, LAUNCH_ADDING_ACTIVITY);

                    finish();
                });
    }
}