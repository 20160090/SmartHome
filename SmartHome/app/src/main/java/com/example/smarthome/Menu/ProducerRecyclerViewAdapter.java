package com.example.smarthome.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.model.Producer;
import com.example.smarthome.R;
import com.example.smarthome.model.User;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProducerRecyclerViewAdapter extends RecyclerView.Adapter<ProducerRecyclerViewAdapter.ViewHolder> {
    private User user = User.getInstance();
    private final List<Producer> mValues;
    private final String locationID;
    private final Context context;

    public ProducerRecyclerViewAdapter(Context context, List<Producer> items, String locationID) {
        this.context = context;
        this.mValues = items;
        this.locationID = locationID;
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
        holder.mWH.setText("" + mValues.get(position).getCurrentlyProduced() + "" + "WH");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Producer producer = mValues.get(position);
                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.mInfo);
                popupMenu.inflate(R.menu.producer_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                /*Intent intent = new Intent(context, AddingProducerActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("locationPos", locationPos);
                                bundle.putInt("producerPos", position);
                                intent.putExtras(bundle);
                                context.startActivity(intent);*/
                                mValues.set(position, producer);
                                notifyItemChanged(position);
                                break;
                            case R.id.delete:
                                FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
                                Map<String, String> data = new HashMap<>();
                                if (user.getLocations().stream().filter(l -> l.getId().equals(locationID)).findFirst().get().getProducers().size() > 1) {

                                    data.put("email", user.getFirebaseUser().getEmail());
                                    data.put("locationID", locationID);
                                    data.put("pvID", producer.getId());
                                    mFunctions
                                            .getHttpsCallable("deleteGenerator")
                                            .call(data)
                                            .addOnSuccessListener(result -> {

                                                mValues.remove(position);
                                                notifyItemRemoved(position);
                                            });
                                } else {
                                    data.put("locationID",locationID);
                                    data.put("email",user.getFirebaseUser().getEmail());
                                    mFunctions
                                            .getHttpsCallable("deleteLocation")
                                            .call(data)
                                            .addOnSuccessListener(result -> {
                                                //TODO: mosa fragen, wie notify LocationDetailActivity
                                            });
                                }
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });

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