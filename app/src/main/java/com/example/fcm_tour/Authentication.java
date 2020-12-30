package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Authentication extends AppCompatActivity {
    Intent login;
    Intent register;
    Intent voltar;
    Intent homePage;
    String loginType; // "Normal" / "Google" / "Facebook"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Preferences.init(getApplicationContext());

        Intent x = getIntent();
        if (Intent.ACTION_VIEW.equals(x.getAction())) {
            loginType = Preferences.readLoginType();
            Uri uri = x.getData();
            String result = uri.getQueryParameter("code").toString();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if(loginType.equals("Google")) {
                                new Authentication.GetUserGoogle().execute("https://fcm-tour.herokuapp.com/token/Google/" + result.substring(2));
                            } else if(loginType.equals("Facebook")) {
                                new Authentication.GetUserGoogle().execute("https://fcm-tour.herokuapp.com/token/Facebook/" + result);
                            }
                        }
                    }, 1000);
        }

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

        Button btnVoltar = (Button) findViewById(R.id.voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voltar = new Intent(v.getContext(), MainActivity.class);
                startActivity(voltar);
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

    class GetUserGoogle extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... fileUrl) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(fileUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream in = connection.getInputStream();

                stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (Exception e) {
                Log.e("MY_CUSTOM_ERRORS", "onCreate: " + e);
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonResponse = new JSONArray(result);
                JSONObject jsonObjectcs = jsonResponse.getJSONObject(0);
                String bearer = jsonObjectcs.getString("bearer");

                Log.e("INFO", String.valueOf(bearer));
                Authentication.JWTUtils.decoded(bearer);
                home();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "REQUEST DONE", Toast.LENGTH_LONG).show();
        }
    }

    public static class JWTUtils {
        public static void decoded(String JWTEncoded) throws Exception {
            try {
                String[] split = JWTEncoded.split("\\.");
                JSONObject body = new JSONObject(getJson(split[1]));
                if(Preferences.readLoginType().equals("Google")) {
                    String name = body.getString("name");
                    String email = body.getString("email");
                    Preferences.write("username", name);
                    Preferences.write("userEmail", email);
                } else {
                    String data = body.getString("data");
                    JSONObject body2 = new JSONObject(data);
                    String email = body2.getString("user");
                    Preferences.write("userEmail", email);
                }
            } catch (UnsupportedEncodingException e) {
                //Error
            }
        }
        private static String getJson(String strEncoded) throws UnsupportedEncodingException {
            byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
            return new String(decodedBytes, "UTF-8");
        }
    }

    public void home() {
        homePage = new Intent(this, MainActivity.class);
        startActivity(homePage);
    }

}