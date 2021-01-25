package com.example.fcm_tour.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fcm_tour.API;
import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Museum extends Fragment {
    View v;
    Button sculptures;
    ImageButton qrCodeBtn;
    Intent qrCodeIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_museum, container, false);
        displayLayoutMuseum();
        qrCodeBtn = v.findViewById(R.id.qrCode);
        qrCodeBtn.setOnClickListener(v -> {
            qrCodeIntent = new Intent(getContext(), PaintingQrCodes.class);
            startActivity(qrCodeIntent);
        });
        sculptures = v.findViewById(R.id.btnAR);
        sculptures.setOnClickListener(v1 -> {
            final int homeContainer = R.id.fullpage;
            SculpturePage sculpturePage = new SculpturePage();
            openFragment(sculpturePage, homeContainer);
        });
        return v;
    }

    public void displayLayoutMuseum() {
        final int tempContainer = R.id.temporary;
        Temporary temporary = new Temporary();
        setTempFragment(temporary, tempContainer);
        final int permaContainer = R.id.permanet;
        Permanent permanent = new Permanent();
        setPermFragment(permanent, permaContainer);
        GetMuseum(getContext());
    }

    private void openFragment(SculpturePage sculpturePage, int homeContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.from_left, R.anim.to_right);
        ft.addToBackStack(null);
        ft.replace(homeContainer, sculpturePage);
        ft.commit();
    }

    public void setTempFragment(Temporary temporary, int tempContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(tempContainer, temporary);
        ft.commit();
    }

    public void setPermFragment(Permanent permanent, int permaContainer) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(permaContainer, permanent);
        ft.commit();
    }

    public void GetMuseum(Context context) {
        String postUrl = API.API_URL + "/museu";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, postUrl, null,
                response -> {
                    try {
                        String description = response.getString("description");
                        String cover = response.getString("cover");
                        JSONArray temporary = response.getJSONArray("temporary");
                        JSONArray permanent = response.getJSONArray("permanent");
                        String[] temporaryImgs = new String[temporary.length()];
                        String[] permanentImgs = new String[permanent.length()];
                        for (int i = 0; i < temporary.length(); i++) {
                            JSONObject temp = temporary.getJSONObject(i);
                            String img = temp.getString("img");
                            temporaryImgs[i] = img;
                        }
                        for (int i = 0; i < permanent.length(); i++) {
                            JSONObject perma = permanent.getJSONObject(i);
                            String img = perma.getString("img");
                            permanentImgs[i] = img;
                        }
                        ImageView imgChuck = v.findViewById(R.id.cover);
                        Picasso.get()
                                .load(cover)
                                .into(imgChuck);
                        TextView text = v.findViewById(R.id.description);
                        text.setText(description);

                        ImageButton card1 = v.findViewById(R.id.temp1);
                        Picasso.get().load(temporaryImgs[0]).into(card1);

                        ImageButton card2 = v.findViewById(R.id.temp2);
                        Picasso.get().load(temporaryImgs[1]).into(card2);

                        ImageButton card3 = v.findViewById(R.id.temp3);
                        Picasso.get().load(temporaryImgs[2]).into(card3);

                        ImageButton card4 = v.findViewById(R.id.temp4);
                        Picasso.get().load(temporaryImgs[3]).into(card4);

                        ImageButton perma1 = v.findViewById(R.id.perma1);
                        Picasso.get().load(permanentImgs[0]).into(perma1);

                        ImageButton perma2 = v.findViewById(R.id.perma2);
                        Picasso.get().load(permanentImgs[1]).into(perma2);

                        ImageButton perma3 = v.findViewById(R.id.perma3);
                        Picasso.get().load(permanentImgs[2]).into(perma3);

                        ImageButton perma4 = v.findViewById(R.id.perma4);
                        Picasso.get().load(permanentImgs[3]).into(perma4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("language", Preferences.readLanguage());
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}