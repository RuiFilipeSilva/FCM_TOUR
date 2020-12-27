package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Login2 extends AppCompatActivity {
    Intent voltar;
    Intent register;
    Intent homePage;
    EditText email;
    EditText password;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Button btnVoltar = (Button) findViewById(R.id.voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltar = new Intent(v.getContext(), MainActivity.class);
                startActivity(voltar);
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

        ImageButton google = (ImageButton) findViewById(R.id.google);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth?access_type=offline&prompt=consent&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&response_type=code&client_id=817455743730-8aptanrqdh06q6aje2jhdp7i30l38mo8.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Ffcm-tour.herokuapp.com%2Flogin"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = (EditText) findViewById(R.id.mail);
                password = (EditText) findViewById(R.id.passwordTxt);
                homePage = new Intent(v.getContext(), MainActivity.class);
                loginVolley(email.getText().toString(), password.getText().toString());
            }
        });
    }

    public void loginVolley(String email, String password) {
        String postUrl = "https://fcm-tour.herokuapp.com/login";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            token = response.get("token").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        homePage.putExtra("token", token);
                        startActivity(homePage);
                        Toast.makeText(getApplicationContext(), "Bem-vindo", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

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
