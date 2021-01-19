package com.example.fcm_tour.Views;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

public class Sculptures extends Fragment {

    private static String[] names, imgs, links;
    private static View actualView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        actualView = inflater.inflate(R.layout.fragment_sculptures, container, false);
        Preferences.removeRoomsAccess();
        new GetSculptures().execute(API.API_URL + "/museu/esculturas");
        return actualView;
    }


    private void locationSort(JSONArray result) throws JSONException {
        View v = getView();
        ListView listView = v.findViewById(R.id.listCardsSculpture);
        names = new String[result.length()];
        imgs = new String[result.length()];
        links = new String[result.length()];
        for (int i = 0; i <= result.length() - 1; i++) {
            JSONObject sculpture = result.getJSONObject(i);
            String img = sculpture.getString("img");
            String name = sculpture.getString("name");
            String link = sculpture.getString("link");
            names[i] = name;
            links[i] = link;
            imgs[i] = img;
        }
        Sculptures.MyAdapter adapter = new Sculptures.MyAdapter(getContext(), names, links, imgs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            for (int i = 0; i < links.length; i++) {
                if (position == i) {
                    Intent intent = new Intent(v.getContext(), AR_Sculptures.class);
                    intent.putExtra("link", links[i]);
                    startActivity(intent);
                }
            }
        });
    }

    static class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rName[];
        String rLink[];
        String rImg[];

        MyAdapter(Context c, String name[], String link[], String img[]) {
            super(c, R.layout.room, R.id.name, name);
            this.context = c;
            this.rName = name;
            this.rLink = link;
            this.rImg = img;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.sculpture, parent, false);
            TextView names = row.findViewById(R.id.sculptureName);
            ImageView imgs = row.findViewById(R.id.sculptureImg);
            names.setText(rName[position]);
            Picasso.get()
                    .load(rImg[position])
                    .into(imgs);
            return row;
        }
    }


    class GetSculptures extends AsyncTask<String, String, String> {
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
                JSONArray jsonResponse = new JSONArray(result);
                locationSort(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}