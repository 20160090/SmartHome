package com.example.smarthome.adding;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.DeviceProducerActivity;
import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.User;
import com.example.smarthome.R;

import java.util.*;

public class AddingLocationActivity extends AppCompatActivity {
    private User user;
    private Location location;
    private int locationPos;
    private final int LAUNCH_ADDING_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_location);
        user = User.getInstance();
        readBundle(getIntent().getExtras());

        Button con = findViewById(R.id.continueBtn);
        final TextView name = findViewById(R.id.nameEt);
        final TextView pvId = findViewById(R.id.pvEt);
        TextView heading = findViewById(R.id.headingTv);

        if (locationPos >= 0) {
            location = user.getLocations().get(locationPos);
            name.setText(location.getName());
            con.setText(getResources().getString(R.string.save));
            heading.setText(getResources().getString(R.string.edit_location));
        } else {

            location = new Location();
            user.getLocations().add(location);
        }

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(pvId.getText())) {
                    Toast.makeText(AddingLocationActivity.this, getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
                } else {
                    //TODO: Location direkt hinzufügen!, dann erst weiter mit Geräten & weiteren PV

                    location.setName(name.getText().toString());
                    if (locationPos < 0) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("locationPos", user.getLocations().size() - 1);
                        bundle.putBoolean("adding", true);
                        Intent intent = new Intent(AddingLocationActivity.this, LocationDetailActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, LAUNCH_ADDING_ACTIVITY);
                    } else {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
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
        if (this.locationPos<0) {
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
}