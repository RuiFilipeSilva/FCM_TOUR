package com.example.fcm_tour.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;


public class Collections extends Fragment {

    private static String[] numbers, names, imgs;
    private static View actualView;
    String description, link, title, img;
    Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        actualView = inflater.inflate(R.layout.fragment_rooms, container, false);
        extras = new Bundle();
        new Collections.GetCollections().execute(API.API_URL + "/biblioteca/acervos");
        return actualView;
    }

    private void locationSort(JSONArray result) throws JSONException {
        View v = getView();
        ListView listView = v.findViewById(R.id.listCards);
        numbers = new String[result.length()];
        names = new String[result.length()];
        imgs = new String[result.length()];
        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject room = result.getJSONObject(i);
            String img = room.getString("img");
            String name = room.getString("name");
            String number = room.getString("number");
            names[i] = name;
            numbers[i] = number;
            imgs[i] = img;
        }
        Collections.MyAdapter adapter = new Collections.MyAdapter(getContext(), names, numbers, imgs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            for (int i = 0; i < numbers.length; i++) {
                if (position == i) {
                    new Collections.GetCollectionsByNumber().execute(API.API_URL + "/biblioteca/acervos/" + numbers[i]);
                    break;
                }
            }
        });
    }

    static class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rName[];
        String rNumber[];
        String rImg[];

        MyAdapter(Context c, String name[], String number[], String img[]) {
            super(c, R.layout.room, R.id.name, name);
            this.context = c;
            this.rName = name;
            this.rNumber = number;
            this.rImg = img;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.collection, parent, false);
            TextView names = row.findViewById(R.id.authorName);
            ImageView imgs = row.findViewById(R.id.authorImg);
            names.setText(rName[position]);
            Picasso.get()
                    .load(rImg[position])
                    .into(imgs);
            return row;
        }
    }

    class GetCollections extends AsyncTask<String, String, String> {
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
                Toast.makeText(getContext(), "Exception: " + e,Toast.LENGTH_LONG);
            }
            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonResponse = new JSONArray(result);
                locationSort(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class GetCollectionsByNumber extends AsyncTask<String, String, String> {
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
                JSONObject rooms = new JSONObject(result);
                img = rooms.getString("img");
                description = rooms.getString("description");
                link = rooms.getString("audio");
                title = rooms.getString("name");
                extras.putString("img", img);
                extras.putString("title", title);
                extras.putString("description", description);
                extras.putString("link", link);
                Preferences.saveAudioPageType(2);
                final int homeContainer = R.id.fullpage;
                AudioPage audioPage = new AudioPage();
                audioPage.setArguments(extras);
                openFragment(audioPage, homeContainer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFragment(AudioPage audioPage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, audioPage);
        ft.commit();
    }
}