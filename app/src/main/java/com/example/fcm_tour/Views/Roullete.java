package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.example.fcm_tour.Views.SettingsPage.handleError;

public class Roullete extends Fragment {
    GifImageView gif;
    Button spining, award;
    TextView points;
    String date, email, formattedDate, myPoints;
    int point, cupertinos;
    Date c;
    SimpleDateFormat df;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_roullete, container, false);
        email = Preferences.readUserEmail();
        points = v.findViewById(R.id.points);
        points.setText(Preferences.readUserPoints());
        date = Preferences.readUserDate();
        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        formattedDate = df.format(c);
        gif = v.findViewById(R.id.gify);
        ((GifDrawable) gif.getDrawable()).stop();
        spining = v.findViewById(R.id.spin);
        spining.setOnClickListener(v12 -> {
            date = Preferences.readUserDate();
            if (formattedDate.equals(date)) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle(R.string.tilte_alert_error);
                alertDialog.setMessage(getString(R.string.message_alert_error));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            } else {
                ((GifDrawable) gif.getDrawable()).start();
                award.setClickable(false);
                new android.os.Handler().postDelayed(
                        () -> loading(), 7000);
            }
        });
        award = v.findViewById(R.id.awards);
        award.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Awards award = new Awards();
            openFragment(award, homeContainer);
        });
        return v;
    }

    private void openFragment(Awards awards, int homeContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, awards);
        ft.commit();
    }

    public void loading() {
        ((GifDrawable) gif.getDrawable()).stop();
        GetPoints(getContext());
        award.setClickable(true);
        changeDate(email, formattedDate, getContext());
    }

    public void GetPoints(Context context) {
        String postUrl = API.API_URL + "/roleta/girar";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        String state = response.getString("award");
                        point = Integer.parseInt(state);
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle(R.string.title_alert_award);
                        alertDialog.setMessage(getString(R.string.message_award) + " " + state + " " + getString(R.string.message_award_2));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    points.setText(myPoints);
                                });
                        alertDialog.show();
                        cupertinos = Integer.parseInt(Preferences.readUserPoints());
                        cupertinos = cupertinos + point;
                        myPoints = String.valueOf(cupertinos);
                        Preferences.write("userPoints", myPoints);
                        updatePoints(email, myPoints, getContext());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void changeDate(String email, String date, Context context) {
        String postUrl = API.API_URL + "/spin/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, postUrl, postData,
                response -> {
                    try {
                        Preferences.write("userDate", date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void updatePoints(String email, String newPoints, Context context) {
        String postUrl = API.API_URL + "/points/" + email;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject postData = new JSONObject();
        try {
            postData.put("points", newPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, postUrl, postData,
                response -> {
                    try {
                        Preferences.write("userPoints", newPoints);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + Preferences.readUserToken());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void handleError(VolleyError error, Context context) {
        String body = null;
        try {
            body = new String(error.networkResponse.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        Toast toast = Toast.makeText(context, R.string.errorToast + body, Toast.LENGTH_SHORT);
        toast.show();
    }
}