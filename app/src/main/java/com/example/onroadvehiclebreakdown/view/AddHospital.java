package com.example.onroadvehiclebreakdown.view;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onroadvehiclebreakdown.R;
import com.example.onroadvehiclebreakdown.dao.DAO;
import com.example.onroadvehiclebreakdown.form.User;
import com.example.onroadvehiclebreakdown.util.Constants;

public class AddHospital extends AppCompatActivity {

    EditText e1,e2,e3,e4,e5;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_hospital);

        e1=(EditText)findViewById(R.id.addhospitalname);
        e2=(EditText)findViewById(R.id.addhospitalEmail);
        e3=(EditText)findViewById(R.id.addhospitalMobile);
        e4=(EditText)findViewById(R.id.addhospitalDescription);
        e5=(EditText)findViewById(R.id.addhospitalAddress);

        b1=(Button)findViewById(R.id.registerHospital);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=e1.getText().toString();
                String email=e2.getText().toString();
                String mobile=e3.getText().toString();
                String description=e4.getText().toString();
                String address=e5.getText().toString();

                if(name==null|| email==null|| mobile==null|| description==null|| address==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(mobile.length()<10|| mobile.length()>12) {
                    Toast.makeText(getApplicationContext(), "Invalid Mobile", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    User user=new User();

                    user.setName(name);
                    user.setEmail(email);
                    user.setMobile(mobile);
                    user.setDescription(description);
                    user.setAddress(address);
                    user.setType("hospital");

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.USER_DB,user,user.getMobile());

                        Toast.makeText(getApplicationContext(),"Hospital Added Successfully",Toast.LENGTH_SHORT).show();

                        Intent i=new Intent(getApplicationContext(),AdminHome.class);
                        startActivity(i);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"Register Error",Toast.LENGTH_SHORT).show();
                        Log.v("Hospital Registration", ex.toString());
                        ex.printStackTrace();
                    }

                }
            }
        });
    }
}
