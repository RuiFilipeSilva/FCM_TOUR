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
    Bundle extras;
    String title;
    String description;
    String link;
    String img;
    Button btnTxt;
    Button btnAudio;
    Button btnImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
        String roomNum = Preferences.read("room", null);
        new GetRoomsByNumber().execute(API.API_URL + "/torre/salas/" + roomNum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_audio_page, container, false);
        extras = new Bundle();
        btnTxt = (Button) v.findViewById(R.id.txtBtn);
        btnAudio = (Button) v.findViewById(R.id.audio);
        btnImages = (Button) v.findViewById(R.id.images);

        btnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openDescFragment();
            }
        });
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioFragment();
            }
        });
        btnImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryFragment();
            }
        });
        return v;
    }


    public void openDescFragment() {
        extras.putString("description", description);
        btnTxt.setClickable(false);
        btnAudio.setClickable(true);
        btnImages.setClickable(true);
        btnTxt.setBackgroundResource(R.drawable.rounded__left_btn);
        btnAudio.setBackgroundResource(R.drawable.rounded__left_btn_grey);
        btnImages.setBackgroundResource(R.drawable.rounded__left_btn_grey_ligth);
        final int descriptionContainer = R.id.audioPageFrame;
        Description description = new Description();
        description.setArguments(extras);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(descriptionContainer, description);
        ft.commit();
    }

    public void openAudioFragment() {
        extras.putString("link", link);
        btnTxt.setClickable(true);
        btnAudio.setClickable(false);
        btnImages.setClickable(true);
        btnTxt.setBackgroundResource(R.drawable.rounded__left_btn_grey);
        btnAudio.setBackgroundResource(R.drawable.rounded__left_btn);
        btnImages.setBackgroundResource(R.drawable.rounded__left_btn_grey_ligth);
        final int audioContainer = R.id.audioPageFrame;
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.setArguments(extras);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(audioContainer, audioPlayer);
        ft.commit();
    }

    public void openGalleryFragment() {
        extras.putString("img", img);
        btnTxt.setClickable(true);
        btnAudio.setClickable(true);
        btnImages.setClickable(false);
        btnTxt.setBackgroundResource(R.drawable.rounded__left_btn_grey);
        btnAudio.setBackgroundResource(R.drawable.rounded__left_btn_grey_ligth);
        btnImages.setBackgroundResource(R.drawable.rounded__left_btn);
        final int galleryContainer = R.id.audioPageFrame;
        Gallery gallery = new Gallery();
        gallery.setArguments(extras);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(galleryContainer, gallery);
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
                img = rooms.getString("cover");
                description = rooms.getString("description");
                link = rooms.getString("audio");
                title = rooms.getString("name");
                ImageView imgChuck = v.findViewById(R.id.IMG);
                Picasso.get()
                        .load(img)
                        .resize(256, 256)
                        .centerCrop()
                        .into(imgChuck);
                TextView text = (TextView) v.findViewById(R.id.title);
                text.setText(title);
                openDescFragment();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}