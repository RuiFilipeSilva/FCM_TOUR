package com.example.fcm_tour.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.fcm_tour.Controllers.ToastMaker;
import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.Homepage;
import com.example.fcm_tour.MainActivity;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;

public class Register extends AppCompatActivity {
    Intent voltar;
    Intent login;
    Intent homePage;
    EditText name;
    EditText email;
    EditText password;
    EditText confPassword;
    String loginType; // "Normal" / "Google" / "Facebook"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Preferences.init(getApplicationContext());

        final int container = R.id.fragmentAuth;
        SocialMediaAuth socialMediaAuth = new SocialMediaAuth();
        openFragment(socialMediaAuth, container);

        Button btnVoltar = (Button) findViewById(R.id.voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltar = new Intent(v.getContext(), MainActivity.class);
                startActivity(voltar);
            }
        });

        Button btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = new Intent(v.getContext(), Login2.class);
                startActivity(login);
            }
        });

        Button btnRegistar = (Button) findViewById(R.id.registar);
        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = (EditText) findViewById(R.id.nametxt);
                email = (EditText) findViewById(R.id.mail);
                password = (EditText) findViewById(R.id.passtxt);
                confPassword = (EditText) findViewById(R.id.confpassTxt);
                login = new Intent(v.getContext(), Login2.class);
                Users.volleyPost(name.getText().toString(), email.getText().toString(), password.getText().toString(), confPassword.getText().toString(), getApplicationContext());
            }
        });
    }

    public void openFragment(SocialMediaAuth socialMediaAuth, int container) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, socialMediaAuth);
        ft.commit();
    }
}
