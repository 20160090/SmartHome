package com.example.smarthome.menu;

import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.example.smarthome.R;


import com.example.smarthome.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.google.firebase.functions.FirebaseFunctions;


public class MenuActivity extends AppCompatActivity {
    private static final int[] TAB_TITLES = new int[]{R.string.statisticTab, R.string.homeTab, R.string.profileTab};
    private ViewPager2 viewPager;
    private User user;
    private FirebaseFunctions mFunctions;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_menu);

        this.viewPager = findViewById(R.id.viewPager2);
        this.user = User.getInstance();
        this.mFunctions = FirebaseFunctions.getInstance();


        TabLayout tabLayout = findViewById(R.id.tabBar);
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        this.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, this.viewPager, (tab, position) -> {
            tab.setText(TAB_TITLES[position]);
            this.viewPager.setCurrentItem(1, false);
        }).attach();


        //Test
        /*Location location = new Location();
        location.setId("i8ofYY4Qddz9CZrz1rSu");
        location.setName("TESTT");
        location.setCountry("AT");
        location.setCity("Perg");
        location.setZip(4320);
        callGetWeather(location);
        this.user.getLocations().add(location);*/

        ProgressBar progressBar = findViewById(R.id.menuPB);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)


    public ViewPager2 getViewPager() {
        return viewPager;
    }



        //zum testen*/
        // Weather weather = new Weather(LocalDateTime.now(),"clouds",10.5, LocalTime.now(), LocalTime.now());
        //location.setWeather(weather);




    //TODO: stückeln getGenerators(email,locationID) getPVData(pvID)
}
