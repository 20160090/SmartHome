package com.example.smarthome.adding;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarthome.model.Location;
import com.example.smarthome.model.Producer;
import com.example.smarthome.model.User;
import com.example.smarthome.R;

public class AddingProducerActivity extends AppCompatActivity {
    private User user;
    private Location location;
    private int producerPos;
    private Producer producer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_producer);

        user = User.getInstance();
        readBundle(getIntent().getExtras());

        Button continueBan = findViewById(R.id.continueBtn);
        final EditText name = findViewById(R.id.nameEt);
        final EditText type = findViewById(R.id.typeEt);
        final EditText manufacturer = findViewById(R.id.manufacturerEt);

        if (producerPos >= 0) {
            producer = this.location.getProducers().get(producerPos);
            name.setText(producer.getName());
            type.setText(producer.getType());
            manufacturer.setText(producer.getManufacturer());
            continueBan.setText(getResources().getString(R.string.save));
        }

        continueBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(type.getText()) || TextUtils.isEmpty(manufacturer.getText())) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_in_all), Toast.LENGTH_LONG).show();
                } else {
                    if (producerPos<0) {
                        producer = new Producer(name.getText().toString(), type.getText().toString(), manufacturer.getText().toString());
                        location.addProducer(producer);
                    } else {
                        producer.setName(name.getText().toString());
                        producer.setType(type.getText().toString());
                        producer.setManufacturer(manufacturer.getText().toString());

                    }
                    finish();
                }
            }
        });
    }

    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.location = this.user.getLocations().get(bundle.getInt("locationPos"));
            this.producerPos = bundle.getInt("producerPos");
        }
    }
}