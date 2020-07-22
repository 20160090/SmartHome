package com.example.smarthome.Menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.smarthome.Model.User;
import com.example.smarthome.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.Objects;

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
        for(int i=0; i<user.getLocations().size(); i++){
            View view1 = inflater.inflate(R.layout.fragment_home, container, false);
            locationName=view1.findViewById(R.id.houseTv);
            locationName.setText(user.getLocations().get(i).getName());

            devices = view1.findViewById(R.id.devicesTv);
            devices.setText("Geräte\n"+user.getLocations().get(i).getRunningNum()+" Geräte laufen");
            int pos =i;
            devices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //((MenuActivity)getActivity()).fragmentChange(0);
                    Bundle args = new Bundle();
                    args.putInt("locationPos", 0);
                    DevicesFragment devicesFragment = new DevicesFragment();
                    devicesFragment.setArguments(args);
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    transaction.replace(((ViewGroup)(getView().getParent())).getId(), devicesFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
            container.addView(view1);
        }
        return container.findFocus();
    }

}