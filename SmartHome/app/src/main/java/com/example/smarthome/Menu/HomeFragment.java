package com.example.smarthome.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthome.Adding.AddingLocationActivity;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FrameLayout mainFrameLayout;
    private FrameLayout frameLayout;
    private TextView devices;
    private TextView locationName;
    private Button add;
    private User user;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        user = User.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        add = view.findViewById(R.id.addBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), AddingLocationActivity.class);
                startActivity(intent);
            }
        });

        //ViewStub
        /* for(int i=0; i<user.getLocations().size(); i++){
            int num=i+1;
            String name="vs"+""+num+"";
            ViewStub viewStub = (ViewStub)view.findViewById(R.id.vs1);
            viewStub.setLayoutResource(R.layout.fragment_profile);
            viewStub.inflate();
        }*/

        /*LinearLayout locations_layout = (LinearLayout) view.findViewById(R.id.homeFL);
        for (int i = 0; i < user.getLocations().size(); i++) {
            Location location = user.getLocations().get(i);
            View to_add = inflater.inflate(R.layout.location_element, locations_layout, false);
            locationName = to_add.findViewById(R.id.houseTv);
            locationName.setText(location.getName());

            devices = to_add.findViewById(R.id.devicesTv);
            devices.setText("Geräte\n" + location.getRunningNum() + " Geräte laufen");
            devices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("location", 0);

                    DevicesFragment devicesFragment = new DevicesFragment();
                    devicesFragment.setArguments(bundle);

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(((ViewGroup) (getView().getParent())).getId(), devicesFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });

            locations_layout.addView(to_add);
        }*/

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        for(int i=0; i<user.getLocations().size(); i++){
            Fragment location = LocationFragment.newInstance(i);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            switch(i){
                case 0: transaction.replace(R.id.fl1, location);
                            //nur zum testen
                    Fragment a = LocationFragment.newInstance(1);
                    transaction.replace(R.id.fl3, a);
                    Fragment b = LocationFragment.newInstance(1);
                    transaction.replace(R.id.fl4, b);
                    Fragment c = LocationFragment.newInstance(1);
                    transaction.replace(R.id.fl5, c).commit();break;
                case 1: transaction.replace(R.id.fl2, location).commit(); break;
                case 2: break;
                case 3: break;
                case 4: break;
            }
        }

    }
}