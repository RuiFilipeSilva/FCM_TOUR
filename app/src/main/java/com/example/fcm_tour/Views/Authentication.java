package com.example.fcm_tour.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.Homepage;
import com.example.fcm_tour.MainActivity;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Authentication extends AppCompatActivity {
    Intent login;
    Intent register;
    Intent voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Preferences.init(getApplicationContext());

        final int container = R.id.fragmentAuth;
        SocialMediaAuth socialMediaAuth = new SocialMediaAuth();
        openFragment(socialMediaAuth, container);

        Button btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = new Intent(v.getContext(), Login2.class);
                startActivity(login);
            }
        });

        Button btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register = new Intent(v.getContext(), Register.class);
                startActivity(register);
            }
        });

        Button btnBack = (Button) findViewById(R.id.voltar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltar = new Intent(v.getContext(), MainActivity.class);
                startActivity(voltar);
            }
        });
    }

    public void openFragment(SocialMediaAuth socialMediaAuth, int container) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, socialMediaAuth);
        ft.commit();
    }
}