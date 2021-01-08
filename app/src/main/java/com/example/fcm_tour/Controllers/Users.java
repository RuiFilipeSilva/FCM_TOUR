package com.example.fcm_tour.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.SideBar;
import com.example.fcm_tour.Views.History;
import com.example.fcm_tour.Views.Login2;
import com.example.fcm_tour.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Users {
    private static String token;

    //VALIDATE USER------------------------------------------------------------------------------------------------
    public static void volleyPost(String name, String email, String password, String confPassword, Context context) {
        if (isEmailValid(email)) {
            if (isPasswordValid(password)) {
                String postUrl = API.API_URL+"/register";
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                JSONObject postData = new JSONObject();
                try {
                    postData.put("username", name);
                    postData.put("email", email);
                    postData.put("password", password);
                    postData.put("confPassword", confPassword);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String message = "";
                        try {
                            message = response.get("res").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent login = new Intent(context, Login2.class);
                        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(login);
                        ToastMaker.sampleToast("! Inicie Sessão");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error, context);
                        error.printStackTrace();
                    }
                });
                requestQueue.add(jsonObjectRequest);
            } else {
                ToastMaker.sampleToast("Password não é válida");
            }
        } else {

        }
    }

    //VALIDATE EMAIL------------------------------------------------------------------------------------------------------
    static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //VALIDATE PASSWORD---------------------------------------------------------------------------------------------------
    static boolean isPasswordValid(String password) {
        Boolean value = false;
        Pattern pattern = Pattern.compile("(?=[A-Za-z0-9@#$%^&+!=]+$)^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{6,}).*$");
        Matcher matcher = pattern.matcher(password);
        Log.d("TESTE", "isPasswordValid: " + matcher.matches());
        if (matcher.matches()) {
            Log.d("CHAR", "isPasswordValid: string " + password + " contains special character");
            value = true;
        } else {
            Log.d("CHAR", "isPasswordValid: string " + password + " does not contains special character");
        }
        Log.d("VALUE", "isPasswordValid: " + value);
        return value;
    }

    //LOGIN  GOOGLE & FACEBOOK-------------------------------------------------------------------------------------------
    public static class authSocialNetworks extends AsyncTask<String, String, String> {
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
                JSONObject jsonObjects = jsonResponse.getJSONObject(0);
                String bearer = jsonObjects.getString("bearer");
                Users.JWTUtils.decoded(bearer);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //LOGIN - NORMAL --------------------------------------------------------------------------------------------------------------
    public static void loginVolley(String email, String password, Context context) {
        String postUrl = API.API_URL+"/login";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

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
                            Preferences.saveUserToken(token);
                            Preferences.saveUserEmail(email);
                            Intent homePage = new Intent(context, SideBar.class);
                            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(homePage);
                            Toast toast = Toast.makeText(context, "Bem vindo", Toast.LENGTH_SHORT);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error, context);
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

    //LOGIN - FACEBOOK --------------------------------------------------------------------------------------------------------------
    public static void facebookLogin(String access_token, String username, String email, Context context) {
        String postUrl = API.API_URL+"/facebook";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject postData = new JSONObject();
        try {
            postData.put("access_token", access_token);
            postData.put("username", username);
            postData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            token = response.get("token").toString();
                            Users.JWTUtils.decoded(token);
                            Preferences.saveUserToken(token);
                            Intent homePage = new Intent(context, SideBar.class);
                            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(homePage);
                            Toast toast = Toast.makeText(context, "Bem vindo Facebook", Toast.LENGTH_SHORT);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error, context);
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

    //LOGIN - GOOGLE --------------------------------------------------------------------------------------------------------------
    public static void googleLogin(String bearer, Context context) {
        String postUrl = API.API_URL+"/login/google";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject postData = new JSONObject();
        try {
            postData.put("token", bearer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            token = response.get("token").toString();
                            Log.d("SIGA", "BEARER: " + token);
                            Users.JWTUtils.decoded(token);
                            Preferences.saveUserToken(token);
                            Intent homePage = new Intent(context, SideBar.class);
                            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(homePage);
                            Toast toast = Toast.makeText(context, "Bem vindo Google", Toast.LENGTH_SHORT);
                            toast.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleError(error, context);
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

    //DECODE BEARER TOKEN------------------------------------------------------------------------------------------------
    public static class JWTUtils {
        public static void decoded(String JWTEncoded) throws Exception {
            try {
                String[] split = JWTEncoded.split("\\.");
                JSONObject body = new JSONObject(getJson(split[1]));
                String data = body.getString("data");
                JSONObject body2 = new JSONObject(data);
                String email = body2.getString("email");
                Preferences.saveUserEmail(email);
                String name = body2.getString("username");
                Preferences.saveUsername(name);
                String picture = body2.getString("picture");
                Preferences.saveUserImg(picture);
            } catch (UnsupportedEncodingException e) {
                //Error
            }
        }

        private static String getJson(String strEncoded) throws UnsupportedEncodingException {
            byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
            return new String(decodedBytes, "UTF-8");
        }
    }

    //ERROS VOLLEY-------------------------------------------------------------------------------------------------------
    public static void handleError(VolleyError error, Context context) {
        String body = null;
        try {
            body = new String(error.networkResponse.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // exception
        }
        Toast toast = Toast.makeText(context, "Erro: " + body, Toast.LENGTH_SHORT);
        toast.show();
    }
}
