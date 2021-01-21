package com.example.fcm_tour.Views;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
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

public class Permanent extends Fragment {
    View v;
    ImageButton permanentBtn1, permanentBtn2, permanentBtn3, permanentBtn4;
    String name, description, img, link;
    Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_permanent, container, false);
        extras = new Bundle();
        permanentBtn1 = v.findViewById(R.id.perma1);
        permanentBtn1.setOnClickListener(v -> GetPermanent(getContext(), "1"));
        permanentBtn2 = v.findViewById(R.id.perma2);
        permanentBtn2.setOnClickListener(v -> GetPermanent(getContext(), "2"));
        permanentBtn3 = v.findViewById(R.id.perma3);
        permanentBtn3.setOnClickListener(v -> GetPermanent(getContext(), "3"));
        permanentBtn4 = v.findViewById(R.id.perma4);
        permanentBtn4.setOnClickListener(v -> GetPermanent(getContext(), "4"));
        return v;
    }

    public void GetPermanent(Context context, String number) {
        String postUrl = API.API_URL + "/museu/permanente/" + number;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        name = response.getString("name");
                        description = response.getString("description");
                        img = response.getString("img");
                        link = response.getString("audio");
                        extras.putString("title", name);
                        extras.putString("description", description);
                        extras.putString("img", img);
                        extras.putString("link", link);
                        Preferences.saveAudioPageType(1);
                        openPermanentPage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("language", Preferences.readLanguage());
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void openPermanentPage() {
        final int homeContainer = R.id.fullpage;
        AudioPage audioPage = new AudioPage();
        audioPage.setArguments(extras);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }
}