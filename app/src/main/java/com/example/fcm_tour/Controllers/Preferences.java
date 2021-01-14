package com.example.fcm_tour.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static SharedPreferences mSharedPref;

    private Preferences() {

    }

    public static void init(Context context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        }
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void Logout() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public static void saveUserToken(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("token");
        prefsEditor.putString("token", value);
        prefsEditor.commit();
    }

    public static void saveUsername(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("username");
        prefsEditor.putString("username", value);
        prefsEditor.commit();
    }

    public static void saveUserEmail(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("userEmail");
        prefsEditor.putString("userEmail", value);
        prefsEditor.commit();
    }

    public static void saveUserImg(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("userImg");
        prefsEditor.putString("userImg", value);
        prefsEditor.commit();
    }

    public static void saveRoomsAccess() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("roomsAccess");
        prefsEditor.putBoolean("roomsAccess", true);
        prefsEditor.commit();
    }

    public static void saveAudioPageType(int value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("pageType");
        prefsEditor.putInt("pageType", value);
        prefsEditor.commit();
    }

    public static void removeRoomsAccess() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("roomsAccess");
        prefsEditor.commit();
    }

    public static void removeRoom() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("rooms");
        prefsEditor.commit();
    }
    public static String readUserToken() {
        return mSharedPref.getString("token", null);
    }

    public static String readUsername() {
        return mSharedPref.getString("username", null);
    }

    public static String readUserEmail() {
        return mSharedPref.getString("userEmail", null);
    }

    public static String readUserImg() {
        return mSharedPref.getString("userImg", null);
    }

    public static Integer readPageType() {
        return mSharedPref.getInt("pageType", 0);
    }

    public static Boolean readRoomsAccess() {
        return mSharedPref.getBoolean("roomsAccess", false);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }
}