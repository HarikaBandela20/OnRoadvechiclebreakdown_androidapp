package com.example.onroadvehiclebreakdown.dao;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.onroadvehiclebreakdown.form.User;
import com.example.onroadvehiclebreakdown.util.Constants;
import com.example.onroadvehiclebreakdown.util.MapUtil;
import com.example.onroadvehiclebreakdown.util.Session;

public class DAO
{

        public static DatabaseReference getDBReference(String dbName)
        {
            return GetFireBaseConnection.getConnection(dbName);
        }

        public static String getUnicKey(String dbName)
        {
            return getDBReference(dbName).push().getKey();
        }

        public int addObject(String dbName,Object obj,String key) {

            int result=0;

            try {

                getDBReference(dbName).child(key).setValue(obj);

                result=1;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return result;
        }


        public void setDataToAdapterList(final View view, final Class c, final String dbname, final String type) {

            Log.v("in list populated ","in list populated ");

            final Map<String,Object> map=new HashMap<String,Object>();
            final Map<String,String> viewMap=new HashMap<String,String>();

            getDBReference(dbname).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                        Log.v("in data found ","in data found ");

                        String id=snapshotNode.getKey();

                        Object object=snapshotNode.getValue(c);

                        if(dbname.equals(Constants.USER_DB)) {

                            User user = (User) object;

                            if(type.equals("all"))
                            {
                                if(!user.getType().equals("user"))
                                {
                                    viewMap.put(user.getName(),user.getMobile());
                                }
                            }
                            else
                            {
                                if(user.getType().equals(type))
                                {
                                    viewMap.put(user.getName(),user.getMobile());
                                }
                            }

                        }
                    }

                    ArrayList<String> al=new ArrayList<String>(viewMap.keySet());

                    if(view instanceof ListView) {

                        Log.v("in list view setting ",al.toString());

                        final ListView myView=(ListView)view;

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(myView.getContext(),
                                android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                        myView.setAdapter(adapter);
                    }

                    Session s=new Session(view.getContext());
                    s.setViewMap(MapUtil.mapToString(viewMap));

                    Log.v("after session setting ",al.toString());
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }

        public int deleteObject(String dbName, String key) {

            int result=0;

            try {

                getDBReference(dbName).child(key).removeValue();

                result=1;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return 0;
        }
    }


