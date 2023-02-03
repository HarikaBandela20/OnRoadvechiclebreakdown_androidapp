package com.example.onroadvehiclebreakdown.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onroadvehiclebreakdown.MainActivity;
import com.example.onroadvehiclebreakdown.R;
import com.example.onroadvehiclebreakdown.dao.DAO;
import com.example.onroadvehiclebreakdown.form.User;
import com.example.onroadvehiclebreakdown.util.Constants;
import com.example.onroadvehiclebreakdown.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {

    EditText mobile;
    EditText description;
    Button updateMechanicSubmit;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_profile);

        final Session s = new Session(getApplicationContext());

        mobile = (EditText) findViewById(R.id.updatemechanicmobile);
        description = (EditText) findViewById(R.id.updateMechanicDescription);

        updateMechanicSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String phone = mobile.getText().toString();
                String info = description.getText().toString();

                if (phone == null || info ==null) {
                    Toast.makeText(getApplicationContext(), "Please Enter Mechanic Status or Count", Toast.LENGTH_SHORT).show();
                } else {

                    DAO dao = new DAO();
                    dao.getDBReference(Constants.USER_DB).child(s.getusename()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User user=dataSnapshot.getValue(User.class);

                            if(user!=null)
                            {
                                user.setMobile(phone);
                                user.setDescription(info);
                                dao.addObject(Constants.USER_DB,user,user.getUsername());

                                Intent i = new Intent(getApplicationContext(),MechanicHome.class);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}
