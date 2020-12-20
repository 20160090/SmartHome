package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smarthome.menu.MenuActivity;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private EditText emailEt, passwordEt;
    private ProgressBar progressBar;
    private User user;
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Parser parser = new Parser();
        this.user = User.getInstance();
        this.mAuth = FirebaseAuth.getInstance();

        this.emailEt = findViewById(R.id.email);
        this.passwordEt = findViewById(R.id.password);

        Button loginBtn = findViewById(R.id.loginBtn);
        Button signupBtn = findViewById(R.id.signupBtn);

        this.progressBar = findViewById(R.id.progressBar);

        signupBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
        loginBtn.setOnClickListener(view -> loginUserAccount());

        if (this.mAuth.getCurrentUser() != null) {
            if (this.mAuth.getCurrentUser().isEmailVerified()) {

                this.user.setFirebaseUser(this.mAuth.getCurrentUser());
            //    this.user.getLoadingData().observe(this, )

                //neuer Versuch










                //Alter Code, funktioniert
                parser.callGetLocations(result -> result);
                parser.callCompanies();
                finish();
                Intent Main = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(Main);
            } else {
                Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loginUserAccount() {
        this.progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();

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
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
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

    private void loadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Parser parser = new Parser();
                parser.callGetLocations(result -> result);

            }
        })
    }

}