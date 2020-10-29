package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.adding.AddingDeviceActivity;
import com.example.smarthome.menu.DevicesRecyclerViewAdapter;
import com.example.smarthome.menu.ProducerRecyclerViewAdapter;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Optional;

public class DeviceProducerActivity extends AppCompatActivity {
    private Location location;
    private User user;
    private RecyclerView recyclerViewDevices, recyclerViewProducer;
    private boolean isFABOpen;
    private FloatingActionButton addDevice, addProducer, add;
    private String locationID;


    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.locationID = bundle.getString("locationID");
            Optional<Location> optLoc = this.user.getLocations().stream().filter(l -> l.getId().equals(locationID)).findFirst();
            if (optLoc.isPresent()) {
                this.location = optLoc.get();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_producer);
        this.user = User.getInstance();
        readBundle(getIntent().getExtras());

        this.isFABOpen = false;

        this.add = findViewById(R.id.addFAB);
        this.addDevice = findViewById(R.id.addDevice);
        this.addProducer = findViewById(R.id.addProducer);
        //ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);


        final DevicesRecyclerViewAdapter adapterDevices = new DevicesRecyclerViewAdapter(this, location.getDevices(), locationID);
        this.recyclerViewDevices = findViewById(R.id.deviceRV);
        this.recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerViewDevices.setHasFixedSize(true);
        this.recyclerViewDevices.setAdapter(adapterDevices);


        final ProducerRecyclerViewAdapter adapterProducer = new ProducerRecyclerViewAdapter(this, location.getProducers(), locationID);
        this.recyclerViewProducer = findViewById(R.id.producerRV);
        this.recyclerViewProducer.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerViewProducer.setHasFixedSize(true);
        this.recyclerViewProducer.setAdapter(adapterProducer);

        this.addDevice.setOnClickListener(view -> {
            /*LayoutInflater layoutInflater = (LayoutInflater) DeviceProducerActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = layoutInflater.inflate(R.layout.device_popup, null);
            addDevice = popupView.findViewById(R.id.continueBtn);
            addingDevicePopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addingDevicePopup.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

            final EditText name = popupView.findViewById(R.id.nameEt);
            final EditText type = popupView.findViewById(R.id.typeEt);
            final EditText manufacturer = popupView.findViewById(R.id.manufacturerEt);

            addDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(type.getText()) || TextUtils.isEmpty(manufacturer.getText())) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
                    } else {
                        Device device = new Device(name.getText().toString(), type.getText().toString(), manufacturer.getText().toString());
                        location.addDevice(device);
                        addingDevicePopup.dismiss();
                    }
                }
            });*/
            closeFABMenu();
            Intent intent = new Intent(DeviceProducerActivity.this, AddingDeviceActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("locationID", locationID);
            bundle.putInt("devicePos", -1);
            intent.putExtras(bundle);
            startActivity(intent);
            adapterDevices.notifyDataSetChanged();
            texts();
        });
        this.addProducer.setOnClickListener(view -> {
            closeFABMenu();
            /*Intent intent = new Intent(DeviceProducerActivity.this, AddingProducerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("locationPos", locationPos);
            bundle.putInt("producerPos",-1);
            intent.putExtras(bundle);

            startActivity(intent);*/
            adapterProducer.notifyDataSetChanged();
            texts();
        });
        this.add.setOnClickListener(view -> {
            if (!this.isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        this.texts();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void btnDisable(Button button) {
        button.setBackground(getResources().getDrawable(R.drawable.rounded_btn_disabled, getTheme()));

        button.setOnClickListener(view -> Toast.makeText(DeviceProducerActivity.this, getResources().getString(R.string.no_produer_no_devices), Toast.LENGTH_LONG).show());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void btnEnable(Button button) {

        button.setBackground(getResources().getDrawable(R.drawable.rounded_log_sign, getTheme()));
        button.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            closeFABMenu();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (this.isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
            if (this.recyclerViewProducer.getChildCount() == 0 && recyclerViewDevices.getChildCount() == 0) {
                this.user.getLocations().remove(user.getLocations().size() - 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        texts();
    }

    public void showFABMenu() {
        this.isFABOpen = true;
        this.add.animate().rotation(-90);
        this.add.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        this.addDevice.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        this.addProducer.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    public void closeFABMenu() {
        this.isFABOpen = false;
        this.add.animate().rotation(90);
        this.addDevice.animate().translationY(0);
        this.addProducer.animate().translationY(0);
        this.add.setImageResource(android.R.drawable.ic_input_add);
    }

    private void texts() {
        TextView producerTv = findViewById(R.id.noProducerTv);
        TextView devicesTv = findViewById(R.id.noDevicesTv);
        Button continueBan = findViewById(R.id.continueBtn);

        if (this.location.getProducers().isEmpty()) {
            producerTv.setVisibility(View.VISIBLE);
            this.recyclerViewProducer.setVisibility(View.GONE);
            this.btnDisable(continueBan);
            if (this.location.getDevices().isEmpty()) {
                devicesTv.setVisibility(View.VISIBLE);
                this.recyclerViewDevices.setVisibility(View.GONE);
            } else {
                devicesTv.setVisibility(View.GONE);
                this.recyclerViewDevices.setVisibility(View.VISIBLE);
            }
        } else {
            producerTv.setVisibility(View.GONE);
            this.recyclerViewProducer.setVisibility(View.VISIBLE);
            if (this.location.getDevices().isEmpty()) {
                devicesTv.setVisibility(View.VISIBLE);
                this.recyclerViewDevices.setVisibility(View.GONE);
                this.btnDisable(continueBan);
            } else {
                devicesTv.setVisibility(View.GONE);
                this.recyclerViewDevices.setVisibility(View.VISIBLE);
                this.btnEnable(continueBan);
            }
        }
    }


}
