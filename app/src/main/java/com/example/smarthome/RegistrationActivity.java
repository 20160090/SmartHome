package com.example.smarthome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthome.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt, passwordConEt;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        this.mAuth = FirebaseAuth.getInstance();

        this.emailEt = findViewById(R.id.etEmail);
        this.passwordEt = findViewById(R.id.etPassword);
        this.passwordConEt = findViewById(R.id.etPasswordConfirm);
        Button regBtn = findViewById(R.id.register);

        this.progressBar = findViewById(R.id.progressBar);

        regBtn.setOnClickListener(view -> registerUser());

    }

    @SuppressLint("ShowToast")
    private void registerUser() {
        this.progressBar.setVisibility(View.VISIBLE);

        final String email, password, passwordC;
        email = this.emailEt.getText().toString();
        password = this.passwordEt.getText().toString();
        passwordC = this.passwordConEt.getText().toString();

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

            this.mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {

                            Objects.requireNonNull(this.mAuth.getCurrentUser()).sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                                            Toast.makeText(RegistrationActivity.this, "Please verify your email!", Toast.LENGTH_LONG).show();

                                            this.progressBar.setVisibility(View.GONE);
                                            User.getInstance().setFirebaseUser(this.mAuth.getCurrentUser());

                                            Intent Main = new Intent(RegistrationActivity.this, LoginActivity.class);
                                            finish();
                                            startActivity(Main);
                                        }
                                    });
                        } else {
                            switch (((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode()) {
                                case "ERROR_INVALID_EMAIL":
                                    Toast.makeText(RegistrationActivity.this, "Please enter a correct email address", Toast.LENGTH_LONG).show();
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    Toast.makeText(RegistrationActivity.this, "Your password should have at least 6 chars", Toast.LENGTH_LONG).show();
                                    break;
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    Toast.makeText(RegistrationActivity.this, "This email address is already in", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                            this.progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            Toast.makeText(RegistrationActivity.this, "Passwörter stimmen nicht überein", Toast.LENGTH_LONG).show();
        }
    }
}