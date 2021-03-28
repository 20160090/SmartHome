package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.menu.MenuActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private EditText emailEt, passwordEt;
    private TextView loadingText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunction;
    private Parser parser;
    private User user;
    private ConstraintLayout linearLayout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.parser = Parser.getInstance();
        this.user = User.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.parser = Parser.getInstance();

        this.linearLayout = findViewById(R.id.loginLayout);
        this.emailEt = findViewById(R.id.etEmail);
        this.passwordEt = findViewById(R.id.etPassword);

        Button loginBtn = findViewById(R.id.loginBtn);
        Button signupBtn = findViewById(R.id.signupBtn);

        this.progressBar = findViewById(R.id.progressBar);
        this.loadingText = findViewById(R.id.loadingText);

        signupBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
        loginBtn.setOnClickListener(view -> loginUserAccount());

        if (this.mAuth.getCurrentUser() != null) {
            if (this.mAuth.getCurrentUser().isEmailVerified()) {
                this.user.setFirebaseUser(this.mAuth.getCurrentUser());
                this.progressBar.setVisibility(View.VISIBLE);
                this.loadingText.setVisibility(View.VISIBLE);
                this.linearLayout.setVisibility(View.GONE);
                loadData();

            } else {
                Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadData() {
        this.parser.callCompanies();
        this.parser.loadLocations(t -> {
            Intent menu = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(menu);
            finish();
            return 0;
        });
    }

    private void loginUserAccount() {
        this.progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = this.emailEt.getText().toString();
        password = this.passwordEt.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User.getInstance().setFirebaseUser(mAuth.getCurrentUser());
                if (User.getInstance().getFirebaseUser().isEmailVerified()) {

                    this.progressBar.setVisibility(View.VISIBLE);
                    this.loadingText.setVisibility(View.VISIBLE);
                    this.linearLayout.setVisibility(View.GONE);
                    loadData();
                } else {
                    Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_LONG).show();
                }
            } else {
                switch (((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode()) {
                    case "ERROR_USER_NOT_FOUND":
                        Toast.makeText(LoginActivity.this, "Please signup first", Toast.LENGTH_LONG).show();
                        break;
                    case "ERROR_WRONG_PASSWORD":
                        Toast.makeText(LoginActivity.this, "Wrong Password, please try again", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "Login failed! Please try again!", Toast.LENGTH_LONG).show();
                }
                this.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}