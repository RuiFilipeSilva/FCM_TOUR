package com.example.fcm_tour;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
    private static SharedPreferences mSharedPref;

    private Preferences()
    {

    }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void saveloginType(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString("loginType", value);
        prefsEditor.commit();
    }

    public static void saveUserToken(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString("token", value);
        prefsEditor.commit();
    }

    public static String readLoginType() {
        return mSharedPref.getString("loginType", null);
    }

    public static String readUserToken() {
        return mSharedPref.getString("token", null);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }
}