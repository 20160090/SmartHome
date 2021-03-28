package com.example.smarthome.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.model.Company;
import com.example.smarthome.model.PossibleDeviceType;
import com.example.smarthome.model.Producer;
import com.example.smarthome.R;
import com.example.smarthome.model.User;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class ProducerRecyclerViewAdapter extends RecyclerView.Adapter<ProducerRecyclerViewAdapter.ViewHolder> {
    private final User user = User.getInstance();
    private final List<Producer> mValues;
    private final String locationID;
    private final Context context;

    public ProducerRecyclerViewAdapter(Context context, List<Producer> items, String locationID) {
        this.mValues = items;
        this.locationID = locationID;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.producer, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mInfo.setText(mValues.get(position).getType());
        holder.mWH.setText("" + mValues.get(position).getCurrentlyProduced() + "" + "W");

     /*   holder.itemView.setOnClickListener(view -> {
            final Producer producer = mValues.get(position);
            PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.mInfo);
            popupMenu.inflate(R.menu.producer_menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.edit:
                        this.mValues.set(position, producer);
                        notifyItemChanged(position);
                        break;
                    case R.id.delete:
                       FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
                        Map<String, String> data = new HashMap<>();
                        if (this.user.getLocations().stream().filter(l -> l.getId().equals(this.locationID)).findFirst().get().getProducers().size() > 1) {
                            data.put("email", this.user.getFirebaseUser().getEmail());
                            data.put("locationID", this.locationID);
                            data.put("pvID", producer.getId());
                            mFunctions
                                    .getHttpsCallable("deleteGenerator")
                                    .call(data)
                                    .addOnSuccessListener(result -> {

                                        this.mValues.remove(position);
                                        notifyItemRemoved(position);
                                    });
                        } else {
                            data.put("locationID",this.locationID);
                            data.put("email",this.user.getFirebaseUser().getEmail());
                            mFunctions
                                    .getHttpsCallable("deleteLocation")
                                    .call(data)
                                    .addOnSuccessListener(result -> {
                                        ((LocationDetailActivity)context).finish();
                                    });
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            });
            popupMenu.show();
        }); */
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mInfo, mWH;
        public Producer mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mInfo = itemView.findViewById(R.id.info);
            mWH = itemView.findViewById(R.id.wh);

        }
    }


}