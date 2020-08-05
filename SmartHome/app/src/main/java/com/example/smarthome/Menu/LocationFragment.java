package com.example.smarthome.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.smarthome.LocationDetailActivity;
import com.example.smarthome.adding.AddingLocationActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.User;
import com.example.smarthome.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    private Location location;
    private User user;
    private int locationPos;
    private int DELETED = 1;

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
            this.locationPos = bundle.getInt("locationPos");
            this.location = this.user.getLocations().get(bundle.getInt("locationPos"));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = User.getInstance();
        readBundle(getArguments());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LocationDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("locationPos", locationPos);
                intent.putExtras(bundle);
                startActivityForResult(intent, DELETED);
                locationDeleted();
            }
        });
        final TextView locationName = view.findViewById(R.id.houseTv);
        TextView devices = view.findViewById(R.id.devicesTv);
        TextView producers = view.findViewById(R.id.producerTv);
        final ConstraintLayout cl = view.findViewById(R.id.locationFragmentCL);


        locationName.setText(location.getName());
        devices.setText("Geräte\n" + location.getRunningNum() + " Geräte laufen");
        producers.setText("Photovoltaik\nMomentan erzeugte Wattstunden:  " + location.getCurrentEnergy() + " Wh");
        cl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), locationName);
                popupMenu.inflate(R.menu.producer_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit:
                                Intent intent = new Intent(getContext(), AddingLocationActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("locationPos", locationPos);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            case R.id.delete:
                                new AlertDialog.Builder(getContext())
                                        .setTitle(getResources().getString(R.string.remove_location))
                                        .setMessage(getResources().getString(R.string.really_delete_location))
                                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                user.getLocations().remove(locationPos);
                                                locationDeleted();

                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.no), null)
                                        .show();
                                break;
                            default:
                                return false;
                        }
                        return true;
                    }
                });
                popupMenu.show();


                return true;
            }
        });
        return view;
    }

    private void locationDeleted() {
        HomeFragment homeFragment = ((HomeFragment) LocationFragment.this.getParentFragment());
        homeFragment.locations();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DELETED) {
            if (resultCode == Activity.RESULT_OK) {
                locationDeleted();
            }
        }
    }
}