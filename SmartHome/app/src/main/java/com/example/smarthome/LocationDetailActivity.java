package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.adding.AddingLocationActivity;
import com.example.smarthome.menu.DevicesRecyclerViewAdapter;
import com.example.smarthome.menu.ProducerRecyclerViewAdapter;
import com.example.smarthome.model.Company;
import com.example.smarthome.model.Device;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.PossibleDeviceType;
import com.example.smarthome.model.Producer;
import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static android.os.Build.VERSION_CODES.M;
import static java.util.stream.Collectors.toList;

public class LocationDetailActivity extends AppCompatActivity {
    private User user;
    private Parser parser;
    private Location location;
    private FirebaseFunctions mFunctions;

    private FloatingActionButton add, addDevice, addProducer;
    private int CHANGED = 1;
    private boolean isFABOpen;
    private String locationID, companyName;

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
        this.parser = new Parser();

        this.user = User.getInstance();
        readBundle(getIntent().getExtras());


        this.companies = this.user.getCompanies();
        this.types = this.companies.get(0).getDevices();

        this.setTextFields();


        //TODO: überarbeiten... vlt auch PopUps???
        final LinearLayout linearLayout = findViewById(R.id.linearLayout2);
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), linearLayout);
                popupMenu.inflate(R.menu.producer_menu);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.edit:
                           /* Intent intent = new Intent(LocationDetailActivity.this, AddingLocationActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("locationID", locationID);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, CHANGED);*/
                            editLocation();
                            break;
                        case R.id.delete:
                            new AlertDialog.Builder(LocationDetailActivity.this)
                                    .setTitle(getResources().getString(R.string.remove_location))
                                    .setMessage(getResources().getString(R.string.really_delete_location))
                                    .setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, i) -> {
                                        Map<String, String> data = new HashMap<>();
                                        data.put("locationID",locationID);
                                        data.put("email",user.getFirebaseUser().getEmail());
                                        mFunctions
                                                .getHttpsCallable("deleteLocation")
                                                .call(data)
                                                .addOnSuccessListener(result -> {
                                                    user.getLocations().remove(location);
                                                    Intent returnIntent = new Intent();
                                                    setResult(Activity.RESULT_OK, returnIntent);
                                                    finish();
                                                });
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
            }
        });

        this.isFABOpen = false;
        this.add = findViewById(R.id.addFAB);
        this.addDevice = findViewById(R.id.addDevice);
        this.addProducer = findViewById(R.id.addProducer);


        adapterDevices = new DevicesRecyclerViewAdapter(this, location.getDevices(), locationID);
        RecyclerView recyclerViewDevices = findViewById(R.id.deviceRV);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDevices.setHasFixedSize(true);
        recyclerViewDevices.setAdapter(adapterDevices);


        adapterProducer = new ProducerRecyclerViewAdapter(this, location.getProducers(), locationID);
        RecyclerView recyclerViewProducer = findViewById(R.id.producerRV);
        recyclerViewProducer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducer.setHasFixedSize(true);
        recyclerViewProducer.setAdapter(adapterProducer);


        this.addDevice.setOnClickListener(view -> {
            closeFABMenu();
            addDevice();
            adapterDevices.notifyDataSetChanged();
            texts();
        });
        this.addProducer.setOnClickListener(view -> {
            closeFABMenu();
            /*Intent intent = new Intent(LocationDetailActivity.this, AddingProducerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("locationPos", locationPos);
            bundle.putInt("producerPos", -1);
            intent.putExtras(bundle);
            startActivity(intent);*/
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

        this.texts();
    }


    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.locationID = bundle.getString("locationID");
            ArrayList<Location> locations = user.getLocations();
            Optional<Location> optLoc = locations.stream().filter(l -> l.getId().equals(locationID)).findFirst();
            if (optLoc.isPresent()) {
                this.location = optLoc.get();
            }

        }
    }

    private void setTextFields(){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGED) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
                startActivity(getIntent());
            }
        }
    }

    public void editLocation() {
        this.builder = new AlertDialog.Builder(this);
        final View locationPopupView = getLayoutInflater().inflate(R.layout.popup_location_edit, null);
        ProgressBar progressBar = locationPopupView.findViewById(R.id.pB);

        EditText name = locationPopupView.findViewById(R.id.name);
        name.setText(this.location.getName());
        Button btnSave = locationPopupView.findViewById(R.id.saveBtn);
        Button btnCancel = locationPopupView.findViewById(R.id.backBtn);

        this.builder.setView(locationPopupView);
        this.dialog = this.builder.create();
        this.dialog.show();

        btnSave.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            Map<String, String> data = new HashMap<>();
            data.put("email",this.user.getFirebaseUser().getEmail());
            data.put("locationID", this.locationID);
            data.put("city",this.location.getCity());
            data.put("zip", this.location.getZipString());
            data.put("country", this.location.getCountry());
            data.put("name", name.getText().toString());
            btnSave.setClickable(false);
            btnSave.setBackgroundResource(R.drawable.rounded_btn_disabled);

            this.mFunctions
                    .getHttpsCallable("updateLocation")
                    .call(data)
                    .addOnSuccessListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LocationDetailActivity.this, "Standort geändert", Toast.LENGTH_LONG).show();
                        this.location.setName(name.getText().toString());
                        this.setTextFields();
                        this.dialog.dismiss();
                    })
                    .addOnFailureListener(task -> {
                        task.printStackTrace();
                    });
        });
        btnCancel.setOnClickListener(view -> this.dialog.dismiss());
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
            //TODO: Server PV hinzufügen true/false? false meldung: entweder PV nicht vorhanden
            // oder PV befindet sich an anderem Standort --> vlt neuen Standort anlegen

            Map<String, Object> data = new HashMap<>();
            data.put("pvID", "6dd05177-193f-4580-97bd-3331e3abe530");
            data.put("email", User.getInstance().getFirebaseUser().getEmail());

            this.mFunctions
                    .getHttpsCallable("addPV")
                    .call(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println(task.getResult().getData().toString());
                            //Producer pv = new Producer(data.get(pvId), task.getResult().getData().toString());
                        }
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
        typeNames = (ArrayList<String>) this.types.stream().map(PossibleDeviceType::getType).collect(toList());

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
            public void onNothingSelected(AdapterView<?> adapterView) {}
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        this.btnAdd = devicePopupView.findViewById(R.id.continueBtn);
        this.btnCancel = devicePopupView.findViewById(R.id.backBtn);
        this.builder.setView(devicePopupView);
        this.dialog = this.builder.create();
        this.dialog.show();

        this.btnAdd.setOnClickListener(view -> {
            Map<String, String> data = new HashMap<>();
            data.put("locationID", this.locationID);
            data.put("consumerType", selectedType.getType());
            data.put("averageConsumption", ""+selectedType.getAverageConsumption()+"");
            data.put("companyName", companyName);
            data.put("consumerName", this.deviceName.getText().toString());
            data.put("consumerSerial", this.deviceId.getText().toString());
            data.put("email", User.getInstance().getFirebaseUser().getEmail());

            this.mFunctions
                    .getHttpsCallable("addConsumer")
                    .call(data)
                    .addOnSuccessListener(task -> {
                        try {
                            JSONObject object = new JSONObject(task.getData().toString());
                            Device consumer = new Device(object.getString("consumerID"), data.get("consumerName"),selectedType.getType(), object.getString("state"), data.get("consumerSerial"),data.get("companyName"), selectedType.getAverageConsumption() );
                            this.location.addDevice(consumer);
                            this.adapterDevices.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //TODO: devices parsen
                    });
            this.dialog.dismiss();
        });
        this.btnCancel.setOnClickListener(view -> this.dialog.dismiss());


    }
    public void editConsumer(Device device){
        this.builder = new AlertDialog.Builder(this);
        final View devicePopupView = getLayoutInflater().inflate(R.layout.popup_device, null);
        this.deviceName = devicePopupView.findViewById(R.id.deviceName);
        this.deviceName.setText(device.getName());
        this.deviceId = devicePopupView.findViewById(R.id.deviceId);
        this.deviceId.setText(device.getSerialNumber());

        ArrayList<String> names = (ArrayList<String>) this.companies.stream().map(Company::getName).collect(toList());
        typeNames = (ArrayList<String>) this.types.stream().map(PossibleDeviceType::getType).collect(toList());

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
            public void onNothingSelected(AdapterView<?> adapterView) {}
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
            public void onNothingSelected(AdapterView<?> adapterView) {}
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
            data.put("averageConsumption", ""+this.selectedType.getAverageConsumption()+"");
            data.put("companyName", this.companyName);
            data.put("consumerName", this.deviceName.getText().toString());
            data.put("consumerSerial", this.deviceId.getText().toString());
            data.put("email", User.getInstance().getFirebaseUser().getEmail());

            device.setCompany(data.get("companyName"));
            device.setAverageConsumption(Double.parseDouble(data.get("averageConsumption")));
            device.setName(data.get("consumerName"));
            device.setPossibleDeviceType(data.get("consumerType"));
            device.setSerialNumber(data.get("consumerSerial"));

            this.mFunctions
                    .getHttpsCallable("addConsumer")
                    .call(data)
                    .addOnSuccessListener(task -> {
                        try {
                            JSONObject object = new JSONObject(task.getData().toString());

                            this.adapterDevices.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //TODO: devices parsen
                    });
            this.dialog.dismiss();
        });
        this.btnCancel.setOnClickListener(view -> this.dialog.dismiss());
    }






}