package com.example.smarthome.adding;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarthome.model.Device;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.User;
import com.example.smarthome.R;

public class AddingDeviceActivity extends AppCompatActivity {
    private User user;
    private Location location;
    private int devicePos;
    private Device device;

    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.location = this.user.getLocations().get(bundle.getInt("locationPos"));
            this.devicePos = bundle.getInt("devicePos");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_device);
        user = User.getInstance();
        readBundle(getIntent().getExtras());

        Button continueBtn = findViewById(R.id.continueBtn);
        final EditText name = findViewById(R.id.nameEt);
        final EditText type = findViewById(R.id.typeEt);
        final EditText manufacturer = findViewById(R.id.manufacturerEt);

        if (devicePos >= 0) {
            device = this.location.getDevices().get(devicePos);
            name.setText(device.getName());
//            type.setText(device.getType());
  //          manufacturer.setText(device.getManufacturer());
            continueBtn.setText(getResources().getString(R.string.save));
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(type.getText()) || TextUtils.isEmpty(manufacturer.getText())) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
                } else {
                    if (device == null) {
     //                   device = new Device(name.getText().toString(), type.getText().toString(), manufacturer.getText().toString());
                        location.addDevice(device);
                    } else {
                        device.setName(name.getText().toString());
       //                 device.setType(type.getText().toString());
         //               device.setManufacturer(manufacturer.getText().toString());
                    }

                    finish();
                }
            }
        });
    }
}