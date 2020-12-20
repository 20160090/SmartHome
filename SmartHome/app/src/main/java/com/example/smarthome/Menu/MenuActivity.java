package com.example.smarthome.menu;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.example.smarthome.R;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MenuActivity extends AppCompatActivity {
    private static final int[] TAB_TITLES = new int[]{R.string.statisticTab, R.string.homeTab, R.string.profileTab};
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_menu);
        this.viewPager = findViewById(R.id.viewPager2);

        TabLayout tabLayout = findViewById(R.id.tabBar);
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        this.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, this.viewPager, (tab, position) -> {
            tab.setText(TAB_TITLES[position]);
            this.viewPager.setCurrentItem(1, false);
        }).attach();

    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

}
