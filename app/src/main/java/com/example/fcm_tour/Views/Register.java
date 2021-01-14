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
    Intent comeBack;
    Intent login;
    EditText name;
    EditText email;
    EditText password;
    EditText confPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Preferences.init(getApplicationContext());
        final int container = R.id.fragmentAuth;
        SocialMediaAuth socialMediaAuth = new SocialMediaAuth();
        openFragment(socialMediaAuth, container);
        Button btnComeBack = (Button) findViewById(R.id.comeBack);
        btnComeBack.setOnClickListener(v -> {
            comeBack = new Intent(v.getContext(), SideBar.class);
            startActivity(comeBack);
        });
        Button btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(v -> {
            login = new Intent(v.getContext(), Login2.class);
            startActivity(login);
        });
        Button btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener((View.OnClickListener) v -> {
            name = (EditText) findViewById(R.id.nametxt);
            email = (EditText) findViewById(R.id.mail);
            password = (EditText) findViewById(R.id.passtxt);
            confPassword = (EditText) findViewById(R.id.confpassTxt);
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
