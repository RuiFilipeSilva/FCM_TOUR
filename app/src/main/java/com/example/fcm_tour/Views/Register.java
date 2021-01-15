package com.example.fcm_tour.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.MainActivity;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;

public class Register extends AppCompatActivity {
    Intent comeBack, login;
    EditText name, email, password, confPassword;
    Button btnComeBack, btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Preferences.init(getApplicationContext());
        final int container = R.id.fragmentAuth;
        SocialMediaAuth socialMediaAuth = new SocialMediaAuth();
        openFragment(socialMediaAuth, container);
        btnComeBack = findViewById(R.id.comeBack);
        btnComeBack.setOnClickListener(v -> {
            comeBack = new Intent(v.getContext(), SideBar.class);
            startActivity(comeBack);
            overridePendingTransition(R.anim.from_bottom, R.anim.to_top);
        });
        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(v -> {
            login = new Intent(v.getContext(), Login2.class);
            startActivity(login);
        });
        btnRegister = findViewById(R.id.register);
        btnRegister.setOnClickListener(v -> {
            name = findViewById(R.id.nametxt);
            email = findViewById(R.id.mail);
            password = findViewById(R.id.passtxt);
            confPassword = findViewById(R.id.confpassTxt);
            login = new Intent(v.getContext(), Login2.class);
            Users.volleyPost(name.getText().toString(), email.getText().toString(), password.getText().toString(), confPassword.getText().toString(), getApplicationContext());
        });
    }

    public void openFragment(SocialMediaAuth socialMediaAuth, int container) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, socialMediaAuth);
        ft.commit();
    }
}
