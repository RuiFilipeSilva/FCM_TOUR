package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;

public class Login2 extends AppCompatActivity {
    Intent voltar;
    Intent register;
    Intent homePage;
    EditText email;
    EditText password;
    String token;
    String loginType; // "Normal" / "Google" / "Facebook"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
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
                                new Login2.GetUserGoogle().execute("https://fcm-tour.herokuapp.com/token/Google/" + result.substring(2));
                            } else if(loginType.equals("Facebook")) {
                                new Login2.GetUserGoogle().execute("https://fcm-tour.herokuapp.com/token/Facebook/" + result);
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
                Preferences.saveloginType("Google");
                Uri uri = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth?access_type=offline&prompt=consent&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile%20https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&response_type=code&client_id=817455743730-8aptanrqdh06q6aje2jhdp7i30l38mo8.apps.googleusercontent.com&redirect_uri=https%3A%2F%2Ffcm-tour.herokuapp.com%2Flogin"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        ImageButton facebook = (ImageButton) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.saveloginType("Facebook");
                Uri uri = Uri.parse("https://fcm-tour.herokuapp.com/auth/facebook");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.saveloginType("Normal");
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
                        Preferences.saveUserToken(token);
                        Preferences.write("userEmail", email);
                        Preferences.write("", email);
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
            body = new String(error.networkResponse.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // exception
        }
        Toast.makeText(getApplicationContext(), "Erro: " + body, Toast.LENGTH_LONG).show();
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
                JWTUtils.decoded(bearer);
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
