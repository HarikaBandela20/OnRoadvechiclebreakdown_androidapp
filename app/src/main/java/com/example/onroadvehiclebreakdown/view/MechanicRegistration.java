package com.example.onroadvehiclebreakdown.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onroadvehiclebreakdown.AppConstants;
import com.example.onroadvehiclebreakdown.GpsUtils;
import com.example.onroadvehiclebreakdown.R;
import com.example.onroadvehiclebreakdown.dao.DAO;
import com.example.onroadvehiclebreakdown.form.User;
import com.example.onroadvehiclebreakdown.util.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MechanicRegistration extends AppCompatActivity {

    EditText e1,e2,e3,e4;
    Button b1;

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String lat;
    private String lang;

    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mechanic_registration);

        e1=(EditText)findViewById(R.id.addmechanicname);
        e2=(EditText)findViewById(R.id.addmechanicEmail);
        e3=(EditText)findViewById(R.id.addmechanicMobile);
        e4=(EditText)findViewById(R.id.addmechanicDescription);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        b1=(Button)findViewById(R.id.registermechanic);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLocation();

                if(lat!=null && lang!=null)
                {
                    String name=e1.getText().toString();
                    String email=e2.getText().toString();
                    String mobile=e3.getText().toString();
                    String description=e4.getText().toString();

                    if(name==null|| email==null|| mobile==null|| description==null)
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
                        user.setAddress(lat+","+lang);
                        user.setType("mechanic");

                        DAO dao=new DAO();

                        try
                        {
                            dao.addObject(Constants.USER_DB,user,user.getMobile());

                            Toast.makeText(getApplicationContext(),"Registred Successfully",Toast.LENGTH_SHORT).show();

                            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(i);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(getApplicationContext(),"Register Error",Toast.LENGTH_SHORT).show();
                            Log.v("Registration", ex.toString());
                            ex.printStackTrace();
                        }

                    }
                }
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MechanicRegistration.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MechanicRegistration.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MechanicRegistration.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(MechanicRegistration.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        lat=wayLatitude+"";
                        lang=wayLongitude+"";
                    } else {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(MechanicRegistration.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
