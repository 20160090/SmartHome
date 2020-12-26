package com.example.smarthome.menu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.model.Device;
import com.example.smarthome.R;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.smarthome.model.Device.State.NOT_RUNNING;
import static com.example.smarthome.model.Device.State.RUNNING;
import static com.example.smarthome.model.Device.State.SHOULD_BE_RUNNING;
import static com.example.smarthome.model.Device.State.SHOULD_NOT_BE_RUNNING;


public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder> {

    private final User user = User.getInstance();
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
        holder.mItem = this.mValues.get(position);
        holder.mInfo.setText(this.mValues.get(position).getName() + "\n" + this.mValues.get(position).getAverageConsumption());
        holder.mConsumption.setText("Momentaner Verbrauch: "+""+this.mValues.get(position).getConsumption()+"");
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
                data.put("pvID",this.user.getLocations().stream().filter(l -> l.getId().equals(this.locationID)).findFirst().get().getProducers().get(0).getId());

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
                                        Parser parser = new Parser();
                                        parser.callConsumerData(device.getPossibleDeviceType(), device);
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
                        break;
                    case R.id.edit:
                        ((LocationDetailActivity) context).editConsumer(device);
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


}