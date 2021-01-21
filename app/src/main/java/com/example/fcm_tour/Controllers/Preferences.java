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

    public static void saveUserType(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("userType");
        prefsEditor.putString("userType", value);
        prefsEditor.commit();
    }

    public static void saveUserPoints(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("userPoints");
        prefsEditor.putString("userPoints", value);
        prefsEditor.commit();
    }

    public static void saveUserDate(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("userDate");
        prefsEditor.putString("userDate", value);
        prefsEditor.commit();
    }

    public static void saveRoomsAccess(String code) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("roomsAccess");
        prefsEditor.remove("roomsAccessCode");
        prefsEditor.putBoolean("roomsAccess", true);
        prefsEditor.putString("roomsAccessCode", code);
        prefsEditor.commit();
    }

    public static void saveAudioPageType(int value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("pageType");
        prefsEditor.putInt("pageType", value);
        prefsEditor.commit();
    }

    public static void saveQuizzState() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("quizzState");
        prefsEditor.putBoolean("quizzState", true);
        prefsEditor.commit();
    }

    public static void saveLanguage(String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("language");
        prefsEditor.putString("language", value);
        prefsEditor.commit();
    }

    public static void removeLanguage() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("language");
        prefsEditor.commit();
    }

    public static void removeQuizzState() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("quizzState");
        prefsEditor.commit();
    }

    public static void removeRoomsAccess() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("roomsAccess");
        prefsEditor.remove("roomsAccessCode");
        prefsEditor.commit();
    }

    public static void removeRoom() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("rooms");
        prefsEditor.commit();
    }

    public static void removeQR() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove("qrPaint");
        prefsEditor.commit();
    }

    public static String readUserToken() {
        return mSharedPref.getString("token", null);
    }

    public static String readQrPaint() {
        return mSharedPref.getString("qrPaint", null);
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

    public static String readUserType() {
        return mSharedPref.getString("userType", null);
    }

    public static String readUserPoints() {
        return mSharedPref.getString("userPoints", null);
    }

    public static String readUserDate() {
        return mSharedPref.getString("userDate", null);
    }

    public static String readLanguage() {
        return mSharedPref.getString("language", null);
    }

    public static Integer readPageType() {
        return mSharedPref.getInt("pageType", 0);
    }

    public static Boolean readRoomsAccess() {
        return mSharedPref.getBoolean("roomsAccess", false);
    }

    public static Boolean readQuizzState() {
        return mSharedPref.getBoolean("quizzState", false);
    }

    public static String readRoomsAccessCode() {
        return mSharedPref.getString("roomsAccessCode", null);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }
}