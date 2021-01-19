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

public class Login2 extends AppCompatActivity {
    Intent comeBack, register, homePage;
    EditText email, password;
    Button btnVoltar, btnRegister, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        Preferences.init(getApplicationContext());
        final int container = R.id.fragmentAuth;
        SocialMediaAuth socialMediaAuth = new SocialMediaAuth();
        openFragment(socialMediaAuth, container);
        btnVoltar = findViewById(R.id.voltar);
        btnVoltar.setOnClickListener(v -> {
            comeBack = new Intent(v.getContext(), SideBar.class);
            startActivity(comeBack);
            overridePendingTransition(R.anim.from_bottom, R.anim.to_top);
        });
        btnRegister = findViewById(R.id.register);
        btnRegister.setOnClickListener(v -> {
            register = new Intent(v.getContext(), Register.class);
            startActivity(register);
        });
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            email = findViewById(R.id.mail);
            password = findViewById(R.id.passwordTxt);
            homePage = new Intent(v.getContext(), SideBar.class);
            Users.loginVolley(email.getText().toString(), password.getText().toString(), getApplicationContext());
        });
    }

    public void openFragment(SocialMediaAuth socialMediaAuth, int container) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(container, socialMediaAuth);
        ft.commit();
    }
}
