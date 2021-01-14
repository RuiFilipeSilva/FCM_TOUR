package com.example.fcm_tour.Views;

import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Library;
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

public class History extends Fragment {
    ImageButton towerBtn;
    ImageButton museumBtn;
    ImageButton libraryBtn;
    ImageButton musicBtn;
    Bundle extras;
    String name, description, img, link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        Preferences.init(getContext());
        new History.GetMuseum().execute(API.API_URL + "/home");
        towerBtn = (ImageButton) v.findViewById(R.id.tower);
        towerBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Tower towerPage = new Tower();
            openTowerFragment(towerPage, homeContainer);
        });
        museumBtn = (ImageButton) v.findViewById(R.id.museum);
        museumBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Museum museumPage = new Museum();
            openMuseumFragment(museumPage, homeContainer);
        });
        if (Preferences.readQrPaint() != null) {
            new History.TicketScan().execute(API.API_URL + "/museu/quadros/" + Preferences.readQrPaint());
        }
        /*libraryBtn = (ImageButton) v.findViewById(R.id.library);
        libraryBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Library libraryPage = new Library();
            openMuseumFragment(libraryPage, homeContainer);
        });
        musicBtn = (ImageButton) v.findViewById(R.id.music);
        musicBtn.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            Music musicPage = new Music();
            openMuseumFragment(musicPage, homeContainer);
        });*/
        return v;
    }

    private void openTowerFragment(Tower towerPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, towerPage);
        ft.commit();
    }

    private void openMuseumFragment(Museum museumPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, museumPage);
        ft.commit();
    }

    /*private void openLibraryFragment(Library libraryPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, libraryPage);
        ft.commit();
    }

    private void openMusicFragment(Music musicPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, musicPage);
        ft.commit();
    }*/

    class GetMuseum extends AsyncTask<String, String, String> {
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
                TextView text = (TextView) v.findViewById(R.id.textview2);
                text.setText(TEMP);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

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
                String state = jsonObject.getString("number");
                name = jsonObject.getString("name");
                description = jsonObject.getString("description");
                img = jsonObject.getString("img");
                link = jsonObject.getString("audio");
                Log.d("SIGA", "onPostExecute: " + name + description);
                extras.putString("title", name);
                extras.putString("description", description);
                extras.putString("img", img);
                extras.putString("link", link);
                extras.putBoolean("paitingQr", true);
                Preferences.saveAudioPageType(1);
                openPaintingPage(extras);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void openPaintingPage(Bundle extras) {
        final int homeContainer = R.id.fullpage;
        AudioPage audioPage = new AudioPage();
        audioPage.setArguments(this.extras);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }
}