package com.example.fcm_tour;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SampleHTTP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_h_t_t_p);

        new SampleHTTP.GetMuseu().execute("https://fcm-tour.herokuapp.com/museu");


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

                String link = jsonObjetcs.getString("audio");


                ImageView imgChuck = findViewById(R.id.cover);
                Picasso.get()
                        .load(img)
                        .resize(256, 256)
                        .centerCrop()
                        .into(imgChuck);


                TextView text = (TextView) findViewById(R.id.textview);
                text.setText(TEMP);
                Log.d("museu", "onPostExecute: " + link);


                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                mediaPlayer.setDataSource(link);
                mediaPlayer.prepare(); // might take long! (for buffering, etc)


                Button play = (Button)findViewById(R.id.button2);
                Button pause = (Button)findViewById(R.id.button);

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.start();
                    }
                });


                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.stop();
                    }
                });





            }catch (JSONException | IOException e){
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "REQUEST DONE", Toast.LENGTH_LONG).show();
        }


    }
}