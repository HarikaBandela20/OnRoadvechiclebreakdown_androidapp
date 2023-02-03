package com.example.onroadvehiclebreakdown.view;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.onroadvehiclebreakdown.util.Session;

public class RegisterActivity extends AppCompatActivity {

    EditText e1,e2,e3,e4,e5,e6,e7,e8;
    Button b1;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        final Session s=new Session(getApplicationContext());

        e1=(EditText)findViewById(R.id.registerPhone);
        e2=(EditText)findViewById(R.id.registerPassword);
        e3=(EditText)findViewById(R.id.registerConPass);
        e4=(EditText)findViewById(R.id.registerEmail);
        e5=(EditText)findViewById(R.id.registerMobile);
        e6=(EditText)findViewById(R.id.registerName);
        e7=(EditText)findViewById(R.id.registerAddress);

        b1=(Button)findViewById(R.id.registerButton);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username=e1.getText().toString();
                String password=e2.getText().toString();
                String conformPassword=e3.getText().toString();
                String email=e4.getText().toString();
                String mobile=e5.getText().toString();
                String name=e6.getText().toString();
                String address=e7.getText().toString();

                if(username==null|| password==null|| conformPassword==null|| email==null|| mobile==null|| name==null|| address==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(mobile.length()!=10)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(conformPassword))
                {
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    User user=new User();

                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.setMobile(mobile);
                    user.setName(name);
                    user.setAddress(address);
                    user.setType("user");

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.USER_DB,user,user.getUsername());

                        Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();

                        s.setusename(username);

                        Intent i=new Intent(getApplicationContext(),AddFamily.class);
                        startActivity(i);

                        db=openOrCreateDatabase(Constants.sqLiteDatabase, MODE_PRIVATE, null);
                        db.execSQL("create table if not exists login(username varchar)");
                        db.execSQL("insert into login values('"+username+"')");

                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"Register Error",Toast.LENGTH_SHORT).show();
                        Log.v("User Registration Ex", ex.toString());
                        ex.printStackTrace();
                    }

                }
            }
        });
    }
}
