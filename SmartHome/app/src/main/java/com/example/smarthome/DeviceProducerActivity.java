package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.smarthome.Menu.DevicesRecycleViewAdapter;
import com.example.smarthome.Menu.ProducerRecycleViewAdapter;
import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.User;

public class DeviceProducerActivity extends AppCompatActivity {
    Location location;
    User user;

    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            System.out.println("asdfasdfasdfasdfasdfasdfasdfasdASDFAAAAAAA\t"+bundle.getInt("locationPos"));
            this.location = this.user.getLocations().get(bundle.getInt("locationPos"));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_producer);

        user = User.getInstance();
        readBundle(getIntent().getExtras());



        RecyclerView recyclerViewDevices = (RecyclerView) findViewById(R.id.deviceRV);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDevices.setHasFixedSize(true);
        recyclerViewDevices.setAdapter(new DevicesRecycleViewAdapter(location.getDevices()));

        RecyclerView recyclerViewProducer = (RecyclerView) findViewById(R.id.producerRV);
        recyclerViewProducer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducer.setHasFixedSize(true);
        recyclerViewProducer.setAdapter(new ProducerRecycleViewAdapter(location.getProducers()));


    }

}
