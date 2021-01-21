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
import com.android.volley.toolbox.JsonArrayRequest;
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

public class Music extends Fragment {
    View v;
    ImageButton firstMusicGroup, secondMusicGroup;
    Bundle extras;
    String title, description, img, link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_music, container, false);
        extras = new Bundle();
        Preferences.init(getContext());
        GetMusic(getContext());
        firstMusicGroup = v.findViewById(R.id.firstImg);
        secondMusicGroup = v.findViewById(R.id.secondImg);
        firstMusicGroup.setOnClickListener(v -> GetCiclos(getContext()));
        secondMusicGroup.setOnClickListener(v -> GetCupertinos(getContext()));
        return v;
    }

    public void GetMusic(Context context) {
        String postUrl = API.API_URL + "/musica/";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        loadLayoutPage(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show()) {
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

    public void GetCiclos(Context context) {
        String postUrl = API.API_URL + "/musica/ciclos/";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        openFragment(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show()) {
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

    public void GetCupertinos(Context context) {
        String postUrl = API.API_URL + "/musica/cupertinos/";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        openFragment(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Erro: " + error, Toast.LENGTH_SHORT).show()) {
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

    private void openFragment(JSONObject result) throws JSONException {
        title = result.getString("text");
        description = result.getString("description");
        img = result.getString("img");
        link = result.getString("audio");
        extras.putString("title", title);
        extras.putString("description", description);
        extras.putString("img", img);
        extras.putString("link", link);
        Preferences.saveAudioPageType(3);
        final int homeContainer = R.id.fullpage;
        AudioPage audioPage = new AudioPage();
        audioPage.setArguments(extras);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }

    public void loadLayoutPage(JSONObject result2) throws JSONException {
        JSONObject jsonCupertinos = new JSONObject(result2.getString("cupertinos"));
        JSONObject jsonCiclos = new JSONObject(result2.getString("ciclos"));
        String firstImg = jsonCupertinos.getString("img");
        String secondImg = jsonCiclos.getString("img");
        Picasso.get()
                .load(firstImg)
                .into(firstMusicGroup);
        Picasso.get()
                .load(secondImg)
                .into(secondMusicGroup);
    }
}