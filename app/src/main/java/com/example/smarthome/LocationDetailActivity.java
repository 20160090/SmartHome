package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.smarthome.menu.DevicesRecyclerViewAdapter;
import com.example.smarthome.menu.ProducerRecyclerViewAdapter;
import com.example.smarthome.model.Company;
import com.example.smarthome.model.Device;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.PossibleDeviceType;
import com.example.smarthome.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class LocationDetailActivity extends AppCompatActivity {
    private User user;
    private Parser parser;
    private Location location;
    private FirebaseFunctions mFunctions;

    private FloatingActionButton add, addDevice, addProducer;
    private boolean isFABOpen;
    private String locationID, companyName;
    private SwipeRefreshLayout refreshLayout;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText pvId, deviceName, deviceId;
    private Button btnCancel, btnAdd;

    private DevicesRecyclerViewAdapter adapterDevices;
    private ProducerRecyclerViewAdapter adapterProducer;

    private PossibleDeviceType selectedType;
    private ArrayList<String> typeNames = new ArrayList<>();
    private ArrayList<PossibleDeviceType> types = new ArrayList<>();
    private ArrayList<Company> companies = new ArrayList<>();



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        this.mFunctions = FirebaseFunctions.getInstance();
        this.parser = Parser.getInstance();
        this.user = User.getInstance();
        readBundle(getIntent().getExtras());

        this.companies = this.user.getCompanies();
        this.types = this.companies.get(0).getDevices();
        this.setTextFields();

        this.isFABOpen = false;
        this.add = findViewById(R.id.addFAB);
        this.addDevice = findViewById(R.id.addDevice);
        this.addProducer = findViewById(R.id.addProducer);
        this.refreshLayout = findViewById(R.id.swipeContainer);

        this.adapterDevices = new DevicesRecyclerViewAdapter(this, this.location.getDevices(), this.locationID);
        RecyclerView recyclerViewDevices = findViewById(R.id.deviceRV);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDevices.setHasFixedSize(true);
        recyclerViewDevices.setNestedScrollingEnabled(false);
        recyclerViewDevices.setAdapter(this.adapterDevices);

        this.adapterProducer = new ProducerRecyclerViewAdapter(this, this.location.getProducers(), this.locationID);
        RecyclerView recyclerViewProducer = findViewById(R.id.producerRV);
        recyclerViewProducer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducer.setHasFixedSize(true);
        recyclerViewProducer.setNestedScrollingEnabled(false);
        recyclerViewProducer.setAdapter(this.adapterProducer);

        this.addDevice.setOnClickListener(view -> {
            closeFABMenu();
            addDevice();
            this.adapterDevices.notifyDataSetChanged();
            texts();
        });
        this.addProducer.setOnClickListener(view -> {
            closeFABMenu();
            addPv();
            adapterProducer.notifyDataSetChanged();
            texts();
        });
        this.add.setOnClickListener(view -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });
        this.refreshLayout.setOnRefreshListener(() -> parser.callGetGeneratorCallback(location,null, t1 -> {
            parser.callGetDevicesCallback(location, t2 -> {
                parser.callGetWeatherCallback(location, t3 -> {
                    parser.callGetForecastCallback(location, t4 -> {
                        adapterDevices.notifyDataSetChanged();
                        adapterProducer.notifyDataSetChanged();
                        this.refreshLayout.setRefreshing(false);
                        return 0;
                    });
                    return 0;
                });
                return 0;
            });
            return 0;
        }));

        this.texts();
    }


    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.locationID = bundle.getString("locationID");
            ArrayList<Location> locations = user.getLocations();
            Optional<Location> optLoc = locations.stream().filter(l -> l.getId().equals(locationID)).findFirst();
            optLoc.ifPresent(value -> {
                this.location = value;
                parser.callGetGeneratorCallback(this.location,null, null);
            });
        }
    }

    private void setTextFields() {
        TextView name = findViewById(R.id.houseTv);
        TextView zip = findViewById(R.id.zipTv);
        TextView city = findViewById(R.id.cityTv);
        TextView country = findViewById(R.id.countryTv);
        name.setText(this.location.getName());
        zip.setText(this.location.getZipString());
        city.setText(this.location.getCity());
        country.setText(this.location.getCountry());
    }

    private void showFABMenu() {
        this.isFABOpen = true;
        this.add.animate().rotation(-90);
        this.add.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        this.addDevice.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        this.addProducer.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu() {
        this.isFABOpen = false;
        this.add.animate().rotation(90);
        this.add.setImageResource(android.R.drawable.ic_input_add);
        this.addDevice.animate().translationY(0);
        this.addProducer.animate().translationY(0);
    }

    public void texts() {
        TextView producerTv = findViewById(R.id.noProducerTv);
        TextView devicesTv = findViewById(R.id.noDevicesTv);

        if (this.location.getProducers().isEmpty()) {
            producerTv.setVisibility(View.VISIBLE);
        } else {
            producerTv.setVisibility(View.GONE);
        }
        if (this.location.getDevices().isEmpty()) {
            devicesTv.setVisibility(View.VISIBLE);
        } else {
            devicesTv.setVisibility(View.GONE);
        }
    }

    public void addPv() {
        this.builder = new AlertDialog.Builder(this);
        final View pvPopupView = getLayoutInflater().inflate(R.layout.popup_pv, null);

        this.pvId = pvPopupView.findViewById(R.id.pvId);
        this.btnAdd = pvPopupView.findViewById(R.id.continueBtn);
        this.btnCancel = pvPopupView.findViewById(R.id.backBtn);

        this.builder.setView(pvPopupView);
        this.dialog = this.builder.create();
        this.dialog.show();

        this.btnAdd.setOnClickListener(view -> {
            Map<String, Object> data = new HashMap<>();
            data.put("pvID", "6dd05177-193f-4580-97bd-3331e3abe530");
            data.put("email", User.getInstance().getFirebaseUser().getEmail());

            this.mFunctions
                    .getHttpsCallable("addPV")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        System.out.println(result.getData().toString());
                        //Producer pv = new Producer(data.get(pvId), task.getResult().getData().toString());
                    });
        });
        this.btnCancel.setOnClickListener(view -> dialog.dismiss());
    }

    public void addDevice() {
        this.builder = new AlertDialog.Builder(this);
        final View devicePopupView = getLayoutInflater().inflate(R.layout.popup_device, null);
        this.deviceName = devicePopupView.findViewById(R.id.deviceName);
        this.deviceId = devicePopupView.findViewById(R.id.deviceId);

        ArrayList<String> names = (ArrayList<String>) this.companies.stream().map(Company::getName).collect(toList());
        this.typeNames = (ArrayList<String>) this.types.stream().map(PossibleDeviceType::getType).collect(toList());

        Spinner deviceSpinner = devicePopupView.findViewById(R.id.deviceSpinner);
        ArrayAdapter<String> adapterD = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typeNames);
        adapterD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceSpinner.setAdapter(adapterD);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = types.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner companySpinner = devicePopupView.findViewById(R.id.companySpinner);
        ArrayAdapter<String> adapterC = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names);
        adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(adapterC);
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                types = companies.get(i).getDevices();
                companyName = companies.get(i).getName();
                selectedType = types.get(0);
                typeNames.clear();
                typeNames.addAll(types.stream().map(PossibleDeviceType::getType).collect(toList()));
                adapterD.notifyDataSetChanged();
                deviceSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        this.btnAdd = devicePopupView.findViewById(R.id.continueBtn);
        this.btnCancel = devicePopupView.findViewById(R.id.backBtn);
        this.builder.setView(devicePopupView);
        this.dialog = this.builder.create();
        this.dialog.show();

        this.btnAdd.setOnClickListener(view -> {
            Map<String, String> data = new HashMap<>();
            data.put("locationID", this.locationID);
            data.put("consumerType", this.selectedType.getType());
            data.put("averageConsumption", "" + this.selectedType.getAverageConsumption() + "");
            data.put("companyName", this.companyName);
            data.put("consumerName", this.deviceName.getText().toString());
            data.put("consumerSerial", this.deviceId.getText().toString());
            data.put("email", User.getInstance().getFirebaseUser().getEmail());

            this.mFunctions
                    .getHttpsCallable("addConsumer")
                    .call(data)
                    .addOnSuccessListener(task -> {
                        try {
                            JSONObject object = new JSONObject(task.getData().toString());
                            Device consumer = new Device(object.getString("consumerID"), data.get("consumerName"), this.selectedType.getType(), object.getString("state"), data.get("consumerSerial"), data.get("companyName"), this.selectedType.getAverageConsumption());
                            this.location.addDevice(consumer);
                            Map<String, String> consumptionData= new HashMap<>();
                            consumptionData.put("consumerType",consumer.getPossibleDeviceType());
                            parser.callConsumerData(consumer.getPossibleDeviceType(), consumer);
                            this.adapterDevices.notifyDataSetChanged();
                            texts();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
            this.dialog.dismiss();
        });
        this.btnCancel.setOnClickListener(view -> this.dialog.dismiss());
    }



}