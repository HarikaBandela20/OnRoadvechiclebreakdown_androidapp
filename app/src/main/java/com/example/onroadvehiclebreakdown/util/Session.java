package com.example.onroadvehiclebreakdown.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class Session {

    private SharedPreferences prefs;
    Context c;
    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        c=cntx;
    }

    public void setusename(String usename) {
        prefs.edit().putString("usename", usename).commit();
    }

    public String getusename() {
        String usename = prefs.getString("usename","");
        return usename;
    }

    public void setCart(Set<String> cart) {
        prefs.edit().putStringSet("cart", cart).commit();
    }

    public Set<String> getCart() {
        Set<String> cart = prefs.getStringSet("cart",new HashSet<String>());
        return cart;
    }


    public void setRole(String role) {
        prefs.edit().putString("role", role).commit();
    }

    public String getRole() {
        String role = prefs.getString("role","");
        return role;
    }

    public void setViewMap(String viewMap) {
        prefs.edit().putString("viewMap", viewMap).commit();
    }

    public String getViewMap() {
        String viewMap = prefs.getString("viewMap","");
        return viewMap;
    }

    public void loggingOut()
    {
        SharedPreferences p1=c.getSharedPreferences(getusename(),c.MODE_PRIVATE);
        p1.edit().remove(getusename()).commit();
    }
}
