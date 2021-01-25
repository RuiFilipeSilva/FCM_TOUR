package com.example.fcm_tour.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.fcm_tour.MainActivity;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;

public class Authentication extends AppCompatActivity {
    Intent login, register, comeBack;
    Button btnLogin, btnRegister, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Preferences.init(getApplicationContext());
        final int container = R.id.fragmentAuth;
        SocialMediaAuth socialMediaAuth = new SocialMediaAuth();
        openFragment(socialMediaAuth, container);

        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(v -> {
            login = new Intent(v.getContext(), Login2.class);
            startActivity(login);
        });

        btnRegister = findViewById(R.id.register);
        btnRegister.setOnClickListener(v -> {
            register = new Intent(v.getContext(), Register.class);
            startActivity(register);
        });

        btnBack = findViewById(R.id.voltar);
        if(btnBack.getText().toString().equals("Return V")) {
            btnBack.getLayoutParams().width = 280;
        }
        btnBack.setOnClickListener(v -> {
            comeBack = new Intent(v.getContext(), SideBar.class);
            startActivity(comeBack);
            overridePendingTransition(R.anim.from_bottom, R.anim.to_top);
        });
    }

    public void openFragment(SocialMediaAuth socialMediaAuth, int container) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, socialMediaAuth);
        ft.commit();
    }
}