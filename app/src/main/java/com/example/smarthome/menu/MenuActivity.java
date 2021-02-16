package com.example.smarthome.menu;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.example.smarthome.R;

import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MenuActivity extends AppCompatActivity {
    private static final int[] TAB_TITLES = new int[]{R.string.statisticTab, R.string.homeTab, R.string.profileTab};
    private ViewPager2 viewPager;
    private FirebaseFunctions mFunction;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_menu);
        this.mFunction = FirebaseFunctions.getInstance();
        this.user = User.getInstance();
        this.viewPager = findViewById(R.id.viewPager2);

        TabLayout tabLayout = findViewById(R.id.tabBar);
        PagerAdapter pagerAdapter = new PagerAdapter(this);
        this.viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, this.viewPager, (tab, position) -> {
            tab.setText(TAB_TITLES[position]);
            this.viewPager.setCurrentItem(1, false);
        }).attach();
        Map<String, String> data1 = new HashMap<>();
        data1.put("email", user.getFirebaseUser().getEmail());
        this.mFunction.getHttpsCallable("getLocationData")
                .call(data1)
                .addOnSuccessListener(result -> {
                    try {
                        Parser.getInstance().parseConsumptions(new JSONArray(result.getData().toString()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                })
                .addOnCompleteListener(task -> {
                    task.getResult().getData();
                });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("TOKEN", "Fetching FCM registration token failed", task.getException());
                return;
            }
            Map<String,String>data = new HashMap<>();
            data.put("email", User.getInstance().getFirebaseUser().getEmail());
            data.put("token", task.getResult());
            FirebaseFunctions.getInstance().getHttpsCallable("updateToken")
                    .call(data)
                    .addOnSuccessListener(result -> System.out.println(result.getData()));

        });

    }


    public ViewPager2 getViewPager() {
        return viewPager;
    }

}
