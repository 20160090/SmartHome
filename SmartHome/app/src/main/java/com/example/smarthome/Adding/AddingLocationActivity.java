package com.example.smarthome.adding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
    private int locationPos;
    private TextView pvId, name;
    private FirebaseFunctions mFunction;
    private final int LAUNCH_ADDING_ACTIVITY = 1;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_location);
        this.parser = new Parser();
        this.user = User.getInstance();
        this.mFunction = FirebaseFunctions.getInstance();
        readBundle(getIntent().getExtras());

        Button con = findViewById(R.id.continueBtn);
        TextView heading = findViewById(R.id.headingTv);
        this.name = findViewById(R.id.nameEt);
        this.pvId = findViewById(R.id.pvEt);
        this.progressBar = findViewById(R.id.progressBar);
        this.pvId.setText("6dd05177-193f-4580-97bd-3331e3abe530");

        if (this.locationPos >= 0) {
            this.location = user.getLocations().get(this.locationPos);
            this.name.setText(location.getName());
            con.setText(getResources().getString(R.string.save));
            heading.setText(getResources().getString(R.string.edit_location));
        } else {
            this.location = new Location();
        }

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

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.locationPos = bundle.getInt("locationPos");
        } else {
            this.locationPos = -1;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.locationPos < 0) {
            this.user.getLocations().remove(user.getLocations().size() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_ADDING_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    public void saveUserLocation(JSONObject object) {
        this.location = this.parser.parseLocation(object);
        if (!this.name.getText().toString().isEmpty()) {
            this.location.setName(this.name.getText().toString());
        }
        this.user.getLocations().add(this.location);
    }


    public void callFronius() {
        Map<String, String> data = new HashMap<>();
        data.put("pvID", this.pvId.getText().toString());
        data.put("name", this.name.getText().toString());
        this.mFunction
                .getHttpsCallable("getFroniusLocation")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {
                    try {
                        JSONObject object = new JSONObject(httpsCallableResult.getData().toString());
                        saveUserLocation(object);
                        callFunctionAddLocationData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
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

                        this.parser.callGetWeather(this.location);
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
                    this.parser.callGenerator(this.location);
                    this.progressBar.setVisibility(View.GONE);
                    if (this.locationPos < 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("locationID", this.location.getId());
                        bundle.putBoolean("adding", true);
                        Intent intent = new Intent(AddingLocationActivity.this, LocationDetailActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, LAUNCH_ADDING_ACTIVITY);
                        finish();
                    } else {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });
    }
}