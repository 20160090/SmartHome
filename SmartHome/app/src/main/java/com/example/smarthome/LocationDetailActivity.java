package com.example.smarthome;

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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.smarthome.adding.AddingDeviceActivity;
import com.example.smarthome.adding.AddingLocationActivity;
import com.example.smarthome.adding.AddingProducerActivity;
import com.example.smarthome.menu.DevicesRecyclerViewAdapter;
import com.example.smarthome.menu.ProducerRecyclerViewAdapter;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LocationDetailActivity extends AppCompatActivity {
    private User user;
    private Location location;
    private FloatingActionButton add, addDevice, addProducer;
    private int CHANGED = 1;
    private boolean isFABOpen;
    private int locationPos;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText pvId;
    private Button pvCancel, pvAdd;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        this.user = User.getInstance();
        readBundle(getIntent().getExtras());


        TextView name = findViewById(R.id.houseTv);
        TextView zip = findViewById(R.id.zipTv);
        TextView city = findViewById(R.id.cityTv);
        TextView country = findViewById(R.id.countryTv);
        name.setText(this.location.getName());
        zip.setText(this.location.getZipString());
        city.setText(this.location.getCity());
        country.setText(this.location.getCountry());


        final LinearLayout linearLayout = findViewById(R.id.linearLayout2);
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), linearLayout);
                popupMenu.inflate(R.menu.producer_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(LocationDetailActivity.this, AddingLocationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("locationPos", locationPos);
                                intent.putExtras(bundle);
                                startActivityForResult(intent, CHANGED);
                                break;
                            case R.id.delete:
                                new AlertDialog.Builder(LocationDetailActivity.this)
                                        .setTitle(getResources().getString(R.string.remove_location))
                                        .setMessage(getResources().getString(R.string.really_delete_location))
                                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                user.getLocations().remove(locationPos);
                                                Intent returnIntent = new Intent();
                                                setResult(Activity.RESULT_OK, returnIntent);
                                                finish();
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), null)
                                        .show();
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });
                popupMenu.show();


                return true;
            }
        });

        this.isFABOpen = false;
        this.add = findViewById(R.id.addFAB);
        this.addDevice = findViewById(R.id.addDevice);
        this.addProducer = findViewById(R.id.addProducer);


        final DevicesRecyclerViewAdapter adapterDevices = new DevicesRecyclerViewAdapter(this, location.getDevices(), locationPos);
        RecyclerView recyclerViewDevices = findViewById(R.id.deviceRV);
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDevices.setHasFixedSize(true);
        recyclerViewDevices.setAdapter(adapterDevices);


        final ProducerRecyclerViewAdapter adapterProducer = new ProducerRecyclerViewAdapter(this, location.getProducers(), locationPos);
        RecyclerView recyclerViewProducer = findViewById(R.id.producerRV);
        recyclerViewProducer.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducer.setHasFixedSize(true);
        recyclerViewProducer.setAdapter(adapterProducer);


        this.addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
                Intent intent = new Intent(LocationDetailActivity.this, AddingDeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("locationPos", locationPos);
                bundle.putInt("devicePos", -1);
                intent.putExtras(bundle);
                startActivity(intent);
                adapterDevices.notifyDataSetChanged();
                texts();
            }
        });
        this.addProducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        this.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        this.texts();
    }


    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.locationPos = bundle.getInt("locationPos");
            this.location = this.user.getLocations().get(this.locationPos);
        }
    }

    private void showFABMenu() {
        this.isFABOpen = true;
        this.add.animate().rotation(-90);
        add.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        this.addDevice.animate().translationY(-getResources().getDimension(R.dimen.standard_55));

        this.addProducer.animate().translationY(-getResources().getDimension(R.dimen.standard_105));

    }

    private void closeFABMenu() {
        this.isFABOpen = false;
        this.add.animate().rotation(90);
        this.addDevice.animate().translationY(0);
        this.addProducer.animate().translationY(0);
        this.add.setImageResource(android.R.drawable.ic_input_add);
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

    public void addPv(){
        builder = new AlertDialog.Builder(this);
        final View pvPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        pvId = (EditText)pvPopupView.findViewById(R.id.pvId);

        pvAdd = (Button)pvPopupView.findViewById(R.id.continueBtn);
        pvCancel = (Button)pvPopupView.findViewById(R.id.backBtn);

        builder.setView(pvPopupView);
        dialog = builder.create();
        dialog.show();

        pvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        pvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

}