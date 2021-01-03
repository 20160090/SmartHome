package com.example.smarthome.menu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.R;
import com.example.smarthome.model.Device;
import com.example.smarthome.model.Forecast;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.example.smarthome.model.Weather;
import com.github.pwittchen.weathericonview.WeatherIconView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder> {
    private User user;
    private List<Forecast> values;
    private Weather actWeather;
    private Context context;

    public WeatherRecyclerViewAdapter(Context context, List<Forecast> weather, String locationID) {
        this.user = User.getInstance();
        this.values = weather;
        this.actWeather = user.getLocations().stream().filter(l -> l.getId().equals(locationID)).findAny().get().getWeather();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = this.values.get(position);

        holder.descriptionIcon.setIconSize(65);
        holder.descriptionIcon.setIconColor(Color.WHITE);
        holder.descriptionIcon.setIconResource(context.getString(Parser.getInstance().weatherDescriptionIcon(actWeather.getSunrise(), actWeather.getSunset(),holder.mItem)));

        holder.sunriseIcon.setIconSize(25);
        holder.sunriseIcon.setIconResource(context.getString(R.string.wi_sunrise));
        holder.sunriseIcon.setIconColor(Color.GRAY);

        holder.sunsetIcon.setIconSize(25);
        holder.sunsetIcon.setIconResource(context.getString(R.string.wi_sunset));
        holder.sunsetIcon.setIconColor(Color.GRAY);

        holder.tempIcon.setIconSize(25);
        holder.tempIcon.setIconResource(context.getString(R.string.wi_thermometer));
        holder.tempIcon.setIconColor(Color.GRAY);

        holder.mTemp.setText(""+values.get(position).getTemp()+"°C");
        holder.tempIcon.setIconResource(context.getString(R.string.wi_thermometer));
        holder.tempIcon.setIconColor(Color.GRAY);

        holder.mSunrise.setText(DateTimeFormatter.ISO_LOCAL_TIME.format(actWeather.getSunrise()));
        holder.mSunset.setText(DateTimeFormatter.ISO_LOCAL_TIME.format(actWeather.getSunset()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        holder.mTime.setText(values.get(position).getTime().format(formatter));
        holder.mDescription.setText(values.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return this.values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDescription, mTemp, mSunset, mSunrise, mTime;
        public Forecast mItem;
        public WeatherIconView descriptionIcon, sunriseIcon, sunsetIcon, tempIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mDescription = itemView.findViewById(R.id.descriptionTv);
            this.mTemp = itemView.findViewById(R.id.tempTv);
            this.mSunset = itemView.findViewById(R.id.sunsetTv);
            this.mSunrise = itemView.findViewById(R.id.sunriseTv);
            this.descriptionIcon = itemView.findViewById(R.id.description);
            this.sunriseIcon = itemView.findViewById(R.id.sunrisetIcon);
            this.sunsetIcon = itemView.findViewById(R.id.sunsetIcon);
            this.tempIcon = itemView.findViewById(R.id.tempIcon);
            this.mTime = itemView.findViewById(R.id.actTime);
        }
    }
}
