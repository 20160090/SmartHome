package com.example.smarthome.menu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.smarthome.LoginActivity;
import com.example.smarthome.R;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button signOutBtn = view.findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(view1 -> {
            this.mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
        Button faqBtn = view.findViewById(R.id.faqBtn);
        faqBtn.setOnClickListener(view2 -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goga-barabadze.github.io/SmartHome-FAQ/"));
            startActivity(browserIntent);
        });

        return view;
    }
}