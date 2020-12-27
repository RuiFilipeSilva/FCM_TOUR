package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Register extends AppCompatActivity {
Intent voltar;
Intent login;
EditText name;
EditText email;
EditText password;
EditText confPassword;
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

        Button btnRegistar = (Button)findViewById(R.id.registar);

        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = (EditText) findViewById(R.id.nametxt);
                email = (EditText) findViewById(R.id.mail);
                password = (EditText) findViewById(R.id.passtxt);
                confPassword = (EditText) findViewById(R.id.confpassTxt);
                volleyPost(name.getText().toString(), email.getText().toString() , password.getText().toString(), confPassword.getText().toString());
            }
        });


    }
    public void volleyPost(String name, String email, String password, String confPassword){
        String postUrl = "https://fcm-tour.herokuapp.com/register";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("username", name);
            postData.put("email", email);
            postData.put("password",password);
            postData.put("confPassword",confPassword);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d("RESPONSE", "onResponse: "+ response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if( error.networkResponse.statusCode == 409){
                //   Toast.makeText(getApplicationContext(), error.networkResponse.data.toString(), Toast.LENGTH_LONG).show();
                //}

                //mostra a mensagem de erro
                handleError(error);
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    //------------------------------------------------FUNÇAO DA MENSAGEM DOS ERROS----------
    public void handleError(VolleyError error) {
        String body = null;
        try {
            body = new String(error.networkResponse.data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            // exception
        }
        Log.d("ERROR", "onErrorResponse: " + body);
        Toast.makeText(getApplicationContext(), "Erro: " + body, Toast.LENGTH_LONG).show();
    }
}
