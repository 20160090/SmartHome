package com.example.smarthome.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.example.smarthome.R;
import com.example.smarthome.model.Weather;
import com.github.pwittchen.weathericonview.WeatherIconView;
import com.google.firebase.functions.FirebaseFunctions;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LocationFragment extends Fragment {

    private Location location;
    private User user;
    private String locationID;
    private  Parser parser;
    private FirebaseFunctions mFunctions;

    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment newInstance(String locationID) {
        Bundle bundle = new Bundle();
        bundle.putString("locationID", locationID);

        LocationFragment locationFragment = new LocationFragment();
        locationFragment.setArguments(bundle);
        return locationFragment;
    }

    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.locationID = bundle.getString("locationID");
            this.location = this.user.getLocations().stream().filter(l -> l.getId().equals(bundle.getString("locationID"))).findFirst().get();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = User.getInstance();
        this.mFunctions = FirebaseFunctions.getInstance();
        this.parser = Parser.getInstance();
        readBundle(getArguments());
        //  Parser parser = new Parser();
        //  parser.parseOneLocation(location);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        view.setOnClickListener(view1 -> {
            HomeFragment homeFragment = ((HomeFragment) LocationFragment.this.getParentFragment());
            homeFragment.loadingDetail();
            parser.callGetGeneratorCallback(location,null, t1 -> {
                parser.callGetDevicesCallback(location, t2 -> {
                    parser.callGetWeatherCallback(location, t3 -> {
                        parser.callGetForecastCallback(location, t4 -> {
                            Intent intent = new Intent(getActivity(), LocationDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("locationID", this.locationID);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            homeFragmentLocation();
                            return 0;
                        });
                        return 0;
                    });
                    return 0;
                });
                return 0;
            });



        });

        Weather weather = this.location.getWeather();

        TextView descriptionTv, sunriseTv, sunsetTv, tempTv;
        descriptionTv = view.findViewById(R.id.descriptionTv);
        sunriseTv = view.findViewById(R.id.sunriseTv);
        sunsetTv = view.findViewById(R.id.sunsetTv);
        tempTv = view.findViewById(R.id.tempTv);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;

        descriptionTv.setText(weather.getWeather().getDescription());
        sunsetTv.setText(DateTimeFormatter.ISO_LOCAL_TIME.format(weather.getSunset()));
        sunriseTv.setText(DateTimeFormatter.ISO_LOCAL_TIME.format(weather.getSunrise()));
        tempTv.setText("" + weather.getWeather().getTemp() + " °C");

        WeatherIconView descriptionIcon, sunriseIcon, sunsetIcon, tempIcon;
        descriptionIcon = view.findViewById(R.id.description);
        sunriseIcon = view.findViewById(R.id.sunrisetIcon);
        sunsetIcon = view.findViewById(R.id.sunsetIcon);
        tempIcon = view.findViewById(R.id.tempIcon);

        descriptionIcon.setIconSize(70);
        descriptionIcon.setIconColor(Color.WHITE);

        LocalTime timeNow = LocalTime.now();
        if (timeNow.isBefore(weather.getSunset()) && timeNow.isAfter(weather.getSunrise())) {
            switch (this.location.getWeather().getWeather().getDescription()) {
                case "clear sky":
                case "sunny":
                    descriptionIcon.setIconResource(getString(R.string.wi_day_sunny));
                    break;
                case "scadered clouds":
                case "overcast clouds":
                case "broken clouds":
                case "few clouds":
                    descriptionIcon.setIconResource(getString(R.string.wi_day_cloudy));
                    break;
                case "clouds":
                    descriptionIcon.setIconResource(getString(R.string.wi_cloud));
                    break;
                case "light rain":
                    descriptionIcon.setIconResource(getString(R.string.wi_raindrops));
                case "rain":
                    descriptionIcon.setIconResource(getString(R.string.wi_day_rain));
                    break;
                case "fog":
                    descriptionIcon.setIconResource(getString(R.string.wi_day_fog));
                    break;
                case "light snow":
                    descriptionIcon.setIconResource(getString(R.string.wi_day_snow));
                default:
                    descriptionIcon.setIconResource(getString(R.string.wi_alien));
            }
        } else {
            switch (this.location.getWeather().getWeather().getDescription()) {
                case "clear sky":
                    descriptionIcon.setIconResource(getString(R.string.wi_night_clear));
                case "scattered clouds":
                case "overcast clouds":
                case "broken clouds":
                case "few clouds":
                    descriptionIcon.setIconResource(getString(R.string.wi_night_alt_cloudy));
                    break;
                case "clouds":
                    descriptionIcon.setIconResource(getString(R.string.wi_cloud));
                    break;
                case "light rain":
                    descriptionIcon.setIconResource(getString(R.string.wi_raindrops));
                case "rain":
                    descriptionIcon.setIconResource(getString(R.string.wi_night_alt_rain));
                    break;
                case "fog":
                    descriptionIcon.setIconResource(getString(R.string.wi_night_fog));
                    break;
                case "light snow":
                    descriptionIcon.setIconResource(getString(R.string.wi_night_snow));
                default:
                    descriptionIcon.setIconResource(getString(R.string.wi_alien));
            }
        }
        sunriseIcon.setIconSize(25);
        sunriseIcon.setIconResource(getString(R.string.wi_sunrise));
        sunriseIcon.setIconColor(Color.GRAY);

        sunsetIcon.setIconSize(25);
        sunsetIcon.setIconResource(getString(R.string.wi_sunset));
        sunsetIcon.setIconColor(Color.GRAY);

        tempIcon.setIconSize(25);
        tempIcon.setIconResource(getString(R.string.wi_thermometer));
        tempIcon.setIconColor(Color.GRAY);


        final TextView locationName = view.findViewById(R.id.houseTv);
        TextView devices = view.findViewById(R.id.devicesTv);
        TextView producers = view.findViewById(R.id.producerTv);
        final ConstraintLayout cl = view.findViewById(R.id.locationFragmentCL);

        locationName.setText(this.location.getName());
        if (this.location.getRunningNum() == 1) {
            devices.setText(this.location.getRunningNum() + " Gerät läuft");
        } else {
            devices.setText(this.location.getRunningNum() + " Geräte laufen");
        }

        producers.setText("Momentan erzeugte Watt:  " + this.location.getCurrentEnergy() + " W");
        cl.setOnLongClickListener(view12 -> {

            PopupMenu popupMenu = new PopupMenu(view.getContext(), cl);
            popupMenu.inflate(R.menu.producer_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.edit:
                        editLocation();
                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(getContext())
                                .setTitle(getResources().getString(R.string.remove_location))
                                .setMessage(getResources().getString(R.string.really_delete_location))
                                .setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                                    Map<String, String> data = new HashMap<>();
                                    data.put("locationID", this.locationID);
                                    data.put("email", this.user.getFirebaseUser().getEmail());
                                    this.mFunctions
                                            .getHttpsCallable("deleteLocation")
                                            .call(data)
                                            .addOnSuccessListener(result -> {
                                                this.user.getLocations().remove(location);
                                            });
                                })
                                .setNegativeButton(getResources().getString(R.string.no), null)
                                .show();
                        break;
                    default:
                        return false;
                }
                return true;
            });
            popupMenu.show();
            return true;
        });
        return view;
    }

    private void homeFragmentLocation() {
        HomeFragment homeFragment = ((HomeFragment) LocationFragment.this.getParentFragment());
        homeFragment.locations();
    }


    public void editLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View locationPopupView = getLayoutInflater().inflate(R.layout.popup_location_edit, null);
        ProgressBar progressBar = locationPopupView.findViewById(R.id.pB);
        EditText name = locationPopupView.findViewById(R.id.name);
        Button btnSave = locationPopupView.findViewById(R.id.saveBtn);
        Button btnCancel = locationPopupView.findViewById(R.id.backBtn);
        name.setText(this.location.getName());

        builder.setView(locationPopupView);
        Dialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            Map<String, String> data = new HashMap<>();
            data.put("email", this.user.getFirebaseUser().getEmail());
            data.put("locationID", this.locationID);
            data.put("city", this.location.getCity());
            data.put("zip", this.location.getZipString());
            data.put("country", this.location.getCountry());
            data.put("name", name.getText().toString());
            btnSave.setClickable(false);
            btnSave.setBackgroundResource(R.drawable.rounded_btn_disabled);

            mFunctions
                    .getHttpsCallable("updateLocation")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Standort geändert", Toast.LENGTH_LONG).show();
                        this.location.setName(name.getText().toString());
                        dialog.dismiss();
                    });
        });
        btnCancel.setOnClickListener(view -> dialog.dismiss());
    }
}