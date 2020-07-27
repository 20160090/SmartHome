package com.example.smarthome.Menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Location location;
    private User user;
    private TextView devices;
    private TextView producers;
    private TextView locationName;
    private int pos;

    public LocationFragment() {
        // Required empty public constructor
    }

    public static LocationFragment newInstance(int locationPos) {
        Bundle bundle = new Bundle();
        bundle.putInt("locationPos", locationPos);

        LocationFragment locationFragment = new LocationFragment();
        locationFragment.setArguments(bundle);
        return locationFragment;
    }

    public void readBundle(Bundle bundle) {
        if (bundle != null) {
            this.location = this.user.getLocations().get(bundle.getInt("locationPos"));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();
        readBundle(getArguments());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        locationName = view.findViewById(R.id.houseTv);
        locationName.setText(location.getName());

        devices = view.findViewById(R.id.devicesTv);
        devices.setText("Geräte\n" + location.getRunningNum() + " Geräte laufen");
        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                ((MenuActivity) getActivity()).changeFragment(bundle.getInt("locationPos"), 1);
                /*
                Bundle bundle = new Bundle();
                bundle.putInt("location", bundle.getInt("locationPos"));
                DevicesFragment devicesFragment = new DevicesFragment();
                devicesFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(((ViewGroup) (getView().getParent())).getId(), devicesFragment);
                transaction.addToBackStack(null);
                transaction.commit();*/
            }
        });

        producers = view.findViewById(R.id.producerTv);
        producers.setText("Photovoltaik\nMomentan erzeugte Wattstunden:  "+location.getCurrentEnergy()+" Wh");
        return view;
    }
}