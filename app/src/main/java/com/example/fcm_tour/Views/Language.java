package com.example.fcm_tour.Views;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;

import java.util.Locale;

public class Language extends AppCompatActivity {
    ImageButton PT, EN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        Preferences.init(getApplicationContext());
        PT = findViewById(R.id.PT);
        PT.setOnClickListener(v -> {
            Preferences.removeLanguage();
            Preferences.saveLanguage("PT");
            String lang = "pt";
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Intent intent = new Intent(this, SideBar.class);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), R.string.welcomeToastPT, Toast.LENGTH_SHORT).show();
        });

        EN = findViewById(R.id.EN);
        EN.setOnClickListener(v -> {
            Preferences.removeLanguage();
            Preferences.saveLanguage("EN");
            String lang = "en";
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Intent intent = new Intent(this, SideBar.class);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), R.string.welcomeToastEN, Toast.LENGTH_SHORT).show();
        });
    }
}