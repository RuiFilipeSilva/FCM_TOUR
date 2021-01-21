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

public class Music extends Fragment {
    View v;
    ImageButton firstMusicGroup, secondMusicGroup;
    Integer getTypeMethod;
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
        new GetMusicGroup(1).execute(API.API_URL + "/musica/");
        firstMusicGroup = v.findViewById(R.id.firstImg);
        secondMusicGroup = v.findViewById(R.id.secondImg);
        firstMusicGroup.setOnClickListener(v -> {
            new GetMusicGroup(0).execute(API.API_URL + "/musica/cupertinos/");
        });
        secondMusicGroup.setOnClickListener(v -> {
            new GetMusicGroup(0).execute(API.API_URL + "/musica/ciclos/");
        });
        return v;
    }

    class GetMusicGroup extends AsyncTask<String, String, String> {
        public GetMusicGroup(int Selected) {
            getTypeMethod = Selected;
        }

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
                if (getTypeMethod == 0) {
                    JSONObject resultJSON = new JSONObject(result);
                    openFragment(resultJSON);
                } else if (getTypeMethod == 1) {
                    JSONArray resultArray = new JSONArray(result);
                    JSONObject resultJSON = resultArray.getJSONObject(0);
                    loadLayoutPage(resultJSON);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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