package com.example.smarthome.Adding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.DeviceProducerActivity;
import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;

public class AddingLocationActivity extends AppCompatActivity {
    private User user;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_location);

        user = User.getInstance();
        location = new Location();
        user.getLocations().add(location);

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Button con = findViewById(R.id.continueBtn);
        final TextView name = findViewById(R.id.nameEt);
        final TextView country = findViewById(R.id.countryEt);
        final TextView zip = findViewById(R.id.zipEt);
        final TextView city = findViewById(R.id.cityEt);
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(country.getText()) || TextUtils.isEmpty(zip.getText()) || TextUtils.isEmpty(city.getText())) {
                    Toast.makeText(AddingLocationActivity.this, getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
                } else {
                    location.setName(name.getText().toString());
                    location.setCountry(country.getText().toString());
                    location.setZip(Integer.parseInt(zip.getText().toString()));
                    location.setCity(city.getText().toString());

                    Bundle bundle = new Bundle();
                    bundle.putInt("locationPos", user.getLocations().size()-1);

                    Intent intent = new Intent(AddingLocationActivity.this, DeviceProducerActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.user.getLocations().remove(user.getLocations().size()-1);
    }
}