package com.example.smarthome.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.adding.AddingLocationActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.User;
import com.example.smarthome.R;
import com.example.smarthome.model.Weather;
import com.github.pwittchen.weathericonview.WeatherIconView;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class LocationFragment extends Fragment {

    private Location location;
    private User user;
    private String locationID;
    private int DELETED = 1;

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
        readBundle(getArguments());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        view.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), LocationDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("locationID", this.locationID);
            intent.putExtras(bundle);
            startActivityForResult(intent, this.DELETED);
            homeFragmentLocation();
        });

        Weather weather = this.location.getWeather();

        TextView descriptionTv, sunriseTv, sunsetTv, tempTv;
        descriptionTv = view.findViewById(R.id.descriptionTv);
        sunriseTv = view.findViewById(R.id.sunriseTv);
        sunsetTv = view.findViewById(R.id.sunsetTv);
        tempTv = view.findViewById(R.id.tempTv);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;

        descriptionTv.setText(weather.getDescription());
        sunsetTv.setText(weather.getSunset().truncatedTo(ChronoUnit.SECONDS).format(formatter));
        sunriseTv.setText(weather.getSunrise().truncatedTo(ChronoUnit.SECONDS).format(formatter));
        tempTv.setText("" + weather.getTemp() + " °C");

        WeatherIconView descriptionIcon, sunriseIcon, sunsetIcon, tempIcon;
        descriptionIcon = view.findViewById(R.id.description);
        sunriseIcon = view.findViewById(R.id.sunrisetIcon);
        sunsetIcon = view.findViewById(R.id.sunsetIcon);
        tempIcon = view.findViewById(R.id.tempIcon);

        descriptionIcon.setIconSize(70);
        descriptionIcon.setIconColor(Color.WHITE);

        LocalTime timeNow = LocalTime.now();
        if (timeNow.isBefore(weather.getSunset()) && timeNow.isAfter(weather.getSunrise())) {
            switch (this.location.getWeather().getDescription()) {
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
                default:
                    descriptionIcon.setIconResource(getString(R.string.wi_alien));
            }
        } else {
            switch (this.location.getWeather().getDescription()) {
                case "clear sky":
                    descriptionIcon.setIconResource(getString(R.string.wi_night_clear));
                case "scadered clouds":
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
        if(this.location.getRunningNum()==1){
            devices.setText(this.location.getRunningNum()+" Gerät läuft");
        }
        else{
            devices.setText(this.location.getRunningNum() + " Geräte laufen");
        }

        producers.setText("Momentan erzeugte Watt:  " + this.location.getCurrentEnergy() + " W");
        cl.setOnLongClickListener(view12 -> {
            PopupMenu popupMenu = new PopupMenu(view12.getContext(), locationName);
            popupMenu.inflate(R.menu.producer_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.edit:
                        Intent intent = new Intent(getContext(), AddingLocationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("locationID", this.locationID);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(getContext())
                                .setTitle(getResources().getString(R.string.remove_location))
                                .setMessage(getResources().getString(R.string.really_delete_location))
                                .setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                                    this.user.getLocations().remove(this.user.getLocations().stream().filter(l -> l.getId().equals(this.locationID)).findFirst().get());
                                    homeFragmentLocation();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELETED) {
            if (resultCode == Activity.RESULT_OK) {
                homeFragmentLocation();
            }
        }
    }
}