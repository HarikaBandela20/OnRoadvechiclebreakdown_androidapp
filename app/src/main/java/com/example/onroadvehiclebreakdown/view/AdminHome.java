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

public class AdminHome extends AppCompatActivity {

    private Session session;

    Button addpolice;
    Button addHospital;
    Button adminLogout;
    Button viewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        addHospital=(Button) findViewById(R.id.addhospital);
        addpolice=(Button) findViewById(R.id.addpolice);
        viewUsers=(Button) findViewById(R.id.adminviewusers);
        adminLogout=(Button) findViewById(R.id.adminlogout);

        final Session s = new Session(getApplicationContext());

        addHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddHospital.class);
                startActivity(i);
            }
        });

        addpolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), AddPolice.class);
                startActivity(i);
            }
        });

        adminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                s.loggingOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        viewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),AdminListUser.class);
                startActivity(i);
            }
        });
    }
}