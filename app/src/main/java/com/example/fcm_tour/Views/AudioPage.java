package com.example.fcm_tour.Views;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.facebook.appevents.suggestedevents.ViewOnClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AudioPage extends Fragment {
    View v;
    Bundle extras;
    String title;
    String description;
    String link;
    String getImgs;
    Button btnTxt;
    Button btnAudio;
    ImageSlider imageSlider;
    Boolean roomsAccess;
    Bundle bundle;
    Button nextRoomBtn;
    Button beforeRoomBtn;
    String nextRoomNum;
    String beforeRoomNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
        bundle = this.getArguments();
        extras = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_audio_page, container, false);
        nextRoomBtn = (Button) v.findViewById(R.id.nextBtn);
        nextRoomBtn.setOnClickListener(v -> {
            new GetRoomsByNumber().execute(API.API_URL + "/torre/salas/" + nextRoomNum);
        });
        beforeRoomBtn = (Button) v.findViewById(R.id.beforeBtn);
        beforeRoomBtn.setOnClickListener(v -> {
            new GetRoomsByNumber().execute(API.API_URL + "/torre/salas/" + beforeRoomNum);
        });
        btnTxt = (Button) v.findViewById(R.id.txtBtn);
        btnAudio = (Button) v.findViewById(R.id.audio);
        btnTxt.setOnClickListener(v1 -> openDescFragment());
        btnAudio.setOnClickListener(v12 -> openAudioFragment());
        try {
            loadRoomInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    public void openDescFragment() {
        extras.putString("description", description);
        btnTxt.setClickable(false);
        btnAudio.setClickable(true);
        btnTxt.setBackgroundResource(R.drawable.botao_descricao_amarelo);
        btnAudio.setBackgroundResource(R.drawable.botao_audio_dgrey);
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
        btnTxt.setBackgroundResource(R.drawable.botao_da_descricao_dgray);
        btnAudio.setBackgroundResource(R.drawable.botao_do_audio_amarelo);
        final int audioContainer = R.id.audioPageFrame;
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.setArguments(extras);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(audioContainer, audioPlayer);
        ft.commit();
    }

    public void loadRoomInfo() throws JSONException {
        title = bundle.getString("title");
        TextView text = (TextView) v.findViewById(R.id.title);
        text.setText(title);
        description = bundle.getString("description");
        link = bundle.getString("link");
        getImgs = bundle.getString("imgsList");
        JSONArray imgsResult = new JSONArray(getImgs);
        List<SlideModel> imgsList = new ArrayList<>();
        for (int i = 0; i < imgsResult.length(); i++) {
            imgsList.add(new SlideModel(imgsResult.getString(i)));
        }
        imageSlider = v.findViewById(R.id.slider);
        imageSlider.setImageList(imgsList, true);
        roomsAccess = Preferences.readRoomsAccess();
        verifyRooms(title);
        openDescFragment();
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
            try {
                JSONObject rooms = new JSONObject(result);
                getImgs = rooms.getString("imgs");
                description = rooms.getString("description");
                link = rooms.getString("audio");
                title = rooms.getString("name");
                loadNextRoomInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadNextRoomInfo() throws JSONException {
        TextView text = (TextView) v.findViewById(R.id.title);
        text.setText(title);
        JSONArray imgsResult = new JSONArray(getImgs);
        List<SlideModel> imgsList = new ArrayList<>();
        for (int i = 0; i < imgsResult.length(); i++) {
            imgsList.add(new SlideModel(imgsResult.getString(i)));
        }
        imageSlider = v.findViewById(R.id.slider);
        imageSlider.setImageList(imgsList, true);
        roomsAccess = Preferences.readRoomsAccess();
        verifyRooms(title);
        openDescFragment();
    }

    public void verifyRooms(String titleTxt) {
        if (roomsAccess == true) {
            if (Rooms.getBeforeAfterRooms(titleTxt).get(0) != null) {
                beforeRoomBtn.setVisibility(View.VISIBLE);
                beforeRoomNum = Rooms.getBeforeAfterRooms(titleTxt).get(2);
                beforeRoomBtn.setText(Rooms.getBeforeAfterRooms(titleTxt).get(0));
            } else {
                beforeRoomBtn.setVisibility(View.INVISIBLE);
            }
            if (Rooms.getBeforeAfterRooms(titleTxt).get(1) != null) {
                nextRoomBtn.setVisibility(View.VISIBLE);
                nextRoomNum = Rooms.getBeforeAfterRooms(titleTxt).get(3);
                nextRoomBtn.setText(Rooms.getBeforeAfterRooms(titleTxt).get(1));
            } else {
                nextRoomBtn.setVisibility(View.INVISIBLE);
            }
        }
    }
}