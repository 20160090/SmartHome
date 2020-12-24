package com.example.smarthome.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarthome.adding.AddingLocationActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.example.smarthome.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.Objects;

public class HomeFragment extends Fragment {

    private FloatingActionButton add;
    private User user;
    private TextView noLocation;
    private SwipeRefreshLayout swipeRefreshLayout;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = User.getInstance();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        this.add = view.findViewById(R.id.addFAB);
        this.noLocation = view.findViewById(R.id.noLocation);
        this.swipeRefreshLayout = view.findViewById(R.id.swipeContainer);

        MenuActivity menuActivity = (MenuActivity) getActivity();
        ViewPager2 viewPager2 = Objects.requireNonNull(menuActivity).getViewPager();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    add.setVisibility(View.VISIBLE);
                } else {
                    add.setVisibility(View.INVISIBLE);
                }
            }
        });
        this.add.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), AddingLocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("locationPos", -1);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                         @Override
                                                         public void onRefresh() {
                                                             Parser parser = new Parser();
                                                             parser.callGetLocations(result -> result);
                                                             locations();
                                                             //swipeRefreshLayout.setRefreshing(false);
                                                         }
                                                     }

        );
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        locations();
    }

    public void locations() {
        FragmentTransaction transaction1 = getChildFragmentManager().beginTransaction();
        Fragment fragment1 = getChildFragmentManager().findFragmentByTag("location1");
        Fragment fragment2 = getChildFragmentManager().findFragmentByTag("location2");
        Fragment fragment3 = getChildFragmentManager().findFragmentByTag("location3");
        Fragment fragment4 = getChildFragmentManager().findFragmentByTag("location4");
        Fragment fragment5 = getChildFragmentManager().findFragmentByTag("location5");
        if (fragment1 != null) {
            transaction1.remove(fragment1);
        }
        if (fragment2 != null) {
            transaction1.remove(fragment2);
        }
        if (fragment3 != null) {
            transaction1.remove(fragment3);
        }
        if (fragment4 != null) {
            transaction1.remove(fragment4);
        }
        if (fragment5 != null) {
            transaction1.remove(fragment5);
        }
        transaction1.commit();

        for (int i = 0; i < this.user.getLocations().size(); i++) {
            Location loc = this.user.getLocations().get(i);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            Fragment location = LocationFragment.newInstance(loc.getId());
            switch (i) {
                case 0:
                    transaction.replace(R.id.fl1, location, "location1").commit();
                    break;
                case 1:
                    transaction.replace(R.id.fl2, location, "location2").commit();
                    break;
                case 2:
                    transaction.replace(R.id.fl3, location, "location3").commit();
                    break;
                case 3:
                    transaction.replace(R.id.fl4, location, "location4").commit();
                    break;
                case 4:
                    transaction.replace(R.id.fl5, location, "location5").commit();
                    this.add.setVisibility(View.GONE);
                    break;
            }
        }
        if (this.user.getLocations().size() == 0) {
            this.noLocation.setVisibility(View.VISIBLE);

        } else {
            this.noLocation.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        locations();
    }
}