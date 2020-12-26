package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login2 extends AppCompatActivity {
Intent voltar;
Intent register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Button btnVoltar = (Button)findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltar = new Intent(v.getContext(), MainActivity.class);
                startActivity(voltar);
            }
        });
        Button btnRegister = (Button)findViewById(R.id.register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register = new Intent(v.getContext(), Register.class);
                startActivity(register);
            }
        });
    }
}