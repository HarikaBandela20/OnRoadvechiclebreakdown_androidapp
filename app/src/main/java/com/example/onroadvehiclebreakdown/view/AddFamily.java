package com.example.onroadvehiclebreakdown.view;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onroadvehiclebreakdown.MainActivity;
import com.example.onroadvehiclebreakdown.R;
import com.example.onroadvehiclebreakdown.dao.DAO;
import com.example.onroadvehiclebreakdown.form.Family;
import com.example.onroadvehiclebreakdown.util.Constants;
import com.example.onroadvehiclebreakdown.util.Session;

public class AddFamily extends AppCompatActivity {

    EditText e1,e2,e3;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_family);

        final Session session=new Session(getApplicationContext());

        e1=(EditText)findViewById(R.id.addfamilymobile1);
        e2=(EditText)findViewById(R.id.addfamilymobile2);
        e3=(EditText)findViewById(R.id.addfamilymobile3);

        b1=(Button)findViewById(R.id.addFamilyButton);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String m1=e1.getText().toString();
                String m2=e2.getText().toString();
                String m3=e3.getText().toString();

                if(m1==null|| m2==null|| m3==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(m1.length()!=10 || m2.length()!=10 || m3.length()!=10) {
                    Toast.makeText(getApplicationContext(), "Invalid Mobile", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Family family=new Family();

                    family.setFamilyId(DAO.getUnicKey(Constants.FAMILY_DB));
                    family.setMobile1(m1);
                    family.setMobile2(m2);
                    family.setMobile3(m3);
                    family.setUserName(session.getusename());

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.FAMILY_DB,family,family.getFamilyId());
                        Toast.makeText(getApplicationContext(),"Added Successfully",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"Register Error",Toast.LENGTH_SHORT).show();
                        Log.v("Family Registration", ex.toString());
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
