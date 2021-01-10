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
import android.widget.Button;
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

public class Museum extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_museum, container, false);

        new GetMuseu().execute(API.API_URL+"/museu");

        Button sulptures = (Button) v.findViewById(R.id.btnAR);
        sulptures.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int homeContainer = R.id.fullpage;
                SculpturePage sculpturePage = new SculpturePage();
                openFragment(sculpturePage, homeContainer);
            }
        });

        return v;
    }

    private void openFragment(SculpturePage sculpturePage, int homeContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(homeContainer, sculpturePage);
        ft.commit();
    }

    class GetMuseu extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... fileUrl){
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(fileUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) !=null){
                    stringBuilder.append(line);
                }
            }catch (Exception e){
                Log.e("MY_CUSTOM_ERRORS", "onCreate: " + e);
            }
            return stringBuilder.toString();
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            View v = getView();
            try {
                JSONArray jsonResponse = new JSONArray(result);
                JSONObject jsonObjetcs = jsonResponse.getJSONObject(0);
                String TEMP = jsonObjetcs.getString("description");
                String img = jsonObjetcs.getString("cover");
                ImageView imgChuck = v.findViewById(R.id.cover);
                Picasso.get()
                        .load(img)
                        .into(imgChuck);
                TextView text = (TextView) v.findViewById(R.id.description);
                text.setText(TEMP);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}