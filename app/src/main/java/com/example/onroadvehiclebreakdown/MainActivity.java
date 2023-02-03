package com.example.onroadvehiclebreakdown;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.onroadvehiclebreakdown.view.MechanicRegistration;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.example.onroadvehiclebreakdown.dao.DAO;
import com.example.onroadvehiclebreakdown.form.Family;
import com.example.onroadvehiclebreakdown.form.User;
import com.example.onroadvehiclebreakdown.util.Constants;
import com.example.onroadvehiclebreakdown.util.Session;
import com.example.onroadvehiclebreakdown.view.ListUsers;
import com.example.onroadvehiclebreakdown.view.LoginActivity;
import com.example.onroadvehiclebreakdown.view.RegisterActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Button b1,b2,b3,b5,b6;
    SQLiteDatabase sqLiteDatabase;
    String userName;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String txtLocation;

    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(getApplicationContext(),"dont have accelerometer sensor",Toast.LENGTH_LONG).show();
        }

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

                        txtLocation=wayLatitude+","+wayLongitude;

                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        b1 = (Button) findViewById(R.id.loginButton);
        b2 = (Button) findViewById(R.id.registerButton);
        b3 = (Button) findViewById(R.id.emergencyalert);
        b5 = (Button) findViewById(R.id.userviewusers);
        b6 = (Button) findViewById(R.id.mechanicregisterButton);

        final Session session=new Session(getApplicationContext());

        sqLiteDatabase=openOrCreateDatabase(Constants.sqLiteDatabase, MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("create table if not exists login(username varchar)");
        Cursor cursor=sqLiteDatabase.rawQuery("select * from login",null);

        if( cursor != null && cursor.moveToFirst() ) {
            userName = cursor.getString(cursor.getColumnIndex("username"));
            cursor.close();
        }
        if(userName!=null && userName!="")
        {
            session.setusename(userName);
            session.setRole("user");

            b1.setEnabled(false);
            b2.setEnabled(false);
            b6.setEnabled(false);
        }
        else {

            b3.setEnabled(false);
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"in function", Toast.LENGTH_SHORT).show();

                if (!isGPS) {
                    Toast.makeText(getApplicationContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                getLocation();

                if(txtLocation!=null)
                {
                    Toast.makeText(getApplicationContext(),"location not null", Toast.LENGTH_SHORT).show();

                    final String[] userLatLongs=txtLocation.split(",");

                    DAO d=new DAO();
                    d.getDBReference(Constants.FAMILY_DB).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                final Family family=snapshotNode.getValue(Family.class);

                                if(family!=null && family.getUserName().equals(userName)) {

                                    final Set<String> senders = new HashSet<>();

                                    senders.add(family.getMobile1());
                                    senders.add(family.getMobile2());
                                    senders.add(family.getMobile3());

                                    Toast.makeText(getApplicationContext(), "Family Added",
                                            Toast.LENGTH_LONG).show();

                                    DAO d=new DAO();
                                    d.getDBReference(Constants.USER_DB).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                                User user=(User)snapshotNode.getValue(User.class);

                                                if(user!=null)
                                                {
                                                    Log.v("user info :",user.toString());

                                                    if(!user.getType().equals("user")) {

                                                        String[] latLongs=user.getAddress().split(",");

                                                        float distance = getDistanceFromCurrentPosition(new Double(userLatLongs[0]), new Double(userLatLongs[1]), new Double(latLongs[0]), new Double(latLongs[1]));

                                                        if(distance<10000)
                                                        {
                                                            senders.add(user.getMobile());
                                                        }
                                                    }
                                                }

                                                Toast.makeText(getApplicationContext(), "Nearest Added",
                                                        Toast.LENGTH_LONG).show();
                                            }

                                            Toast.makeText(getApplicationContext(), "Count"+senders.size(),
                                                    Toast.LENGTH_LONG).show();

                                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                                            ArrayList<PendingIntent> pendingIntents=new ArrayList<PendingIntent>();
                                            pendingIntents.add(pi);

                                            //Get the SmsManager instance and call the sendTextMessage method to send message
                                            SmsManager sms=SmsManager.getDefault();

                                            for(String mobile : senders)
                                            {
                                                Toast.makeText(getApplicationContext(), "in for while sending",
                                                        Toast.LENGTH_LONG).show();

                                                ArrayList<String> parts = sms.divideMessage(userName+" is in Emergency at https://maps.google.com/?q="+userLatLongs[0]+","+userLatLongs[1]);
                                                //smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                                sms.sendMultipartTextMessage(mobile, null, parts,
                                                        pendingIntents, null);

                                                Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Location Null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("going to list users :","");
                Intent i = new Intent(getApplicationContext(),ListUsers.class);
                startActivity(i);
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MechanicRegistration.class);
                startActivity(i);
            }
        });
    }

    public static float getDistanceFromCurrentPosition(double lat1,double lng1, double lat2, double lng2)
    {
        double earthRadius = 3958.75;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        int meterConversion = 1609;

        return new Float(dist * meterConversion).floatValue();

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        txtLocation=wayLatitude+","+wayLongitude;
                    } else {
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

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                txtLocation=wayLatitude+","+wayLongitude;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {

                if (!isGPS) {
                    Toast.makeText(getApplicationContext(), "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }

                getLocation();

                if(txtLocation!=null)
                {
                    final String[] userLatLongs=txtLocation.split(",");

                    DAO d=new DAO();
                    d.getDBReference(Constants.FAMILY_DB).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                final Family family=snapshotNode.getValue(Family.class);

                                if(family!=null && family.getUserName().equals(userName)) {

                                    final Set<String> senders = new HashSet<>();

                                    senders.add(family.getMobile1());
                                    senders.add(family.getMobile2());
                                    senders.add(family.getMobile3());

                                    DAO d=new DAO();
                                    d.getDBReference(Constants.USER_DB).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                                User user=(User)snapshotNode.getValue(User.class);

                                                if(user!=null)
                                                {
                                                    Log.v("user info :",user.toString());

                                                    if(!user.getType().equals("user")) {

                                                        String[] latLongs=user.getAddress().split(",");

                                                        float distance = getDistanceFromCurrentPosition(new Double(userLatLongs[0]), new Double(userLatLongs[1]), new Double(latLongs[0]), new Double(latLongs[1]));

                                                        if(distance<10000)
                                                        {
                                                            senders.add(user.getMobile());
                                                        }
                                                    }
                                                }
                                            }

                                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);

                                            ArrayList<PendingIntent> pendingIntents=new ArrayList<PendingIntent>();
                                            pendingIntents.add(pi);

                                            //Get the SmsManager instance and call the sendTextMessage method to send message
                                            SmsManager sms=SmsManager.getDefault();

                                            for(String mobile : senders)
                                            {
                                                ArrayList<String> parts = sms.divideMessage(userName+" is in Emergency at https://maps.google.com/?q="+userLatLongs[0]+","+userLatLongs[1]);
                                                //smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                                                sms.sendMultipartTextMessage(mobile, null, parts,
                                                        pendingIntents, null);

                                                Toast.makeText(getApplicationContext(), "Message Sent successfully!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                Toast.makeText(getApplicationContext(),"accident occured x: "+x+" \n y:"+y+" \n z:"+z+" \n speed:"+speed,Toast.LENGTH_LONG).show();
            }

            last_x = x;
            last_y = y;
            last_z = z;
        }
    }
}