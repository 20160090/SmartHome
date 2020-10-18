package com.example.smarthome.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.adding.AddingProducerActivity;
import com.example.smarthome.model.Device;
import com.example.smarthome.model.Producer;
import com.example.smarthome.R;

import java.util.List;

public class ProducerRecyclerViewAdapter extends RecyclerView.Adapter<ProducerRecyclerViewAdapter.ViewHolder> {
    private final List<Producer> mValues;
    private final int locationPos;
    private final Context context;

    public ProducerRecyclerViewAdapter(Context context, List<Producer> items, int locationPos) {
        this.context = context;
        this.mValues = items;
        this.locationPos = locationPos;
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
        holder.mInfo.setText(mValues.get(position).getName() + "\n" + mValues.get(position).getType());
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
                                mValues.set(position,producer);
                                notifyItemChanged(position);
                                break;
                            case R.id.delete:
                                mValues.remove(position);
                                notifyItemRemoved(position);
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