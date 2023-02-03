package com.example.onroadvehiclebreakdown.view;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.onroadvehiclebreakdown.MainActivity;
import com.example.onroadvehiclebreakdown.R;
import com.example.onroadvehiclebreakdown.util.Session;

public class MechanicHome extends AppCompatActivity {

    Button updateprofile;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        updateprofile=(Button) findViewById(R.id.mechanicupdateprofilebutton);
        logout=(Button) findViewById(R.id.mechaniclogout);

        final Session s = new Session(getApplicationContext());

        updateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),UpdateProfile.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
    }
}