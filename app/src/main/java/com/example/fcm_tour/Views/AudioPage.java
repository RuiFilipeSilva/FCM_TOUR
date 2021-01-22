package com.example.fcm_tour.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AudioPage extends Fragment {
    View v;
    String title, description, link, getImgs, nextRoomNum, beforeRoomNum;
    Button btnTxt, btnAudio, goBackBtn, startQuizzBtn;
    LinearLayout nextRoomBtn, previousRoomBtn;
    RelativeLayout navigateRoomsLayout;
    ImageSlider imageSlider;
    Boolean roomsAccess;
    Integer pageType;
    Bundle extras, bundle;
    ImageView imgView, underline;
    TextView nextBtnTxt;

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
        Preferences.removeQR();
        navigateRoomsLayout = v.findViewById(R.id.navigateRoomsLayout);
        nextBtnTxt = v.findViewById(R.id.nextBtnTxt);
        goBackBtn = v.findViewById(R.id.goBackBtn);
        goBackBtn.setOnClickListener(v -> {
            AudioPlayer.stopAudio();
            goBackFragment();
        });
        nextRoomBtn = v.findViewById(R.id.nextBtn);
        nextRoomBtn.setOnClickListener(v -> {
            GetRoomsByNumber(nextRoomNum);
        });
        previousRoomBtn = v.findViewById(R.id.beforeBtn);
        previousRoomBtn.setOnClickListener(v -> {
            GetRoomsByNumber(beforeRoomNum);
        });
        startQuizzBtn = v.findViewById(R.id.startQuizzBtn);
        startQuizzBtn.setOnClickListener(v -> {
            AudioPlayer.stopAudio();
            openQuizzFragment();
        });
        btnTxt = v.findViewById(R.id.txtBtn);
        btnAudio = v.findViewById(R.id.audio);
        btnTxt.setOnClickListener(v1 -> openDescFragment());
        btnAudio.setOnClickListener(v12 -> openAudioFragment());
        pageType = Preferences.readPageType();
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
        switch (pageType) {
            case 0:
                btnTxt.setBackgroundResource(R.drawable.botao_descricao_amarelo);
                btnAudio.setBackgroundResource(R.drawable.botao_audio_dgrey);
                break;
            case 1:
                btnTxt.setBackgroundResource(R.drawable.botao_descricao_museu);
                btnAudio.setBackgroundResource(R.drawable.botao_audio_dgrey);
                break;
            case 2:
                btnTxt.setBackgroundResource(R.drawable.bot_o_descri__o_biblioteca);
                btnAudio.setBackgroundResource(R.drawable.botao_audio_dgrey);
                break;
            case 3:
                btnTxt.setBackgroundResource(R.drawable.bot_o_descri__o_musica);
                btnAudio.setBackgroundResource(R.drawable.botao_audio_dgrey);
                break;
        }
        final int descriptionContainer = R.id.audioPageFrame;
        Description description = new Description();
        description.setArguments(extras);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(descriptionContainer, description);
        ft.commit();
    }

    public void openAudioFragment() {
        extras.putString("link", link);
        btnTxt.setClickable(true);
        btnAudio.setClickable(false);
        switch (pageType) {
            case 0:
                btnTxt.setBackgroundResource(R.drawable.botao_da_descricao_dgray);
                btnAudio.setBackgroundResource(R.drawable.botao_do_audio_amarelo);
                break;
            case 1:
                btnTxt.setBackgroundResource(R.drawable.botao_da_descricao_dgray);
                btnAudio.setBackgroundResource(R.drawable.botao_audio_museu);
                break;
            case 2:
                btnTxt.setBackgroundResource(R.drawable.botao_da_descricao_dgray);
                btnAudio.setBackgroundResource(R.drawable.bot_o__udio_biblioteca);
                break;
            case 3:
                btnTxt.setBackgroundResource(R.drawable.botao_da_descricao_dgray);
                btnAudio.setBackgroundResource(R.drawable.bot_o__udio_musica);
                break;
        }
        final int audioContainer = R.id.audioPageFrame;
        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.setArguments(extras);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(audioContainer, audioPlayer);
        ft.commit();
    }

    public void openQuizzFragment() {
        final int quizzContainer = R.id.fullpage;
        QuizzPage quizzPage = new QuizzPage();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(quizzContainer, quizzPage);
        ft.commit();
    }

    @SuppressLint("ResourceAsColor")
    public void loadRoomInfo() throws JSONException {
        title = bundle.getString("title");
        TextView text = (TextView) v.findViewById(R.id.title);
        text.setText(title);
        description = bundle.getString("description");
        link = bundle.getString("link");
        getImgs = bundle.getString("imgsList");
        imgView = v.findViewById(R.id.imgView);
        underline = v.findViewById(R.id.underline);
        imageSlider = v.findViewById(R.id.slider);
        switch (pageType) {
            case 0:
                JSONArray imgsResult = new JSONArray(getImgs);
                List<SlideModel> imgsList = new ArrayList<>();
                for (int i = 0; i < imgsResult.length(); i++) {
                    imgsList.add(new SlideModel(imgsResult.getString(i)));
                }
                imgView.setVisibility(View.INVISIBLE);
                imageSlider.setVisibility(View.VISIBLE);
                imageSlider.setImageList(imgsList, true);
                goBackBtn.setVisibility(View.VISIBLE);
                goBackBtn.setText(R.string.goBackRooms);
                break;
            case 1:
                goBackBtn.setVisibility(View.VISIBLE);
                goBackBtn.setText(R.string.gobackPaintings);
                underline.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.museum)));
                imgView.setVisibility(View.VISIBLE);
                imageSlider.setVisibility(View.INVISIBLE);
                getImgs = bundle.getString("img");
                Picasso.get().load(getImgs).into(imgView);
                break;
            case 2:
                goBackBtn.setVisibility(View.VISIBLE);
                goBackBtn.setText(R.string.goBackCollections);
                underline.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.library)));
                imgView.setVisibility(View.VISIBLE);
                imageSlider.setVisibility(View.INVISIBLE);
                getImgs = bundle.getString("img");
                Picasso.get().load(getImgs).into(imgView);
                break;
            case 3:
                goBackBtn.setVisibility(View.VISIBLE);
                goBackBtn.setText(R.string.goBackMusic);
                underline.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.music)));
                imgView.setVisibility(View.VISIBLE);
                imageSlider.setVisibility(View.INVISIBLE);
                getImgs = bundle.getString("img");
                Picasso.get().load(getImgs).into(imgView);
                break;
            default:
                break;
        }
        roomsAccess = Preferences.readRoomsAccess();
        verifyRooms(title);
        openDescFragment();
    }

    public void GetRoomsByNumber(String number) {
        String postUrl = API.API_URL + "/torre/salas/" + number;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        getImgs = response.getString("imgs");
                        description = response.getString("description");
                        link = response.getString("audio");
                        title = response.getString("name");
                        if (title.equals("Início") && Preferences.readLanguage().equals("EN")) {
                            title = "Beginning";
                        }
                        loadNextRoomInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro" + error, Toast.LENGTH_LONG).show()) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("language", Preferences.readLanguage());
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void loadNextRoomInfo() throws JSONException {
        TextView text = v.findViewById(R.id.title);
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

    @SuppressLint("ResourceType")
    public void verifyRooms(String titleTxt) {
        if (roomsAccess == true && pageType == 0) {
            navigateRoomsLayout.setVisibility(View.VISIBLE);
            if (Rooms.getBeforeAfterRooms(titleTxt).get(0) != null) {
                previousRoomBtn.setClickable(true);
                beforeRoomNum = Rooms.getBeforeAfterRooms(titleTxt).get(2);
                previousRoomBtn.getBackground().setColorFilter(Color.parseColor(getString(R.color.tower)), PorterDuff.Mode.SRC_ATOP);
            } else {
                previousRoomBtn.setClickable(false);
                previousRoomBtn.getBackground().setColorFilter(Color.parseColor(getString(R.color.grey_light)), PorterDuff.Mode.SRC_ATOP);
            }
            if (Rooms.getBeforeAfterRooms(titleTxt).get(1) != null) {
                nextRoomBtn.setClickable(true);
                nextBtnTxt.setText(R.string.nextBtn);
                nextRoomNum = Rooms.getBeforeAfterRooms(titleTxt).get(3);
                nextRoomBtn.getBackground().setColorFilter(Color.parseColor(getString(R.color.tower)), PorterDuff.Mode.SRC_ATOP);
                startQuizzBtn.setVisibility(View.GONE);
            } else {
                nextBtnTxt.setText("Quizz");
                nextRoomBtn.setClickable(false);
                nextRoomBtn.getBackground().setColorFilter(Color.parseColor(getString(R.color.grey_light)), PorterDuff.Mode.SRC_ATOP);
                startQuizzBtn.setVisibility(View.VISIBLE);
            }
        } else {
            navigateRoomsLayout.setVisibility(View.GONE);
        }
    }

    public void goBackFragment() {
        FragmentManager fragmentManager;
        FragmentTransaction ft;
        switch (pageType) {
            case 0:
                if (roomsAccess == true) {
                    Preferences.saveRoomsAccess(Preferences.readRoomsAccessCode());
                }
                final int roomContainer = R.id.fullpage;
                RoomPage rooms = new RoomPage();
                rooms.setArguments(extras);
                fragmentManager = getParentFragmentManager();
                ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
                ft.addToBackStack(null);
                ft.replace(roomContainer, rooms);
                ft.commit();
                break;
            case 1:
                final int museumContainer = R.id.fullpage;
                Museum museum = new Museum();
                museum.setArguments(extras);
                fragmentManager = getParentFragmentManager();
                ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
                ft.addToBackStack(null);
                ft.replace(museumContainer, museum);
                ft.commit();
                break;
            case 2:
                final int collectionsContainer = R.id.fullpage;
                CollectionsPage collectionsPage = new CollectionsPage();
                collectionsPage.setArguments(extras);
                fragmentManager = getParentFragmentManager();
                ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
                ft.addToBackStack(null);
                ft.replace(collectionsContainer, collectionsPage);
                ft.commit();
                break;
            case 3:
                final int musicContainer = R.id.fullpage;
                Music music = new Music();
                music.setArguments(extras);
                fragmentManager = getParentFragmentManager();
                ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.from_right, R.anim.to_left);
                ft.addToBackStack(null);
                ft.replace(musicContainer, music);
                ft.commit();
                break;
            default:
                break;
        }
    }
}