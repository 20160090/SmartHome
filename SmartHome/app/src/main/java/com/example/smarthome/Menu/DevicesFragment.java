package com.example.smarthome.Menu;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;
import com.example.smarthome.Menu.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 */
public class DevicesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private Location location;
    private User user;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DevicesFragment() {
    }

    public static DevicesFragment newInstance(int locationPos) {
        Bundle bundle = new Bundle();
        bundle.putInt("locationPos", locationPos);

        DevicesFragment devicesFragment = new DevicesFragment();
        devicesFragment.setArguments(bundle);
        return devicesFragment;
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
        View view = inflater.inflate(R.layout.fragment_devices_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(location.getDevices()));
        }
        return view;
    }
}