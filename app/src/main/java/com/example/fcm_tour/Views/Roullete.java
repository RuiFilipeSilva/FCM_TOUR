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
    Button spining;
    Button award;
    TextView points;
    String date, email, formattedDate;
    int point;
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_roullete, container, false);
        email = Preferences.readUserEmail();
        points = v.findViewById(R.id.points);
        points.setText(Preferences.readUserPoints());
        date = Preferences.readUserDate();
        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        formattedDate = df.format(c);
        Log.d("SIGA", "onCreateView: " + formattedDate);
        Log.d("SIGA", "onCreateView: " + date);
        Log.d("SIGA", "onCreateView: " + formattedDate.equals(date));
        gif = (GifImageView) v.findViewById(R.id.gify);
        ((GifDrawable) gif.getDrawable()).stop();
        spining = (Button) v.findViewById(R.id.spin);
        spining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (formattedDate.equals(date)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.title_alert_award);
                    alertDialog.setMessage(getString(R.string.message_award));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> {
                                dialog.dismiss();
                            });
                    alertDialog.show();


                } else {
                    ((GifDrawable) gif.getDrawable()).start();
                    award.setClickable(false);
                    new android.os.Handler().postDelayed(
                            () -> loading(), 7000);

                }
            }
        });

        award = v.findViewById(R.id.awards);
        award.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int homeContainer = R.id.fullpage;
                Awards award = new Awards();
                openFragment(award, homeContainer);
            }
        });


        return v;
    }

    private void openFragment(Awards awards, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, awards);
        ft.commit();
    }

    public void loading() {
        ((GifDrawable) gif.getDrawable()).stop();
        new GetPoints().execute(API.API_URL + "/roleta/girar");
        award.setClickable(true);
        changeDate(email, formattedDate, getContext());
    }

    class GetPoints extends AsyncTask<String, String, String> {
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
                JSONObject jsonResponse = new JSONObject(result);
                String state = jsonResponse.getString("award");
                point = Integer.parseInt(state);
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle(R.string.title_alert_award);
                alertDialog.setMessage(getString(R.string.message_award) + " " + state + "" + getString(R.string.message_award_2));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> {
                            dialog.dismiss();
                        });
                alertDialog.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void changeDate(String email, String date, Context context) {
        String postUrl = API.API_URL + "/spin/" + email;
        Log.d("SIGA", "changeDate: " + email);
        Log.d("SIGA", "changeDate: " + email);
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
                        Log.d("SIGA", "changeDate: " + e);
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