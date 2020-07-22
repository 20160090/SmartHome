package com.example.smarthome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthome.Menu.MenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt, passwordConEt;
    private Button regBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        passwordConEt = findViewById(R.id.passwordConfirm);
        regBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();

    }

    @SuppressLint("ShowToast")
    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);

        final String email, password, passwordC;
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();
        passwordC = passwordConEt.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegistrationActivity.this, "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegistrationActivity.this, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(passwordC)) {
            Toast.makeText(RegistrationActivity.this, "Please enter password again!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.equals(password, passwordC)) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegistrationActivity.this, "Registration successful! Please verify your email", Toast.LENGTH_LONG).show();

                                                    progressBar.setVisibility(View.GONE);

                                                    Intent Main = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                    finish();
                                                    startActivity(Main);
                                                }
                                            }
                                        });
                            } else {
                                switch(((FirebaseAuthException)task.getException()).getErrorCode()){
                                    case "ERROR_INVALID_EMAIL": Toast.makeText(RegistrationActivity.this, "Please enter a correct email address", Toast.LENGTH_LONG).show(); break;
                                    case "ERROR_WEAK_PASSWORD": Toast.makeText(RegistrationActivity.this, "Your password should have at least 6 chars", Toast.LENGTH_LONG).show(); break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE": Toast.makeText(RegistrationActivity.this, "This email address is already in", Toast.LENGTH_LONG).show(); break;
                                    //TODO: weak password!!!!!!!!!!!!!!!!!
                                    default: Toast.makeText(RegistrationActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            Toast.makeText(RegistrationActivity.this, "Ka wos ma do schreim soid... Vatippt??", Toast.LENGTH_LONG).show();
            return;
        }


    }


}