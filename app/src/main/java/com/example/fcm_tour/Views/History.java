package com.example.fcm_tour.Views;

import android.app.AlertDialog;
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
import com.android.volley.VolleyError;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class History extends Fragment {
    ImageButton towerBtn, museumBtn, libraryBtn, musicBtn;
    Bundle extras;
    String name, description, img, link;
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_history, container, false);
        Preferences.init(getContext());
        GetMuseum(getContext());
        towerBtn = v.findViewById(R.id.tower);
        towerBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Tower towerPage = new Tower();
            openTowerFragment(towerPage, homeContainer);
        });
        museumBtn = v.findViewById(R.id.museum);
        museumBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Museum museumPage = new Museum();
            openMuseumFragment(museumPage, homeContainer);
        });
        if (Preferences.readQrPaint() != null) {
            new History.TicketScan().execute(API.API_URL + "/museu/quadros/" + Preferences.readQrPaint());
        }
        libraryBtn = v.findViewById(R.id.library);
        libraryBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Library library = new Library();
            openLibraryFragment(library, homeContainer);
        });
        musicBtn = v.findViewById(R.id.music);
        musicBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Music musicPage = new Music();
            openMusicFragment(musicPage, homeContainer);
        });
        return v;
    }

    private void openTowerFragment(Tower towerPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, towerPage);
        ft.commit();
    }

    private void openMuseumFragment(Museum museumPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, museumPage);
        ft.commit();
    }

    private void openLibraryFragment(Library library, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, library);
        ft.commit();
    }

    private void openMusicFragment(Music musicPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, musicPage);
        ft.commit();
    }


    public void GetMuseum(Context context) {
        String postUrl = API.API_URL + "/home";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        String TEMP = response.getString("description");
                        String img = response.getString("cover");
                        ImageView imgChuck = v.findViewById(R.id.coverHistory);
                        Picasso.get().load(img).into(imgChuck);
                        TextView text = v.findViewById(R.id.textview2);
                        text.setText(TEMP);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> handleError(error, context)) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("language", Preferences.readLanguage());
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
    /*class GetMuseum extends AsyncTask<String, String, String> {
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
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            View v = getView();
            try {
                JSONArray jsonResponse = new JSONArray(result);
                JSONObject jsonObjetcs = jsonResponse.getJSONObject(0);
                String TEMP = jsonObjetcs.getString("description");
                String img = jsonObjetcs.getString("cover");
                ImageView imgChuck = v.findViewById(R.id.cover);
                Picasso.get().load(img).into(imgChuck);
                TextView text = v.findViewById(R.id.textview2);
                text.setText(TEMP);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    class TicketScan extends AsyncTask<String, String, String> {
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
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                name = jsonObject.getString("name");
                description = jsonObject.getString("description");
                img = jsonObject.getString("img");
                link = jsonObject.getString("audio");
                extras.putString("title", name);
                extras.putString("description", description);
                extras.putString("img", img);
                extras.putString("link", link);
                extras.putBoolean("paitingQr", true);
                Preferences.saveAudioPageType(1);
                openPaintingPage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void openPaintingPage() {
        final int homeContainer = R.id.fullpage;
        AudioPage audioPage = new AudioPage();
        audioPage.setArguments(this.extras);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }
}