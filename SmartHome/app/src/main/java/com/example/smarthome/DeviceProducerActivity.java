package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Adding.AddingDeviceActivity;
import com.example.smarthome.Adding.AddingProducerActivity;
import com.example.smarthome.Menu.DevicesRecycleViewAdapter;
import com.example.smarthome.Menu.ProducerRecycleViewAdapter;
import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.User;

public class DeviceProducerActivity extends AppCompatActivity {
    private Location location;
    private User user;
    private RecyclerView recyclerViewDevices;
    private RecyclerView recyclerViewProducer;


    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.location = this.user.getLocations().get(bundle.getInt("locationPos"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_producer);
        user = User.getInstance();
        readBundle(getIntent().getExtras());

        Button backBtn = findViewById(R.id.backBtn);
        Button addDevice = findViewById(R.id.addDevice);
        Button addProducer = findViewById(R.id.addProducer);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceProducerActivity.this, AddingDeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("locationPos", getIntent().getExtras().getInt("locationPos"));
                intent.putExtras(bundle);
                startActivity(intent);
                texts();
            }
        });
        addProducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceProducerActivity.this, AddingProducerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("locationPos", getIntent().getExtras().getInt("locationPos"));
                intent.putExtras(bundle);
                startActivity(intent);
                texts();
            }
        });

        recyclerViewDevices = (RecyclerView) findViewById(R.id.deviceRV);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDevices.setHasFixedSize(true);
        recyclerViewDevices.setAdapter(new DevicesRecycleViewAdapter(location.getDevices()));

        recyclerViewProducer = (RecyclerView) findViewById(R.id.producerRV);
        recyclerViewProducer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducer.setHasFixedSize(true);
        recyclerViewProducer.setAdapter(new ProducerRecycleViewAdapter(location.getProducers()));


        this.texts();
    }

    private void btnDisable(Button button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            button.setBackground(getResources().getDrawable(R.drawable.rounded_btn_disabled, getTheme()));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.rounded_btn_disabled));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DeviceProducerActivity.this, getResources().getString(R.string.no_produer_no_devices), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void btnEnable(Button button) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            button.setBackground(getResources().getDrawable(R.drawable.rounded_log_sign, getTheme()));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.rounded_log_sign));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Intent location fix hinzufügen
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(recyclerViewProducer.getChildCount()==0&&recyclerViewDevices.getChildCount()==0){
            this.user.getLocations().remove(user.getLocations().size()-1);
        }
    }


    private void texts() {
        TextView producerTv = findViewById(R.id.noProducerTv);
        TextView devicesTv = findViewById(R.id.noDevicesTv);
        Button continueBan = findViewById(R.id.continueBtn);

        if (recyclerViewProducer.getChildCount() == 0) {
            producerTv.setVisibility(View.VISIBLE);
            this.btnDisable(continueBan);
            if (recyclerViewDevices.getChildCount() == 0) {
                devicesTv.setVisibility(View.VISIBLE);
            } else {
                devicesTv.setVisibility(View.GONE);
            }
        } else {
            producerTv.setVisibility(View.GONE);
            if (recyclerViewDevices.getChildCount() == 0) {
                devicesTv.setVisibility(View.VISIBLE);
                this.btnDisable(continueBan);
            } else {
                devicesTv.setVisibility(View.GONE);
                this.btnEnable(continueBan);
            }
        }
    }
}
