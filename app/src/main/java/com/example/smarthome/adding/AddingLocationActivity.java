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
    private Button add;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_location);
        this.parser = Parser.getInstance();
        this.user = User.getInstance();
        this.mFunction = FirebaseFunctions.getInstance();


        this.add = findViewById(R.id.continueBtn);
        this.name = findViewById(R.id.nameEt);
        this.pvId = findViewById(R.id.pvEt);
        this.progressBar = findViewById(R.id.progressBar);
        this.pvId.setText("6dd05177-193f-4580-97bd-3331e3abe530");

        this.location = new Location();


        this.add.setOnClickListener(view -> {
            if (TextUtils.isEmpty(this.pvId.getText())) {
                Toast.makeText(AddingLocationActivity.this, getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
            } else {

                this.add.setClickable(false);
                this.add.setBackgroundResource(R.drawable.rounded_btn_disabled);
                this.progressBar.setVisibility(View.VISIBLE);
                callFroniusCallback();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.user.getLocations().remove(user.getLocations().size() - 1);

    }

    public boolean parseLocation(JSONObject object) {
        Location location = this.parser.parseLocation(object);
        if(location==null){
            this.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "ungültige Photovoltaik-ID", Toast.LENGTH_LONG);
            return false;
        }
        this.location = this.parser.parseLocation(object);
        if (!this.name.getText().toString().equals("")) {
            this.location.setName(this.name.getText().toString());
        }
        return true;
    }


    public void callFroniusCallback() {
        Map<String, String> data = new HashMap<>();
        data.put("pvID", this.pvId.getText().toString());
        this.mFunction
                .getHttpsCallable("getFroniusLocation")
                .call(data)
                .addOnSuccessListener(httpsCallableResult -> {
                    try {
                        //TODO: funktioniert nicht wenn error zurückgegeben
                        JSONObject object = new JSONObject(httpsCallableResult.getData().toString());
                        if(parseLocation(object)){
                            callFunctionAddLocationData();
                        }
                        else{
                            Toast.makeText(this, "ungültige Photovoltaik-ID", Toast.LENGTH_SHORT).show();
                            this.progressBar.setVisibility(View.GONE);
                            this.add.setClickable(true);
                            this.add.setBackgroundResource(R.drawable.round_add_button);
                        }
                    } catch (JSONException e) {
                        e.getMessage();
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
                    this.parser.callGetGeneratorCallback(this.location,null,t->{
                        this.parser.callGetWeatherCallback(this.location, t2->{
                            this.parser.callGetForecastCallback(this.location, t3->{
                                this.progressBar.setVisibility(View.GONE);
                                Bundle bundle = new Bundle();
                                bundle.putString("locationID", this.location.getId());
                                bundle.putBoolean("adding", true);
                                Intent intent = new Intent(AddingLocationActivity.this, LocationDetailActivity.class);
                                intent.putExtras(bundle);
                                this.user.addLocation(this.location);
                                startActivityForResult(intent, LAUNCH_ADDING_ACTIVITY);
                                finish();
                                return 0;
                            });
                            return 0;
                        });

                        return 0;
                    });

                });
    }
}