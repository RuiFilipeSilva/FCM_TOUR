package com.example.fcm_tour;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    Intent thisPage;
    String token;
    Button auth;
    Intent towerIntent;
    Intent museumIntent;
    Intent musicIntent;
    Intent libraryIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //BUTTONS
        ImageButton tower = (ImageButton) findViewById(R.id.tower);

        tower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                towerIntent = new Intent(v.getContext(), Tower.class);
                startActivity(towerIntent);
            }
        });

        ImageButton museum = (ImageButton) findViewById(R.id.museum);

        museum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                museumIntent = new Intent(v.getContext(), Museum.class);
                startActivity(museumIntent);
            }
        });

        ImageButton music = (ImageButton) findViewById(R.id.music);

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicIntent = new Intent(v.getContext(), Music.class);
                startActivity(musicIntent);
            }
        });

        ImageButton  library = (ImageButton) findViewById(R.id.library);

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                libraryIntent = new Intent(v.getContext(), Library.class);
                startActivity(libraryIntent);
            }
        });



        //EXTRAS FROM LOGIN RESULT
        thisPage = getIntent();
        Bundle extras = thisPage.getExtras();
        if(extras != null) {
            token = extras.getString("token");
            Toast.makeText(getApplicationContext(), "" + token, Toast.LENGTH_LONG).show();
        }

        auth = (Button)findViewById(R.id.auth);

        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), Authentication.class);
                startActivity(intent);
            }
        });

        new MainActivity.GetMuseu().execute("https://fcm-tour.herokuapp.com/home");

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
            try {
                JSONArray jsonResponse = new JSONArray(result);
                JSONObject jsonObjetcs = jsonResponse.getJSONObject(0);

                String TEMP = jsonObjetcs.getString("description");

                String img = jsonObjetcs.getString("cover");


                ImageView imgChuck = findViewById(R.id.cover);
                Picasso.get()
                        .load(img)
                        .into(imgChuck);


                TextView text = (TextView) findViewById(R.id.textview2);
                text.setText(TEMP);




            }catch (JSONException e){
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "REQUEST DONE", Toast.LENGTH_LONG).show();
        }


    }

}