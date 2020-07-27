package com.example.smarthome.Menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarthome.Model.Device;
import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.Producer;
import com.example.smarthome.Model.User;
import com.example.smarthome.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static com.example.smarthome.Model.Device.RUNNING;
import static com.example.smarthome.Model.Device.NOT_RUNNING;
import static com.example.smarthome.Model.Device.SHOULD_BE_RUNNING;
import static com.example.smarthome.Model.Device.SHOULD_NOT_BE_RUNNING;

//oder FragmentActivity??
public class MenuActivity extends AppCompatActivity {
    private static final int[] TAB_TITLES = new int[]{R.string.statisticTab, R.string.homeTab, R.string.profileTab};
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        final ViewPager2 viewPager = findViewById(R.id.viewPager2);
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
        this.dummy();


    }

    public void changeFragment(int locationPos, int type) {
        if(type==1){
            Bundle args = new Bundle();
            args.putInt("locationPos", locationPos);
            DevicesFragment devicesFragment = new DevicesFragment();
            devicesFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.homeFL, devicesFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else{

        }

    }

    public void dummy() {
        Location loc = new Location();
        loc.setName("Zu Hause");
        loc.setZip(4300);
        loc.addDevice(new Device("Waschmaschine", "Samsung AddWash WW4500K", RUNNING, 0.78));
        loc.addDevice(new Device("Trockner", "DV5000M Trockner", NOT_RUNNING, 0.90));
        loc.addDevice(new Device("Geschirspüler", "SDW7500", SHOULD_BE_RUNNING, 83));
        loc.addDevice(new Device("Staubsauger Steckdose", "Roomba s9+", SHOULD_NOT_BE_RUNNING, 0.20));
        loc.addDevice(new Device("Steckdose Schlafzimmer", "Fritz Dect 200/210", NOT_RUNNING, 0.0));
        loc.addDevice(new Device("Steckdose Wohnzimmer", "Fritz Dect 200/210", RUNNING, 0.0));
        loc.addProducer(new Producer("Photovoltaik", "Fronius", "Test",30.1));
        this.user.addLocation(loc);
        Location loc2 = new Location("Appartment", 1020, "Wien","AT");
        loc2.setDevices(loc.getDevices());
        loc2.setProducers(loc.getProducers());
        loc2.addProducer(new Producer("Photovoltaik", "Fronius", "test1",25.7));

        this.user.addLocation(loc2);
        this.user.setDisplayName(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());

    }

}