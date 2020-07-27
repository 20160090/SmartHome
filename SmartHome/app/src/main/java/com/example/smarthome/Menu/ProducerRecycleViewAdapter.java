package com.example.smarthome.Menu;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.DeviceProducerActivity;
import com.example.smarthome.Model.Device;
import com.example.smarthome.Model.Producer;
import com.example.smarthome.R;

import java.util.List;

public class ProducerRecycleViewAdapter  extends RecyclerView.Adapter<ProducerRecycleViewAdapter.ViewHolder> {
    private final List<Producer> mValues;
    public ProducerRecycleViewAdapter(List<Producer> items){mValues = items;}


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_devices, parent, false);
        return new ProducerRecycleViewAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        //holder.mOutput.setText(""+mValues.get(position).getCurrentlyProduced()+"");
        holder.mOutput.setText(""+mValues.get(position).getCurrentlyProduced()+""+"Wh");
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mOutput;
        public Producer mItem;
        public ViewHolder( View itemView) {
            super(itemView);
            mView=itemView;
            mNameView = (TextView) itemView.findViewById(R.id.item_number);
            mOutput = (TextView) itemView.findViewById(R.id.content);

        }
    }
}