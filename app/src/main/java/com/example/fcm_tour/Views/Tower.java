package com.example.fcm_tour.Views;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.R;
import com.example.fcm_tour.SideBar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Tower extends Fragment {
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_tower, container, false);
        GetTower(getContext());
        Button tower = v.findViewById(R.id.btnStart);
        tower.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            RoomPage rooms = new RoomPage();
            openFragment(rooms, homeContainer);
        });
        return v;
    }

    private void openFragment(RoomPage rooms, int homeContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, rooms);
        ft.commit();
    }

    public void GetTower(Context context) {
        String postUrl = API.API_URL + "/torre";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        String desc = response.getString("description");
                        String img = response.getString("cover");
                        ImageView imgChuck = v.findViewById(R.id.cover);
                        Picasso.get()
                                .load(img)
                                .into(imgChuck);
                        TextView text = v.findViewById(R.id.description);
                        text.setText(desc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro" + error, Toast.LENGTH_LONG).show()) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("language", Preferences.readLanguage());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}