package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarthome.Model.Location;
import com.example.smarthome.Model.User;

import org.w3c.dom.Text;

public class AddingLocationActivity extends AppCompatActivity {
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_location);

        user = User.getInstance();

        Button back = findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Button con = findViewById(R.id.continueBtn);
        final TextView name = findViewById(R.id.nameEt);
        final TextView country = findViewById(R.id.countryEt);
        final TextView zip = findViewById(R.id.zipEt);
        final TextView city = findViewById(R.id.cityEt);
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(country.getText()) || TextUtils.isEmpty(zip.getText()) || TextUtils.isEmpty(city.getText())) {
                    Toast.makeText(AddingLocationActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                } else {
                    Location location = new Location(name.getText().toString(), Integer.parseInt(zip.getText().toString()), city.getText().toString(), country.getText().toString());
*/
                    Bundle bundle = new Bundle();
                    bundle.putInt("locationPos", 0);

                    Intent intent = new Intent(AddingLocationActivity.this, DeviceProducerActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                //}
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}