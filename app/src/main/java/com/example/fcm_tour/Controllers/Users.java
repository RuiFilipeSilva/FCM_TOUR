package com.example.fcm_tour.Controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.MainActivity;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;
import com.example.fcm_tour.Views.Language;
import com.example.fcm_tour.Views.Login2;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Views.ChangePwPage;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Users {
    private static String token;

    public static void volleyPost(String name, String email, String password, String confPassword, Context context) {
        if (isEmailValid(email)) {
            if (isPasswordValid(password)) {
                String postUrl = API.API_URL + "/register";
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
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, response -> {
                    Intent login = new Intent(context, Login2.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(login);
                    Toast.makeText(context, R.string.successRegisterMsg, Toast.LENGTH_SHORT).show();
                }, error -> {
                    handleError(error, context);
                    error.printStackTrace();
                });
                requestQueue.add(jsonObjectRequest);
            } else {
                Toast.makeText(context, R.string.passwordErrorMsg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.emailErrorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(String password) {
        Boolean value = false;
        Pattern pattern = Pattern.compile("(?=[A-Za-z0-9@#$%^&+!=]+$)^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{6,}).*$");
        Matcher matcher = pattern.matcher(password);
        if (matcher.matches()) {
            value = true;
        }
        return value;
    }

    public static void loginVolley(String email, String password, Context context) {
        String postUrl = API.API_URL + "/login";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                response -> {
                    try {
                        token = response.get("token").toString();
                        JWTUtils.decoded(token);
                        Preferences.saveUserToken(token);
                        if (Preferences.readLanguage() == null) {
                            Intent languague = new Intent(context, Language.class);
                            languague.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(languague);
                        } else {
                            Intent homePage = new Intent(context, SideBar.class);
                            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(homePage);
                            if (Preferences.readLanguage().equals("EN")) {
                                Toast.makeText(context, R.string.welcomeToastEN, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.welcomeToastPT, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void facebookLogin(String access_token, String username, String email, Context context) {
        String postUrl = API.API_URL + "/facebook";
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
                response -> {
                    try {
                        token = response.get("token").toString();
                        JWTUtils.decoded(token);
                        Preferences.saveUserToken(token);
                        if (Preferences.readLanguage() == null) {
                            Intent languague = new Intent(context, Language.class);
                            languague.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(languague);
                        } else {
                            Intent homePage = new Intent(context, SideBar.class);
                            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(homePage);
                            if (Preferences.readLanguage().equals("EN")) {
                                Toast.makeText(context, R.string.welcomeToastEN, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.welcomeToastPT, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void googleLogin(String bearer, Context context) {
        String postUrl = API.API_URL + "/login/google";
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject postData = new JSONObject();
        try {
            postData.put("token", bearer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData,
                response -> {
                    try {
                        token = response.get("token").toString();
                        JWTUtils.decoded(token);
                        Preferences.saveUserToken(token);
                        if (Preferences.readLanguage() == null) {
                            Intent languague = new Intent(context, Language.class);
                            languague.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(languague);
                        } else {
                            Intent homePage = new Intent(context, SideBar.class);
                            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(homePage);
                            if (Preferences.readLanguage().equals("EN")) {
                                Toast.makeText(context, R.string.welcomeToastEN, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.welcomeToastPT, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

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
                String type = body2.getString("type");
                Preferences.saveUserType(type);
                String points = body2.getString("points");
                Preferences.saveUserPoints(points);
                String date = body2.getString("date");
                Preferences.saveUserDate(date);
            } catch (UnsupportedEncodingException e) {
                //Error
            }
        }

        private static String getJson(String strEncoded) throws UnsupportedEncodingException {
            byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
            return new String(decodedBytes, "UTF-8");
        }
    }

    public static void Logout() {
        if (Preferences.readLanguage().equals("EN")) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.logoutToastEN), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.logoutToastPT), Toast.LENGTH_SHORT).show();
        }
        Preferences.Logout();
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        } else {
            LoginManager.getInstance().logOut();
        }
    }

    public static void handleError(VolleyError error, Context context) {
        String body = null;
        try {
            body = new String(error.networkResponse.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        Toast toast = Toast.makeText(context, getApplicationContext().getString(R.string.errorToast) + body, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void changePw(String email, String oldPassword, String newPassword, Context context) {
        String postUrl = API.API_URL + "/pass/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("oldPassword", oldPassword);
            postData.put("newPassword", newPassword);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, postUrl, postData,
                response -> {
                    ChangePwPage.pwChangedSuccess();
                    Toast.makeText(context, R.string.changePwToast, Toast.LENGTH_SHORT).show();
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void addPw(String email, String newPassword, Context context) {
        String postUrl = API.API_URL + "/addPass/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, postUrl, postData,
                response -> {
                    try {
                        String type = response.getString("user");
                        Preferences.saveUserType(type);
                        ChangePwPage.pwChangedSuccess();
                        Toast.makeText(context, R.string.addPwToast, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
