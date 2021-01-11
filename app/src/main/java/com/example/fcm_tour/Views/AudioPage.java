package com.example.fcm_tour.Views;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AudioPage extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_audio_page, container, false);
        Preferences.init(getContext());
        String roomNum = Preferences.read("room", null);
        new GetRoomsByNumber().execute(API.API_URL + "/torre/salas/" + roomNum);

        final int audioContainer = R.id.audioPageFrame;
        AudioPlayer audioPlayer = new AudioPlayer();
        openFragment(audioPlayer, audioContainer);
        return v;
    }

    private void openFragment(AudioPlayer audioPlayer, int audioContainer) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(audioContainer, audioPlayer);
        ft.commit();
    }

    class GetRoomsByNumber extends AsyncTask<String, String, String> {
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
                JSONObject rooms = new JSONObject(result);
                String description = rooms.getString("description");
                String img = rooms.getString("cover");
                String link = rooms.getString("audio");
                Preferences.write("audioPlayer", link);
                String name = rooms.getString("name");
                ImageView imgChuck = v.findViewById(R.id.IMG);
                Picasso.get()
                        .load(img)
                        .resize(256, 256)
                        .centerCrop()
                        .into(imgChuck);
                TextView text = (TextView) v.findViewById(R.id.title);
                text.setText(name);
                TextView text2 = (TextView) v.findViewById(R.id.description);
                text2.setText(description);
                /* MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                mediaPlayer.setDataSource(link);
                mediaPlayer.prepare();
                Button play = (Button) v.findViewById(R.id.btnStart);
                Button pause = (Button) v.findViewById(R.id.btnAR);

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { mediaPlayer.start();
                    }
                });

                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.pause();
                    }
                }); */
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}