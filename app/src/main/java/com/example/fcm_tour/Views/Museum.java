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
import java.lang.reflect.Array;
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


        final int tempContainer = R.id.temporary;
        Temporary temporary = new Temporary();
        setFragment(temporary, tempContainer);

        final int permaContainer = R.id.permanet;
        Permanent permanent = new Permanent();
        setFragment2(permanent, permaContainer);

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

    public void setFragment(Temporary temporary, int tempContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(tempContainer, temporary);
        ft.commit();
    }

    public void setFragment2(Permanent permanent, int permaContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(permaContainer, permanent);
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
                String description = jsonObjetcs.getString("description");
                String cover = jsonObjetcs.getString("cover");
                JSONArray temporary = jsonObjetcs.getJSONArray("temporary");
                JSONArray permanent = jsonObjetcs.getJSONArray("permanent");

                String[] temporaryImgs = new String[temporary.length()];
                String[] permanentImgs = new String[permanent.length()];

                for(int i = 0; i < temporary.length(); i++){
                    JSONObject temp = temporary.getJSONObject(i);
                    String img = temp.getString("img");
                    Log.d("SIGA", "onPostExecute: " + img);

                    temporaryImgs[i] = img;
                }

                for(int i = 0; i < permanent.length(); i++){
                    JSONObject perma = permanent.getJSONObject(i);
                    String img = perma.getString("img");
                    Log.d("SIGA", "onPostExecute: " + img);

                    permanentImgs[i] = img;
                }

                ImageView imgChuck = v.findViewById(R.id.cover);
                Picasso.get()
                        .load(cover)
                        .into(imgChuck);
                TextView text = (TextView) v.findViewById(R.id.description);
                text.setText(description);

                ImageButton card1 = (ImageButton) v.findViewById(R.id.card1);
                Picasso.get().load(temporaryImgs[0]).into(card1);

                ImageButton card2 = (ImageButton) v.findViewById(R.id.card2);
                Picasso.get().load(temporaryImgs[1]).into(card2);

                ImageButton card3 = (ImageButton) v.findViewById(R.id.card3);
                Picasso.get().load(temporaryImgs[2]).into(card3);

                ImageButton card4 = (ImageButton) v.findViewById(R.id.card4);
                Picasso.get().load(temporaryImgs[3]).into(card4);

                ImageButton perma1 = (ImageButton) v.findViewById(R.id.perma1);
                Picasso.get().load(permanentImgs[0]).into(perma1);

                ImageButton perma2 = (ImageButton) v.findViewById(R.id.perma2);
                Picasso.get().load(permanentImgs[1]).into(perma2);

                ImageButton perma3 = (ImageButton) v.findViewById(R.id.perma3);
                Picasso.get().load(permanentImgs[2]).into(perma3);

                ImageButton perma4 = (ImageButton) v.findViewById(R.id.perma4);
                Picasso.get().load(permanentImgs[3]).into(perma4);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

}