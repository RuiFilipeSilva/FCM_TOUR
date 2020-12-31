package com.example.fcm_tour.Views;

import androidx.appcompat.app.AppCompatActivity;

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
        ToastMaker toastMaker = new ToastMaker(getApplicationContext());

        Intent x = getIntent();
        if (Intent.ACTION_VIEW.equals(x.getAction())) {
            loginType = Preferences.readLoginType();
            Uri uri = x.getData();
            String result = uri.getQueryParameter("code").toString();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if (loginType.equals("Google")) {
                                new Users.authSocialNetworks().execute("https://fcm-tour.herokuapp.com/token/Google/" + result.substring(2));
                                home();
                            } else if (loginType.equals("Facebook")) {
                                new Users.authSocialNetworks().execute("https://fcm-tour.herokuapp.com/token/Facebook/" + result);
                                home();
                            }
                        }
                    }, 1000);
        }

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

        ImageButton google = (ImageButton) findViewById(R.id.googleBtn);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.saveloginType("Google");
                Uri uri = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth?access_type=offline&prompt=consent&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&response_type=code&client_id=817455743730-8aptanrqdh06q6aje2jhdp7i30l38mo8.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Ffcm-tour.herokuapp.com%2Flogin"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        ImageButton facebook = (ImageButton) findViewById(R.id.facebookBtn);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.saveloginType("Facebook");
                Uri uri = Uri.parse("https://fcm-tour.herokuapp.com/auth/facebook");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


    public void home() {
        homePage = new Intent(this, Homepage.class);
        startActivity(homePage);
    }
}
