package com.example.fcm_tour.Views;

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
        temporaryBtn1.setOnClickListener(v -> {
            new GetTemporary().execute(API.API_URL + "/museu/temporarias/1");
        });
        temporaryBtn2 = v.findViewById(R.id.temp2);
        temporaryBtn2.setOnClickListener(v -> {
            new GetTemporary().execute(API.API_URL + "/museu/temporarias/2");
        });
        temporaryBtn3 = v.findViewById(R.id.temp3);
        temporaryBtn3.setOnClickListener(v -> {
            new GetTemporary().execute(API.API_URL + "/museu/temporarias/3");
        });
        temporaryBtn4 = v.findViewById(R.id.temp4);
        temporaryBtn4.setOnClickListener(v -> {
            new GetTemporary().execute(API.API_URL + "/museu/temporarias/4");
        });
        return v;
    }

    class GetTemporary extends AsyncTask<String, String, String> {
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
                Preferences.saveAudioPageType(1);
                openTemporaryPage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void openTemporaryPage() {
        final int homeContainer = R.id.fullpage;
        AudioPage audioPage = new AudioPage();
        audioPage.setArguments(extras);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }
}