package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {
Intent voltar;
Intent login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnVoltar = (Button)findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltar = new Intent(v.getContext(), MainActivity.class);
                startActivity(voltar);
            }
        });
        Button btnLogin = (Button)findViewById(R.id.login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login = new Intent(v.getContext(), Login2.class);
                startActivity(login);
            }
        });
    }
}