package com.example.fcm_tour;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.Views.Authentication;
import com.example.fcm_tour.Views.History;
import com.example.fcm_tour.Views.Language;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Preferences.init(getApplicationContext());
        ImageView img = findViewById(R.id.FCM);
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img.startAnimation(aniFade);
        new android.os.Handler().postDelayed(
                () -> loading(), 3000);
    }

    public void loading() {
        if(Preferences.readLanguage() == null){
            Intent intent = new Intent(this, Language.class);
            startActivity(intent);
            finish();
        }
        else{
            if(Preferences.readLanguage().equals("EN")){
                String lang = "en";
                Locale locale = new Locale(lang);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            }
            Intent intent = new Intent(this, SideBar.class);
            startActivity(intent);
            finish();
        }
    }
}