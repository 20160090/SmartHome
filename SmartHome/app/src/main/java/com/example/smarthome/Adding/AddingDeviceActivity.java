package com.example.smarthome.Adding;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarthome.Model.Device;
import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;

public class AddingDeviceActivity extends AppCompatActivity {
    User user;
    Location location;

    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.location = this.user.getLocations().get(bundle.getInt("locationPos"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_device);
        user = User.getInstance();
        readBundle(getIntent().getExtras());

        Button continueBan = findViewById(R.id.continueBtn);
        Button backBtn = findViewById(R.id.backBtn);
        final EditText name = findViewById(R.id.nameEt);
        final EditText type = findViewById(R.id.typeEt);
        final EditText manufacturer = findViewById(R.id.manufacturerEt);

        continueBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(type.getText()) || TextUtils.isEmpty(manufacturer.getText())) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
                } else {
                    Device device = new Device(name.getText().toString(), type.getText().toString(), manufacturer.getText().toString());
                    location.addDevice(device);
                    finish();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}