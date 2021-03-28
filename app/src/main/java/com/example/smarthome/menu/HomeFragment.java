package com.example.smarthome.menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarthome.LoginActivity;
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
    private ProgressBar progressBar;
    private LinearLayout linearLayout;


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
        this.linearLayout = view.findViewById(R.id.linearLayoutHomeFragment);
        this.progressBar = view.findViewById(R.id.homeProgressBar);
        this.progressBar.setVisibility(View.GONE);

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

        this.swipeRefreshLayout.setOnRefreshListener(() -> {
                    Parser parser = Parser.getInstance();
                    parser.loadLocations(t -> {
                        locations();
                        swipeRefreshLayout.setRefreshing(false);
                        return 0;
                    });
                }
        );
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        locations();
    }

    public void locations() {
        this.linearLayout.removeAllViews();
        for (int i = 0; i < this.user.getLocations().size(); i++) {
            Location loc = this.user.getLocations().get(i);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            Fragment location = LocationFragment.newInstance(loc.getId());
            transaction
                    .setReorderingAllowed(true)
                    .add(R.id.linearLayoutHomeFragment, location, null)
                    .commit();
        }

        if (this.user.getLocations().size() == 0) {
            this.noLocation.setVisibility(View.VISIBLE);
        } else {
            this.noLocation.setVisibility(View.GONE);
        }
    }

    public void loadingDetail() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.swipeRefreshLayout.setEnabled(false);
        this.add.setEnabled(false);
    }

    public void endLoadingDetail() {
        this.progressBar.setVisibility(View.GONE);
        this.swipeRefreshLayout.setEnabled(true);
        this.add.setEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        locations();
    }
}