package com.example.smarthome.menu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.adding.AddingDeviceActivity;
import com.example.smarthome.model.Device;
import com.example.smarthome.R;
import com.example.smarthome.model.User;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.smarthome.model.Device.State.NOT_RUNNING;
import static com.example.smarthome.model.Device.State.RUNNING;
import static com.example.smarthome.model.Device.State.SHOULD_BE_RUNNING;
import static com.example.smarthome.model.Device.State.SHOULD_NOT_BE_RUNNING;


public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder> {

    private User user = User.getInstance();
    private final List<Device> mValues;
    private final String locationID;
    private final Context context;

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
        holder.mItem = mValues.get(position);
        holder.mInfo.setText(mValues.get(position).getName() + "\n" + mValues.get(position).getAverageConsumption());

        holder.itemView.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.mInfo);
            popupMenu.inflate(R.menu.device_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                Device device = new Device(mValues.get(position));
                FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
                Map<String, String> data = new HashMap<>();
                data.put("email",user.getFirebaseUser().getEmail());
                data.put("locationID",locationID);
                data.put("consumerID", device.getId());

                switch (menuItem.getItemId()) {
                    case R.id.run:
                        switch (device.getState()) {
                            case NOT_RUNNING:
                                device.setState(SHOULD_NOT_BE_RUNNING);
                                data.put("consumerState","SHOULD_NOT_BE_RUNNING");
                                break;
                            case SHOULD_BE_RUNNING:
                                device.setState(RUNNING);
                                data.put("consumerState","RUNNING");
                                break;
                        }
                        mFunctions
                                .getHttpsCallable("updateState")
                                .call(data)
                                .addOnSuccessListener(result -> {
                                    mValues.set(position, device);
                                    notifyItemChanged(position);
                                });
                        break;
                    case R.id.stop:
                        switch (device.getState()) {
                            case RUNNING:
                                device.setState(SHOULD_BE_RUNNING);
                                data.put("consumerState","SHOULD_BE_RUNNING");
                                break;
                            case SHOULD_NOT_BE_RUNNING:
                                device.setState(NOT_RUNNING);
                                data.put("consumerState","NOT_RUNNING");
                                break;
                        }
                        mFunctions
                                .getHttpsCallable("updateState")
                                .call(data)
                                .addOnSuccessListener(result -> {
                                    mValues.set(position, device);
                                    notifyItemChanged(position);
                                });
                        break;
                    case R.id.delete:

                        data.clear();
                        data.put("email",user.getFirebaseUser().getEmail());
                        data.put("locationID",locationID);
                        data.put("consumerID",device.getId());
                        mFunctions
                                .getHttpsCallable("deleteConsumer")
                                .call(data)
                                .addOnSuccessListener(result -> {

                                    mValues.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                });
                        break;
                    case R.id.edit:
                        ((LocationDetailActivity)context).editConsumer(device);
                        mValues.set(position, device);
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

        switch (mValues.get(position).getState()) {
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
        return mValues.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mInfo;
        public Device mItem;
        public final ImageView mImg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mInfo = view.findViewById(R.id.info);
            mImg = view.findViewById(R.id.imageView);
        }


        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mInfo.getText() + "'";
        }
    }


}