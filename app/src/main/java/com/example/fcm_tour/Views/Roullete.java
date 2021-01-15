package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fcm_tour.API;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class Roullete extends Fragment {
    GifImageView gif;
    Button spining;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_roullete, container, false);

        gif = (GifImageView) v.findViewById(R.id.gify);
        ((GifDrawable) gif.getDrawable()).stop();
        spining = (Button) v.findViewById(R.id.spin);
        spining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GifDrawable) gif.getDrawable()).start();
                new android.os.Handler().postDelayed(
                        () -> loading(), 5000);
            }
        });


        return v;
    }

    public void loading() {
        ((GifDrawable) gif.getDrawable()).stop();
        new GetPoints().execute(API.API_URL + "/roleta/girar");
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
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle(R.string.title_alert_award);
                alertDialog.setMessage(getString(R.string.message_award) + state + getString(R.string.message_award_2));
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
}