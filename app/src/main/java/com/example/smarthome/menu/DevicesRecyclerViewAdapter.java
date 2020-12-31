package com.example.smarthome.menu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.smarthome.model.Company;
import com.example.smarthome.model.Device;
import com.example.smarthome.R;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.PossibleDeviceType;
import com.example.smarthome.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.smarthome.model.Device.State.NOT_RUNNING;
import static com.example.smarthome.model.Device.State.RUNNING;
import static com.example.smarthome.model.Device.State.SHOULD_BE_RUNNING;
import static com.example.smarthome.model.Device.State.SHOULD_NOT_BE_RUNNING;
import static java.util.stream.Collectors.toList;


public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder> {

    private final User user = User.getInstance();
    private final List<Device> mValues;
    private final String locationID;
    private final Context context;
    private PossibleDeviceType selectedType;
    private final ArrayList<Company> companies = this.user.getCompanies();
    private ArrayList<PossibleDeviceType> types = companies.get(0).getDevices();
    private String companyName;

    public DevicesRecyclerViewAdapter(Context context, List<Device> items, String locationID) {
        this.context = context;
        this.mValues = items;
        this.locationID = locationID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.devices, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = this.mValues.get(position);
        holder.mInfo.setText(this.mValues.get(position).getName() + "\n" + this.mValues.get(position).getAverageConsumption());
        holder.mConsumption.setText("Momentaner Verbrauch: " + "" + this.mValues.get(position).getConsumption() + "");
        holder.itemView.setOnClickListener(view -> {

            PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.mInfo);
            popupMenu.inflate(R.menu.device_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                Device device = new Device(this.mValues.get(position));
                FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
                Map<String, String> data = new HashMap<>();
                data.put("email", this.user.getFirebaseUser().getEmail());
                data.put("locationID", this.locationID);
                data.put("consumerID", device.getId());
                data.put("pvID", this.user.getLocations().stream().filter(l -> l.getId().equals(this.locationID)).findFirst().get().getProducers().get(0).getId());

                switch (menuItem.getItemId()) {
                    case R.id.run:
                        data.put("modus", "start");
                        mFunctions
                                .getHttpsCallable("updateState")
                                .call(data)
                                .addOnSuccessListener(result -> {
                                    try {
                                        JSONObject object = new JSONObject(result.getData().toString());
                                        device.setState(device.switchState(object.getString("consumerState").toUpperCase()));

                                        Parser.getInstance().callConsumerData(device.getPossibleDeviceType(), device);
                                        this.mValues.set(position, device);
                                        notifyItemChanged(position);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                });
                        break;
                    case R.id.stop:
                        data.put("modus", "stop");
                        mFunctions
                                .getHttpsCallable("updateState")
                                .call(data)
                                .addOnSuccessListener(result -> {
                                    try {
                                        JSONObject object = new JSONObject(result.getData().toString());
                                        device.setState(device.switchState(object.getString("consumerState").toUpperCase()));
                                        this.mValues.set(position, device);
                                        notifyItemChanged(position);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                });
                        break;
                    case R.id.delete:

                        new AlertDialog.Builder(context)
                                .setTitle(context.getResources().getString(R.string.remove_location))
                                .setMessage(context.getResources().getString(R.string.really_delete_location))
                                .setPositiveButton(context.getResources().getString(R.string.yes), (dialogInterface, i) -> {
                                    data.clear();
                                    data.put("email", this.user.getFirebaseUser().getEmail());
                                    data.put("locationID", this.locationID);
                                    data.put("consumerID", device.getId());
                                    mFunctions
                                            .getHttpsCallable("deleteConsumer")
                                            .call(data)
                                            .addOnSuccessListener(result -> {
                                                this.mValues.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                            });
                                })
                                .setNegativeButton(context.getResources().getString(R.string.no), null)
                                .show();
                        break;
                    case R.id.edit:
                        editConsumer(device);
                        this.mValues.set(position, device);
                        notifyItemChanged(position);
                        break;
                    default:
                        return false;
                }
                notifyDataSetChanged();
                return true;
            });
            popupMenu.show();
        });

        switch (this.mValues.get(position).getState()) {
            case RUNNING:
                holder.mImg.setBackgroundResource(R.mipmap.pfeilgruen);
                break;
            case NOT_RUNNING:
                holder.mImg.setBackgroundResource(R.mipmap.x);
                break;
            case SHOULD_BE_RUNNING:
                holder.mImg.setBackgroundResource(R.mipmap.hacken);
                break;
            case SHOULD_NOT_BE_RUNNING:
                holder.mImg.setBackgroundResource(R.mipmap.pfeilgelb);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mInfo, mConsumption;
        public Device mItem;
        public final ImageView mImg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mInfo = view.findViewById(R.id.info);
            mConsumption = view.findViewById(R.id.consumption);
            mImg = view.findViewById(R.id.imageView);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mInfo.getText() + "'";
        }
    }


    public void editConsumer(Device device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View devicePopupView = LayoutInflater.from(context).inflate(R.layout.popup_device, null);

        EditText deviceName, deviceId;
        AppCompatButton btnAdd, btnCancel;
        AlertDialog dialog;
        ArrayList<String> names = (ArrayList<String>) companies.stream().map(Company::getName).collect(toList());
        ArrayList<String> typeNames = (ArrayList<String>) types.stream().map(PossibleDeviceType::getType).collect(toList());


        deviceName = devicePopupView.findViewById(R.id.deviceName);
        deviceName.setText(device.getName());

        deviceId = devicePopupView.findViewById(R.id.deviceId);
        deviceId.setText(device.getSerialNumber());


        Spinner deviceSpinner = devicePopupView.findViewById(R.id.deviceSpinner);
        ArrayAdapter<String> adapterD = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, typeNames);
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
        ArrayAdapter<String> adapterC = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, names);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ;
        companySpinner.setSelection(adapterC.getPosition(device.getCompany()));

        int pos = adapterD.getPosition(device.getPossibleDeviceType());
        deviceSpinner.setSelection(pos);
        this.selectedType = types.get(pos);


        btnAdd = devicePopupView.findViewById(R.id.continueBtn);
        btnCancel = devicePopupView.findViewById(R.id.backBtn);
        builder.setView(devicePopupView);
        dialog = builder.create();
        dialog.show();

        btnAdd.setText(R.string.save);
        btnAdd.setOnClickListener(view -> {
            Map<String, String> data = new HashMap<>();
            data.put("locationID", this.locationID);
            data.put("consumerType", this.selectedType.getType());
            data.put("averageConsumption", "" + this.selectedType.getAverageConsumption() + "");
            data.put("companyName", this.companyName);
            data.put("consumerName", deviceName.getText().toString());
            data.put("consumerSerial", deviceId.getText().toString());
            data.put("email", User.getInstance().getFirebaseUser().getEmail());

            device.setCompany(data.get("companyName"));
            device.setAverageConsumption(Double.parseDouble(data.get("averageConsumption")));
            device.setName(data.get("consumerName"));
            device.setPossibleDeviceType(data.get("consumerType"));
            device.setSerialNumber(data.get("consumerSerial"));

            FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
            mFunctions
                    .getHttpsCallable("updateConsumer")
                    .call(data)
                    .addOnSuccessListener(result -> {
                        notifyDataSetChanged();
                    });
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(view -> dialog.dismiss());
    }


}