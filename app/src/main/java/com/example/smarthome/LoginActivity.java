package com.example.smarthome;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.menu.MenuActivity;
import com.example.smarthome.model.Location;
import com.example.smarthome.model.Parser;
import com.example.smarthome.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private EditText emailEt, passwordEt;
    private TextView loadingText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunction;
    private Parser parser;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.parser = Parser.getInstance();
        User user = User.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.parser = Parser.getInstance();

        this.emailEt = findViewById(R.id.email);
        this.passwordEt = findViewById(R.id.password);

        Button loginBtn = findViewById(R.id.loginBtn);
        Button signupBtn = findViewById(R.id.signupBtn);

        this.progressBar = findViewById(R.id.progressBar);
        this.loadingText = findViewById(R.id.loadingText);

        signupBtn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
        loginBtn.setOnClickListener(view -> loginUserAccount());
        LinearLayout linearLayout = findViewById(R.id.loginLinearLayout);
        if (this.mAuth.getCurrentUser() != null) {
            if (this.mAuth.getCurrentUser().isEmailVerified()) {

                user.setFirebaseUser(this.mAuth.getCurrentUser());
                //    this.user.getLoadingData().observe(this, )
                this.progressBar.setVisibility(View.VISIBLE);
                this.loadingText.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                //neuer Versuch
              /*  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                finish();
                                Intent Main = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(Main);
                            }
                        });
                    }
                });*/
                //nächster Versuch
      /*          Executor executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                finish();
                                Intent Main = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(Main);
                            }
                        });
                    }

                });



*/


                //Alter Code, funktioniert
                /*Location test = new Location("z3SzhrJAEwqpBxLzlKrD", "test", 4220, "Testhausen", "At", new ArrayList<Device>(), new ArrayList<Producer>(), new Weather(), new ArrayList<Forecast>());
                CountDownLatch countDownLatch = new CountDownLatch(20);
                for (int i = 0; i < 20; i++) {
                    parser.callGetGeneratorCallback(test, countDownLatch, null);
                }
                //loadData();

//interessant, Problem: counDownLatch.await() hat den Hauptthread aufgehalten --> deshalb new Thread
                new Thread(() -> {
                    try {
                        System.out.println("vorher");
                        countDownLatch.await();
                        System.out.println("fertig");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();*/

                /*
                parser.callCompanies();
                finish();
                Intent Main = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(Main);
                 */
                loadData();

            } else {
                Toast.makeText(LoginActivity.this, "Please verify your email.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadData() {
        parser.callCompanies();
        parser.loadLocations(t -> {
            Intent menu = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(menu);
            finish();
            return 0;
        });
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

    private void parseGetLocations(JSONArray array) {
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject act = array.getJSONObject(i);
                JSONObject object = act.getJSONObject("Location");
                Location location = new Location();

                location.setId(object.getString("locationID"));
                location.setName(object.getString("name"));
                location.setZip(object.getInt("zip"));
                location.setCity(object.getString("city"));
                location.setCountry(object.getString("country"));

                parser.callGetGeneratorCallback(location, null, t1 -> {
                    parser.callGetDevicesCallback(location, t2 -> {
                        parser.callGetWeatherCallback(location, t3 -> {
                            parser.callGetForecastCallback(location, t4 -> {
                                User.getInstance().addLocation(location);
                                return 0;
                            });
                            return 0;
                        });
                        return 0;
                    });
                    return 0;
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}