package com.example.fcm_tour.Views;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Temporary extends Fragment {
    View v;
    ImageButton temporaryBtn1, temporaryBtn2, temporaryBtn3, temporaryBtn4;
    Bundle extras;
    String name, description, img, link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_temporary, container, false);
        temporaryBtn1 = v.findViewById(R.id.temp1);
        temporaryBtn1.setOnClickListener(v -> GetTemporary(getContext(), "1"));
        temporaryBtn2 = v.findViewById(R.id.temp2);
        temporaryBtn2.setOnClickListener(v -> GetTemporary(getContext(), "2"));
        temporaryBtn3 = v.findViewById(R.id.temp3);
        temporaryBtn3.setOnClickListener(v -> GetTemporary(getContext(), "3"));
        temporaryBtn4 = v.findViewById(R.id.temp4);
        temporaryBtn4.setOnClickListener(v -> GetTemporary(getContext(), "4"));
        return v;
    }

    public void GetTemporary(Context context, String number) {
        String postUrl = API.API_URL + "/museu/temporarias/" + number;
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
                        openTemporaryPage();
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

    public void openTemporaryPage() {
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