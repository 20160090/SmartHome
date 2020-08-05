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


import com.example.smarthome.DeviceProducerActivity;
import com.example.smarthome.adding.AddingDeviceActivity;
import com.example.smarthome.model.Device;
import com.example.smarthome.R;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.smarthome.model.Device.NOT_RUNNING;
import static com.example.smarthome.model.Device.RUNNING;
import static com.example.smarthome.model.Device.SHOULD_BE_RUNNING;
import static com.example.smarthome.model.Device.SHOULD_NOT_BE_RUNNING;

public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder> {

    private final List<Device> mValues;
    private final int locationPos;
    private final Context context;

    public DevicesRecyclerViewAdapter(Context context, List<Device> items, int locationPos) {
        this.context = context;
        this.mValues = items;
        this.locationPos = locationPos;
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
        holder.mInfo.setText(mValues.get(position).getName() + "\n" + mValues.get(position).getType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.mInfo);
                popupMenu.inflate(R.menu.device_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Device device = new Device(mValues.get(position));
                        switch (menuItem.getItemId()) {
                            case R.id.run:
                                switch (device.getState()) {
                                    case NOT_RUNNING:
                                        device.setState(SHOULD_NOT_BE_RUNNING);
                                        break;
                                    case SHOULD_BE_RUNNING:
                                        device.setState(RUNNING);
                                        break;
                                }
                                mValues.set(position, device);
                                notifyItemChanged(position);
                                break;
                            case R.id.stop:
                                switch (device.getState()) {
                                    case RUNNING:
                                        device.setState(SHOULD_BE_RUNNING);
                                        break;
                                    case SHOULD_NOT_BE_RUNNING:
                                        device.setState(NOT_RUNNING);
                                        break;
                                }
                                mValues.set(position, device);
                                notifyItemChanged(position);
                                break;
                            case R.id.delete:
                                mValues.remove(position);
                                notifyItemRemoved(position);
                                break;
                            case R.id.edit:
                                Intent intent = new Intent(context, AddingDeviceActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("locationPos", locationPos);
                                bundle.putInt("devicePos", position);
                                intent.putExtras(bundle);

                                ((Activity) context).startActivityForResult(intent, 1);

                                mValues.set(position, device);
                                notifyItemChanged(position);
                                break;
                            default:
                                return false;
                        }
                        notifyDataSetChanged();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        switch (mValues.get(position).getState()) {
            case Device.RUNNING:
                holder.mImg.setBackgroundResource(R.mipmap.pfeilgruen);
                break;
            case Device.NOT_RUNNING:
                holder.mImg.setBackgroundResource(R.mipmap.x);
                break;
            case Device.SHOULD_BE_RUNNING:
                holder.mImg.setBackgroundResource(R.mipmap.hacken);
                break;
            case Device.SHOULD_NOT_BE_RUNNING:
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