package com.example.smarthome.menu;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarthome.model.Device;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Producer;
import com.example.smarthome.model.User;
import com.example.smarthome.model.Weather;
import com.example.smarthome.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONException;
import org.json.JSONObject;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.smarthome.model.Device.RUNNING;
import static com.example.smarthome.model.Device.NOT_RUNNING;
import static com.example.smarthome.model.Device.SHOULD_BE_RUNNING;
import static com.example.smarthome.model.Device.SHOULD_NOT_BE_RUNNING;


public class MenuActivity extends AppCompatActivity {
    private static final int[] TAB_TITLES = new int[]{R.string.statisticTab, R.string.homeTab, R.string.profileTab};
    private User user;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        viewPager = findViewById(R.id.viewPager2);
        TabLayout tabLayout = findViewById(R.id.tabBar);

        PagerAdapter pagerAdapter = new PagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);


        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(TAB_TITLES[position]);
                viewPager.setCurrentItem(1, true);
            }
        }).attach();

        this.user = User.getInstance();
        //this.dummy();


        final FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("city", "Perg");

        mFunctions.getHttpsCallable("getWeatherOnCall").call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        System.out.println("ONCALL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + e.getMessage());
                    }
                } else {

                    Toast.makeText(MenuActivity.this, ((task.getResult()).getData()).toString(), Toast.LENGTH_LONG).show();

                    try {

                        String data =task.getResult().getData().toString();
                        //parseWeather( new JSONObject (data));
                        parseWeather(new JSONObject(getResources().getString(R.string.event)));
                        //{dt=1597731540, temp=17, sunrise=1597723220, sunset=1597774231, "description"="few clouds"}
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Map<String, Object> pvID = new HashMap<>();
        pvID.put("pvID","6dd05177-193f-4580-97bd-3331e3abe530");
        mFunctions.getHttpsCallable("addPV")
                .call(pvID)
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        mFunctions.getHttpsCallable("getLocationData").call().addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                            @Override
                            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                Toast.makeText(MenuActivity.this,task.getResult().getData().toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
      /*  mFunctions.getHttpsCallable("getFirstJSON").call(data).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
            @Override
            public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        System.out.println("ONCALL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + e.getMessage());
                    }
                } else {
                    //int i = task.getResult().getData();
                    Toast.makeText(MenuActivity.this, ((task.getResult()).getData()).toString(), Toast.LENGTH_LONG).show();

                    try {
                        parseWeather( new JSONObject (task.getResult().getData().toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
*/
        /*RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        StringRequest mCloudRequest = new StringRequest(Request.Method.GET, "https://us-central1-diplomarbeit-33903.cloudfunctions.net/getWeather", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("ONREQUEST!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + error.getMessage());
               // mTextView.setText(String.format(Locale.getDefault(),"%s", error.getMessage()));

            }
        });
        mRequestQueue.add(mCloudRequest);*/

    }


    public void dummy() {
        Location loc = new Location();
        loc.setName("Zu Hause");
        loc.setZip(4300);
        loc.setCity("St.Valentin");
        loc.setCountry("AT");
        loc.addDevice(new Device("Waschmaschine", "Samsung AddWash WW4500K", "Hersteller", RUNNING, 0.78));
        loc.addDevice(new Device("Trockner", "DV5000M Trockner", "Hersteller", NOT_RUNNING, 0.90));
        loc.addDevice(new Device("Geschirspüler", "SDW7500", "Hersteller", SHOULD_BE_RUNNING, 83));
        loc.addDevice(new Device("Staubsauger Steckdose", "Roomba s9+", "Hersteller", SHOULD_NOT_BE_RUNNING, 0.20));
        loc.addDevice(new Device("Steckdose Schlafzimmer", "Fritz Dect 200/210", "Hersteller", NOT_RUNNING, 0.0));
        loc.addDevice(new Device("Steckdose Wohnzimmer", "Fritz Dect 200/210", "Hersteller", RUNNING, 0.0));
        loc.addProducer(new Producer("Photovoltaik", "Fronius", "Test", 30.1));
        Collections.sort(loc.getDevices());
        this.user.addLocation(loc);
        Location loc2 = new Location("Appartment", 1020, "Wien", "AT");
        loc2.setDevices(loc.getDevices());
        loc2.setProducers(loc.getProducers());
        loc2.addProducer(new Producer("Photovoltaik", "Fronius", "test1", 25.7));

        this.user.addLocation(loc2);
        this.user.setDisplayName(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());

    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void parseWeather(JSONObject object) {
        try {
            Timestamp dt = new Timestamp(Long.parseLong(object.getString("dt"))*1000);
            LocalDateTime dateTime;
            dateTime = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String description = object.getString("description");
            //Double temp = object.getDouble("temp");

            Timestamp sr = new Timestamp(Long.parseLong(object.getString("sunrise"))*1000);
            Timestamp ss = new Timestamp(Long.parseLong(object.getString("sunset"))*1000);

            LocalTime sunrise = sr.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime sunset = ss.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();



            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + sunset + "!!" + sunrise + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!2");
            //user.setWeather(new Weather(sunset));
            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + user.getWeather().getSunset() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}